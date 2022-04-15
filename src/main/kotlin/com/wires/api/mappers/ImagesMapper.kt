package com.wires.api.mappers

import com.wires.api.database.entity.ImageEntity
import com.wires.api.model.Image
import com.wires.api.model.ImageSize
import com.wires.api.routing.respondmodels.ImageResponse
import com.wires.api.routing.respondmodels.ImageSizeResponse
import org.koin.core.annotation.Single

@Single
class ImagesMapper {

    fun fromEntityToModel(imageEntity: ImageEntity): Image {
        return Image(
            url = imageEntity.id.value,
            size = ImageSize(imageEntity.width, imageEntity.height)
        )
    }

    fun fromModelToResponse(image: Image): ImageResponse {
        return ImageResponse(
            url = image.url,
            size = fromModelToResponse(image.size)
        )
    }

    fun fromModelToResponse(imageSize: ImageSize): ImageSizeResponse {
        return ImageSizeResponse(
            width = imageSize.width,
            height = imageSize.height
        )
    }
}
