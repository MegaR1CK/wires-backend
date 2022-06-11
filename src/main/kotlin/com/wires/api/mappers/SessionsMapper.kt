package com.wires.api.mappers

import com.wires.api.database.tables.Sessions
import com.wires.api.model.Session
import org.jetbrains.exposed.sql.ResultRow
import org.koin.core.annotation.Single

@Single
class SessionsMapper {

    /** Мапим ряд вручную, так как Exposed DAO не поддерживает составные первичные ключи */
    fun fromRowToModel(resultRow: ResultRow?) = resultRow?.let { row ->
        Session(
            deviceId = row[Sessions.deviceId].value,
            userId = row[Sessions.userId].value,
            refreshToken = row[Sessions.refreshToken],
            expiresAt = row[Sessions.expiresAt]
        )
    }
}
