package com.wires.api.database.entity

import com.wires.api.database.tables.Devices
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DeviceEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, DeviceEntity>(Devices)
    var name by Devices.name
    var pushToken by Devices.pushToken
}
