package com.wires.api.service

import com.wires.api.database.params.DeviceInsertParams
import com.wires.api.repository.DevicesRepository
import com.wires.api.routing.DeviceExistsException
import com.wires.api.routing.requestparams.DeviceRegisterParams
import com.wires.api.routing.requestparams.PushTokenUpdateParams
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class DevicesService : KoinComponent {

    private val devicesRepository: DevicesRepository by inject()

    suspend fun registerDevice(params: DeviceRegisterParams) {
        if (devicesRepository.findDeviceById(params.id) != null) throw DeviceExistsException()
        devicesRepository.addDevice(DeviceInsertParams(params.id, params.name))
    }

    suspend fun updatePushToken(params: PushTokenUpdateParams) {
        devicesRepository.updatePushToken(params.deviceId, params.pushToken)
    }
}
