package com.wires.api.model

open class UserPreview(
    open val id: Int,
    open val username: String,
    open val avatar: Image?,
    open val firstName: String?,
    open val lastName: String?
)
