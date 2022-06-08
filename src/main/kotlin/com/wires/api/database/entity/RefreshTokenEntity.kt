package com.wires.api.database.entity

import com.wires.api.database.tables.RefreshTokens
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RefreshTokenEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, RefreshTokenEntity>(RefreshTokens)
    var userId by RefreshTokens.userId
    var expiresAt by RefreshTokens.expiresAt
}
