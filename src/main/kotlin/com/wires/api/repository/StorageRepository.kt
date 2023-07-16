package com.wires.api.repository

import com.wires.api.di.inject
import com.wires.api.model.Image
import com.wires.api.model.ImageSize
import io.imagekit.sdk.ImageKit
import io.imagekit.sdk.models.FileCreateRequest
import org.koin.core.annotation.Single

@Single
class StorageRepository {

    companion object {
        private const val IMAGE_NAME = "image"
    }

    private val imageKit: ImageKit by inject()

    fun uploadFile(byteArray: ByteArray): Image? {
        val request = FileCreateRequest(byteArray, IMAGE_NAME)
        return try {
            val result = imageKit.upload(request)
            Image(result.url, ImageSize(result.width, result.height))
        } catch (ex: Exception) {
            null
        }
    }
}
