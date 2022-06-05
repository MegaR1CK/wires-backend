package com.wires.api.model

data class Channel(
    val id: Int,
    val name: String?,
    val type: String,
    val image: Image?,
    val members: List<UserPreview>
) {
    fun containsUser(userId: Int) = members.map { it.id }.contains(userId)
}
