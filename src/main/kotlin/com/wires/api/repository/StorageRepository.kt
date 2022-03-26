package com.wires.api.repository

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.koin.core.annotation.Single

@Single
class StorageRepository(private val cloudinary: Cloudinary) {

    companion object {
        private const val SECURE_URL_KEY = "secure_url"
    }

    fun uploadFile(byteArray: ByteArray): String? {
        return cloudinary.uploader().upload(byteArray, ObjectUtils.asMap())[SECURE_URL_KEY] as? String
    }
}
