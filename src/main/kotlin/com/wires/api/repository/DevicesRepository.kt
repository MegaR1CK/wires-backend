package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.DeviceEntity
import com.wires.api.database.params.DeviceInsertParams
import com.wires.api.database.tables.Devices
import com.wires.api.database.tables.Sessions
import com.wires.api.mappers.DevicesMapper
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class DevicesRepository : KoinComponent {

    private val devicesMapper: DevicesMapper by inject()

    suspend fun addDevice(params: DeviceInsertParams) = dbQuery {
        Devices.insert { statement ->
            statement[id] = params.id
            statement[name] = params.name
            statement[pushToken] = params.pushToken
        }
    }

    suspend fun updatePushToken(deviceId: String, pushToken: String?) = dbQuery {
        DeviceEntity.findById(deviceId)?.pushToken = pushToken
    }

    suspend fun findDeviceById(id: String) = dbQuery {
        DeviceEntity.findById(id)?.let { devicesMapper.fromEntityToModel(it) }
    }

    suspend fun getUsersDevices(usersIds: List<Int>) = dbQuery {
        DeviceEntity.wrapRows(Sessions.innerJoin(Devices).select { Sessions.userId inList usersIds }).map(devicesMapper::fromEntityToModel)
    }
}
