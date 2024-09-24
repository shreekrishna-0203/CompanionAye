package com.example.companionaye.utils

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

fun uploadVideo(uri: Uri, folderName: String, callback: (String) -> Unit) {
    val fileName = UUID.randomUUID().toString() + ".mp4"
    val refStorage = Firebase.storage.reference.child("$folderName/$fileName")

    refStorage.putFile(uri)
        .addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUrl ->
                callback(downloadUrl.toString())
            }
        }
        .addOnFailureListener { exception ->
            exception.printStackTrace()
            callback("")
        }
}