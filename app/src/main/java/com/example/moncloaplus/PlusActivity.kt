package com.example.moncloaplus

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlusActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Uncomment the following line if you want to run
        // against the Firebase Local Emulator Suite:
        //configureFirebaseServices()

        setContent { PlusApp() }
    }
}

private fun configureFirebaseServices() {
    if (BuildConfig.DEBUG) {
        Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
        Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
    }
}