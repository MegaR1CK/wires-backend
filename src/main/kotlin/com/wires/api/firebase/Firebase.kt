package com.wires.api.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.FileInputStream

fun installFirebase() {
    val credentialsStream = FileInputStream("firebase-credentials.json")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(credentialsStream))
        .build()
    FirebaseApp.initializeApp(options)
}
