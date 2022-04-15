package com.wires.api.model

data class User(
    override val id: Int,
    override val username: String,
    override val avatar: Image?,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val interests: List<String>
) : UserPreview(id, username, avatar)
