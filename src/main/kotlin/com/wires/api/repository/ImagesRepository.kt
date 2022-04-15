package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.params.ImageInsertParams
import com.wires.api.database.tables.Images
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class ImagesRepository : KoinComponent {

    suspend fun addImage(params: ImageInsertParams) = dbQuery {
        Images.insert { statement ->
            statement[id] = params.url
            statement[width] = params.width
            statement[height] = params.height
        }[Images.id]
    }
}
