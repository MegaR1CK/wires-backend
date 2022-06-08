package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IdTable

object RefreshTokens : IdTable<String>("refresh_tokens") {
    override val id = text("refresh_token").entityId()
    val userId = reference("user_id", Users)
    val expiresAt = long("expires_at")
}
