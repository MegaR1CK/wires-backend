package com.wires.api.routing.routes

import com.wires.api.extensions.handleRoute
import com.wires.api.routing.API_VERSION
import com.wires.api.routing.respondmodels.ImageUrlResponse
import com.wires.api.utils.Cryptor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.io.File
import javax.imageio.ImageIO

private const val STORAGE_PATH = "$API_VERSION/storage"
private const val IMAGE_UPLOAD_PATH = "$STORAGE_PATH/upload_image"
private const val IMAGE_GET_PATH = "$STORAGE_PATH/image/"
private const val CONTENT_TYPE_HEADER_NAME = "Content-Type"
private const val CONTENT_TYPE_DIVIDER = "/"
private const val STORAGE_LOCAL_PATH = "src/main/resources/static/images/"
private const val HTTP_PREFIX = "http://"

fun Application.registerStorageRoutes(cryptor: Cryptor) {
    routing {
        uploadImage(cryptor)
        getImage()
    }
}

fun Route.uploadImage(cryptor: Cryptor) = handleRoute(IMAGE_UPLOAD_PATH, HttpMethod.Post) { scope, call ->
    scope.launch {
        val format = call.request.headers[CONTENT_TYPE_HEADER_NAME]?.split(CONTENT_TYPE_DIVIDER)?.last()
        withContext(Dispatchers.IO) {
            runCatching {
                val bytes = call.receiveStream().readBytes()
                val fileName = "${cryptor.getMd5BytesHash(bytes)}.$format"
                val inputStream = ByteArrayInputStream(bytes)
                val config = environment?.config
                ImageIO.write(ImageIO.read(inputStream), format, File(STORAGE_LOCAL_PATH + fileName))
                val imageUrl = StringBuilder()
                    .append(HTTP_PREFIX)
                    .append(config?.host)
                    .append(":")
                    .append(config?.port)
                    .append(IMAGE_GET_PATH)
                    .append(fileName)
                    .toString()
                call.respond(HttpStatusCode.OK, ImageUrlResponse(imageUrl))
            }
        }
    }
}

fun Route.getImage() {
    static(IMAGE_GET_PATH) {
        resources("static/images")
    }
}
