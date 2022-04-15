package com.wires.api.model

data class Channel(
    override val id: Int,
    override val name: String,
    override val image: Image?,
    val members: List<UserPreview>
) : ChannelPreview(id, name, image) {

    fun containsUser(userId: Int) = members.map { it.id }.contains(userId)
}
