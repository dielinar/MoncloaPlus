package com.example.moncloaplus.model.service.impl

import android.net.Uri
import com.example.moncloaplus.model.Fix
import com.example.moncloaplus.model.UploadResult
import com.example.moncloaplus.model.service.FixService
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FixServiceImpl @Inject constructor(
    private val db: FirebaseFirestore,
    accService: AccountServiceImpl,
    private val storage: FirebaseStorage
): FixService {

    private val userId = accService.currentUserId

    private val usersCollection = db.collection("users")

    private val fixesCollection: CollectionReference
        get() = userId.let { usersCollection.document(it).collection("fixes") }

    override suspend fun addFix(fix: Fix, imageUri: Uri?): Fix {
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

}
