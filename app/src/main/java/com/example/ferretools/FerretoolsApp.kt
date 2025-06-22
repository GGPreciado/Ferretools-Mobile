package com.example.ferretools

import android.app.Application
import com.google.firebase.FirebaseApp

class FerretoolsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 