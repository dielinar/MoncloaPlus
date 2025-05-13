package com.example.moncloaplus.service.impl

import android.net.Uri
import android.util.Log
import com.example.moncloaplus.data.model.Fix
import com.example.moncloaplus.data.model.FixState
import com.example.moncloaplus.data.model.UploadResult
import com.example.moncloaplus.service.FixService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class FixServiceImpl @Inject constructor(
    db: FirebaseFirestore,
    accService: AccountServiceImpl,
    private val storage: FirebaseStorage,
    private val storageService: StorageServiceImpl
): FixService {

    private val userId = accService.currentUserId

    private val usersCollection = db.collection("users")

    private val fixesCollection: CollectionReference
        get() = userId.let { usersCollection.document(it).collection("fixes") }

    override suspend fun createFix(fix: Fix, imageUri: Uri?): Fix {
        val docRef = fixesCollection.add(fix).await()
        val fixId = docRef.id

        var fixImage = Fix.FixImage()
        if (imageUri != null) {
            val uploadResult = uploadImage(userId, fixId, imageUri)
            fixImage = Fix.FixImage(
                nombreArchivo = uploadResult.fileName,
                url = uploadResult.downloadUrl,
                path = uploadResult.path,
                tamano = uploadResult.size
            )
        }
        val newFix = fix.copy(id = fixId, imagen = fixImage)
        fixesCollection.document(docRef.id).set(newFix).await()

        return newFix
    }

    override suspend fun getUserFixes(state: Int): List<Fix> = coroutineScope {
        try {
            val result = fixesCollection
                .whereEqualTo("estado", FixState.entries[state].name)
                .orderBy("fecha", Query.Direction.DESCENDING)
                .get().await()

            val fixesDeferred = result.documents.mapNotNull { doc ->
                val fix = doc.toObject(Fix::class.java)?.copy(id = doc.id)
                val userId = doc.reference.parent.parent?.id

                if (fix != null && userId != null) {
                    async {
                        val user = storageService.getUser(userId)
                        fix.owner = user

                        if (fix.imagen.path.isNotEmpty()) {
                            val imageUrl = getImageUrl(fix.imagen.path)
                            fix.imagen = fix.imagen.copy(url = imageUrl)
                        }

                        fix
                    }
                } else null
            }

            fixesDeferred.awaitAll()
        } catch(e: Exception) {
            Log.e("Firestore", "Error al obtener los arreglos", e)
            emptyList()
        }
    }

    override suspend fun deleteFix(fix: Fix) {
        try {
            fix.imagen.path.takeIf { it.isNotEmpty() }?.let { imagePath ->
                try {
                    storage.reference.child(imagePath).delete().await()
                    Log.d("Firebase Storage", "Imagen eliminada correctamente: $imagePath")
                } catch (e: Exception) {
                    Log.e("Firebase Storage", "Error al eliminar la imagen", e)
                }
            }
            fixesCollection.document(fix.id).delete().await()

            Log.d("Firestore", "Arreglo eliminado correctamente: ${fix.id}")
        } catch (e: Exception) {
            Log.e("Firestore", "Error al eliminar el arreglo", e)
        }
    }

    override suspend fun getAllFixesByState(state: Int): List<Fix> = coroutineScope {
        try {
            val users = usersCollection.get().await().documents
            val allFixesDeferred = users.map { userDoc ->
                async {
                    val userId = userDoc.id
                    val fixesSnapshot = usersCollection.document(userId)
                        .collection("fixes")
                        .whereEqualTo("estado", FixState.entries[state].name)
                        .orderBy("fecha", Query.Direction.DESCENDING)
                        .get()
                        .await()

                    fixesSnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Fix::class.java)?.copy(id = doc.id)?.also { fix ->
                            val user = storageService.getUser(userId)
                            fix.owner = user
                            if (fix.imagen.path.isNotEmpty()) {
                                fix.imagen = fix.imagen.copy(
                                    url = getImageUrl(fix.imagen.path)
                                )
                            }
                        }

                    }
                }
            }
            allFixesDeferred.awaitAll().flatten()
        } catch(e: Exception) {
            Log.e("Firestore", "Error al obtener todos los arreglos", e)
            emptyList()
        }
    }

    override suspend fun updateFixState(fix: Fix, newState: FixState) {
        try {
            usersCollection.document(fix.owner!!.id)
                .collection("fixes")
                .document(fix.id)
                .update("estado", newState.name)
                .await()

            Log.d("Firestore", "Estado actualizado: ${fix.id} -> $newState")
        } catch(e: Exception) {
            Log.e("Firestore", "Error al actualizar estado del arreglo", e)
        }
    }

    private suspend fun uploadImage(userId: String, fixId: String, uri: Uri): UploadResult {
        val storagePath = "users/$userId/fixes/$fixId"
        val storageRef = storage.reference.child(storagePath)

        val uploadTask = storageRef.putFile(uri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()
        val metadata = uploadTask.metadata
        val size = metadata?.sizeBytes ?: 0

        return UploadResult(
            fileName = fixId,
            downloadUrl = downloadUrl,
            path = storagePath,
            size = size
        )
    }

    private suspend fun getImageUrl(imagePath: String): String {
        return try {
            val storageRef = storage.reference.child(imagePath)
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e("Firebase Storage", "Error al obtener la URL de la imagen", e)
            ""
        }
    }

}
