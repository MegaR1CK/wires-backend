package com.wires.api

import com.wires.api.routing.requestparams.UserLoginParams
import com.wires.api.routing.requestparams.UserRegisterParams
import com.wires.api.routing.respondmodels.TokensResponse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class UserTests {

    companion object {
        private const val REGISTER_PATH = "$API_VERSION/user/register"
        private const val LOGIN_PATH = "$API_VERSION/user/login"
    }

    @Test
    fun whenCorrectParams_registerSuccess() = testApplication {
        val response = client.post(REGISTER_PATH) {
            setBody(
                UserRegisterParams(
                    username = "testUsername",
                    email = getRandomEmail(),
                    passwordHash = "randomHash"
                ).toJson()
            )
            contentType(ContentType.Application.Json)
        }
        response.status shouldBe HttpStatusCode.Created
    }

    @Test
    fun whenCorrectParams_loginSuccess() = testApplication {
        val testEmail = getRandomEmail()
        val testPasswordHash = "randomPasswordHash"
        client.post(REGISTER_PATH) {
            setBody(
                UserRegisterParams(
                    username = "testUsername",
                    email = testEmail,
                    passwordHash = testPasswordHash
                ).toJson()
            )
            contentType(ContentType.Application.Json)
        }
        val response = client.post(LOGIN_PATH) {
            setBody(
                UserLoginParams(
                    email = testEmail,
                    passwordHash = testPasswordHash
                ).toJson()
            )
            contentType(ContentType.Application.Json)
        }
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText().toModel<TokensResponse>() shouldNotBe null
    }
}
