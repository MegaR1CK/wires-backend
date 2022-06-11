package com.wires.api.database.tables

import org.jetbrains.exposed.sql.Table

object Sessions : Table("sessions") {
    val deviceId = reference("device_id", Devices)
    val userId = reference("user_id", Users)
    val refreshToken = text("refresh_token")
    val expiresAt = long("expires_at")
    override val primaryKey = PrimaryKey(deviceId, userId)
}
