package com.wires.api.mappers

import com.wires.api.database.entity.RefreshTokenEntity
import com.wires.api.model.RefreshToken
import org.koin.core.annotation.Single

@Single
class TokensMapper {
    fun fromEntityToModel(entity: RefreshTokenEntity): RefreshToken {
        return RefreshToken(
            refreshToken = entity.id.value,
            userId = entity.userId.value,
            expiresAt = entity.expiresAt
        )
    }
}
