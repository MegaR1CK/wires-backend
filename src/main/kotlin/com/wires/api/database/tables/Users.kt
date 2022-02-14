package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

// TODO: add avatar url
object Users : IntIdTable() {
    val email = text("email").uniqueIndex()
    val username = text("username")
    val passwordHash = text("password_hash")
    val passwordSalt = text("password_salt")
}
