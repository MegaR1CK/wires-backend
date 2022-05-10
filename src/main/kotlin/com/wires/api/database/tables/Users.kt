package com.wires.api.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val email = text("email").uniqueIndex()
    val username = text("username")
    val passwordHash = text("password_hash")
    val passwordSalt = text("password_salt")
    val avatarUrl = reference("avatar_url", Images).nullable()
    val interests = text("interests").nullable()
    val firstName = text("first_name").nullable()
    val lastName = text("last_name").nullable()
}
