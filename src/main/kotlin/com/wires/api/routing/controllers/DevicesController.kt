package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.getUserId
import com.wires.api.extensions.receiveBodyOrException
import com.wires.api.extensions.respondEmpty
import com.wires.api.routing.requestparams.DeviceRegisterParams
import com.wires.api.service.DevicesService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

private const val DEVICE_PATH = "$API_VERSION/device"
private const val DEVICE_REGISTER_PATH = "$DEVICE_PATH/register"

fun Routing.devicesController() {

    val devicesService: DevicesService by inject()

    authenticate("jwt") {
        /** Регистрация девайса для получения уведомлений */
        post(DEVICE_REGISTER_PATH) {
            val params = call.receiveBodyOrException<DeviceRegisterParams>()
            devicesService.registerDevice(call.getUserId(), params)
            call.respondEmpty(HttpStatusCode.Created)
        }
    }
}
