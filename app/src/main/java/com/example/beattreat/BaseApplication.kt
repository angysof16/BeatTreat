package com.example.beattreat

import android.app.Application
import android.util.Log
import com.example.beattreat.util.FirestoreSeedHelper
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class BaseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        // FirebaseApp.initializeApp(this) //  inicializa Firebase

        if (BuildConfig.DEBUG) {
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Log.d("FIREBASE", "Emulando Firestore")
            Firebase.auth.useEmulator("10.0.2.2", 9099)
           // SeedHelper.seedUsersAndTweets(Firebase.firestore)
        }
    }

}