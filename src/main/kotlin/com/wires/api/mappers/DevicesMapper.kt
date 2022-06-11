package com.wires.api.mappers

import com.wires.api.database.entity.DeviceEntity
import com.wires.api.model.Device
import org.koin.core.annotation.Single

@Single
class DevicesMapper {

    fun fromEntityToModel(deviceEntity: DeviceEntity) = Device(
        id = deviceEntity.id.value,
        name = deviceEntity.name,
        pushToken = deviceEntity.pushToken
    )
}
