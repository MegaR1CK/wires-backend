package com.wires.api.service

import com.wires.api.database.params.DeviceInsertParams
import com.wires.api.repository.DevicesRepository
import com.wires.api.routing.requestparams.PushTokenRegisterParams
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class DevicesService : KoinComponent {

    private val devicesRepository: DevicesRepository by inject()

    suspend fun registerPushToken(params: PushTokenRegisterParams) {
        val device = devicesRepository.findDeviceById(params.id)
        if (device != null) {
            devicesRepository.updatePushToken(device.id, params.pushToken)
        } else {
            devicesRepository.addDevice(DeviceInsertParams(params.id, params.name, params.pushToken))
        }
    }
}
