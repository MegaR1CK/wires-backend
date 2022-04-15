package com.wires.api.database.entity

import com.wires.api.database.tables.Images
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ImageEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, ImageEntity>(Images)
    var width by Images.width
    var height by Images.height
}
