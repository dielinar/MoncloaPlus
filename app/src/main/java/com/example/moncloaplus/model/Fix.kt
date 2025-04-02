package com.example.moncloaplus.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude

enum class FixState { PENDING, IN_PROGRESS, FIXED }

data class Fix (
    var id: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val localizacion: String = "",
    val descripcion: String = "",
    val estado: FixState = FixState.PENDING,
    var imagen: FixImage = FixImage(),

    @get:Exclude
    var owner: User? = null
) {
    data class FixImage(
        val nombreArchivo: String = "",
        val url: String = "",
        val path: String = "",
        val tamano: Long = 0
    )
}

/*
https://chat.deepseek.com/a/chat/s/a8d06207-aae6-48b6-acdc-306abb564eb6
 */

/*
Antigua regla Storage:
rules_version = '2';

// Craft rules based on data in your Firestore database
// allow write: if firestore.get(
//    /databases/(default)/documents/users/$(request.auth.uid)).data.isAdmin;
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if false;
    }
  }
}
 */
