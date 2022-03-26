package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.extensions.handleRoute
import com.wires.api.repository.StorageRepository
import com.wires.api.routing.respondmodels.ImageUrlResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val STORAGE_PATH = "$API_VERSION/storage"
private const val IMAGE_UPLOAD_PATH = "$STORAGE_PATH/upload_image"

fun Application.registerStorageRoutes(storageRepository: StorageRepository) {
    routing {
        uploadImage(storageRepository)
    }
}

fun Route.uploadImage(
    storageRepository: StorageRepository
) = handleRoute(IMAGE_UPLOAD_PATH, HttpMethod.Post) { scope, call ->
    scope.launch {
        withContext(Dispatchers.IO) {
            val bytes = call.receiveStream().readBytes()
            call.respond(HttpStatusCode.OK, ImageUrlResponse(storageRepository.uploadFile(bytes)))
        }
    }
}
