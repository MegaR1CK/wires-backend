package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.receiveBodyOrException
import com.wires.api.extensions.respondEmpty
import com.wires.api.routing.requestparams.DeviceRegisterParams
import com.wires.api.routing.requestparams.PushTokenUpdateParams
import com.wires.api.service.DevicesService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

private const val DEVICE_PATH = "$API_VERSION/device"
private const val DEVICE_REGISTER_PATH = "$DEVICE_PATH/register"
private const val DEVICE_UPDATE_TOKEN_PATH = "$DEVICE_PATH/update_push_token"

fun Routing.devicesController() {

    val devicesService: DevicesService by inject()

    /** Регистрация девайса для получения уведомлений */
    post(DEVICE_REGISTER_PATH) {
        val params = call.receiveBodyOrException<DeviceRegisterParams>()
        devicesService.registerDevice(params)
        call.respondEmpty(HttpStatusCode.Created)
    }

    authenticate("jwt") {

        /** Добавление/обновление пуш-токена девайса */
        post(DEVICE_UPDATE_TOKEN_PATH) {
            val params = call.receiveBodyOrException<PushTokenUpdateParams>()
            devicesService.updatePushToken(params)
            call.respondEmpty(HttpStatusCode.OK)
        }
    }
}
