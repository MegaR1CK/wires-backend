package com.wires.api.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

fun installFirebase() {
    val credentialsString = System.getenv("FIREBASE_CREDENTIALS")
    val credentialsStream = credentialsString.byteInputStream(Charsets.UTF_8)
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(credentialsStream))
        .build()
    FirebaseApp.initializeApp(options)
}
