package com.wires.api.routing.controllers

import com.wires.api.API_VERSION
import com.wires.api.di.inject
import com.wires.api.extensions.receiveBodyOrException
import com.wires.api.extensions.respondEmpty
import com.wires.api.routing.requestparams.PushTokenRegisterParams
import com.wires.api.service.DevicesService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

private const val DEVICE_PATH = "$API_VERSION/device"
private const val DEVICE_REGISTER_TOKEN_PATH = "$DEVICE_PATH/register_push_token"

fun Routing.devicesController() {

    val devicesService: DevicesService by inject()

    /** Регистрация токена девайса для получения уведомлений */
    post(DEVICE_REGISTER_TOKEN_PATH) {
        val params = call.receiveBodyOrException<PushTokenRegisterParams>()
        devicesService.registerPushToken(params)
        call.respondEmpty(HttpStatusCode.OK)
    }
}
