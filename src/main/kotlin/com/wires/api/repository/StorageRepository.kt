package com.wires.api.repository

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.wires.api.di.inject
import com.wires.api.model.Image
import com.wires.api.model.ImageSize
import org.koin.core.annotation.Single

@Single
class StorageRepository {

    private val cloudinary: Cloudinary by inject()

    companion object {
        private const val SECURE_URL_KEY = "secure_url"
        private const val WIDTH_KEY = "width"
        private const val HEIGHT_KEY = "height"
    }

    fun uploadFile(byteArray: ByteArray): Image? {
        val imageResponse = cloudinary.uploader().upload(byteArray, ObjectUtils.asMap())
        val imageUrl = imageResponse[SECURE_URL_KEY] as? String
        val imageWidth = imageResponse[WIDTH_KEY] as? Int
        val imageHeight = imageResponse[HEIGHT_KEY] as? Int
        return if (imageUrl != null && imageWidth != null && imageHeight != null) {
            Image(imageUrl, ImageSize(imageWidth, imageHeight))
        } else {
            null
        }
    }
}
