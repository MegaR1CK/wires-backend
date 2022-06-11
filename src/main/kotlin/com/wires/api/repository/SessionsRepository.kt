package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.params.RefreshTokenUpdateParams
import com.wires.api.database.params.SessionDeleteParams
import com.wires.api.database.params.SessionInsertParams
import com.wires.api.database.tables.Sessions
import com.wires.api.mappers.SessionsMapper
import com.wires.api.model.Session
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class SessionsRepository : KoinComponent {

    private val sessionsMapper: SessionsMapper by inject()

    suspend fun addSession(params: SessionInsertParams) = dbQuery {
        Sessions.insert { statement ->
            statement[deviceId] = params.deviceId
            statement[userId] = params.userId
            statement[refreshToken] = params.refreshToken
            statement[expiresAt] = params.expiresAt
        }
    }

    suspend fun findSessionByToken(token: String): Session? = dbQuery {
        sessionsMapper.fromRowToModel(Sessions.select { Sessions.refreshToken eq token }.singleOrNull())
    }

    suspend fun findSessionByIds(deviceId: String, userId: Int) = dbQuery {
        sessionsMapper.fromRowToModel(
            Sessions.select { (Sessions.deviceId eq deviceId) and (Sessions.userId eq userId) }.singleOrNull()
        )
    }

    suspend fun updateRefreshToken(params: RefreshTokenUpdateParams) = dbQuery {
        Sessions.update({ Sessions.refreshToken eq params.oldRefreshToken }) { statement ->
            statement[refreshToken] = params.newRefreshToken
            statement[expiresAt] = params.newExpiresAt
        }
    }

    suspend fun deleteSession(params: SessionDeleteParams) = dbQuery {
        Sessions.deleteWhere { (Sessions.deviceId eq params.deviceId) and (Sessions.userId eq params.userId) }
    }
}
