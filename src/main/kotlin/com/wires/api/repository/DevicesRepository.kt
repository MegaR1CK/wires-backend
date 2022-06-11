package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.params.DeviceInsertParams
import com.wires.api.database.tables.Devices
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent

@Single
class DevicesRepository : KoinComponent {
    suspend fun addDevice(params: DeviceInsertParams) = dbQuery {
        Devices.insert { statement ->
            statement[id] = params.id
            statement[userId] = params.userId
            statement[name] = params.name
            statement[pushToken] = params.pushToken
        }
    }
}
