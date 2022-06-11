package com.wires.api.service

import com.wires.api.database.params.DeviceInsertParams
import com.wires.api.repository.DevicesRepository
import com.wires.api.routing.requestparams.DeviceRegisterParams
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class DevicesService : KoinComponent {

    private val devicesRepository: DevicesRepository by inject()

    suspend fun registerDevice(userId: Int, params: DeviceRegisterParams) {
        devicesRepository.addDevice(DeviceInsertParams(params.id, userId, params.name, params.pushToken))
    }
}
