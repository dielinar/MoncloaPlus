package com.example.moncloaplus.model.service.impl

import android.net.Uri
import android.util.Log
import com.example.moncloaplus.model.Fix
import com.example.moncloaplus.model.FixState
import com.example.moncloaplus.model.UploadResult
import com.example.moncloaplus.model.service.FixService
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

    override suspend fun getFix(fixId: String): Fix? {
        return try {
            val doc = fixesCollection.document(fixId).get().await()
            val fix = doc.toObject(Fix::class.java)?.copy(id = doc.id)

            fix?.let {
                val userId = doc.reference.parent.parent?.id
                if (userId != null) {
                    val user = storageService.getUser(userId)
                    it.owner = user
                }

                if (it.imagen.path.isNotEmpty()) {
                    val imageUrl = getImageUrl(it.imagen.path)
                    it.imagen = it.imagen.copy(url = imageUrl)
                }
            }
            fix
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener el arreglo", e)
            null
        }
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
