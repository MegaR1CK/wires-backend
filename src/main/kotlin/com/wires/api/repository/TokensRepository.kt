package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.RefreshTokenEntity
import com.wires.api.database.params.RefreshTokenDeleteParams
import com.wires.api.database.params.RefreshTokenInsertParams
import com.wires.api.database.params.RefreshTokenUpdateParams
import com.wires.api.database.tables.RefreshTokens
import com.wires.api.mappers.TokensMapper
import com.wires.api.model.RefreshToken
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class TokensRepository : KoinComponent {

    private val tokensMapper: TokensMapper by inject()

    suspend fun insertRefreshToken(params: RefreshTokenInsertParams) = dbQuery {
        RefreshTokens.insert { statement ->
            statement[id] = params.refreshToken
            statement[userId] = params.userId
            statement[expiresAt] = params.expiresAt
        }
    }

    suspend fun findRefreshToken(token: String): RefreshToken? = dbQuery {
        return@dbQuery RefreshTokenEntity.findById(token)?.let(tokensMapper::fromEntityToModel)
    }

    suspend fun updateRefreshToken(params: RefreshTokenUpdateParams) = dbQuery {
        RefreshTokens.update({ RefreshTokens.id eq params.oldRefreshToken }) { statement ->
            statement[id] = params.newRefreshToken
            statement[expiresAt] = params.newExpiresAt
        }
    }

    suspend fun deleteRefreshToken(params: RefreshTokenDeleteParams) = dbQuery {
        RefreshTokenEntity.findById(params.refreshToken)?.delete()
    }
}
