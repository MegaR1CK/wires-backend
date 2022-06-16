package com.wires.api.model

data class Channel(
    val id: Int,
    val name: String?,
    val type: ChannelType,
    val image: Image?,
    val members: List<UserPreview>,
    val ownerId: Int
) {
    fun containsUser(userId: Int) = members.map { it.id }.contains(userId)
}
