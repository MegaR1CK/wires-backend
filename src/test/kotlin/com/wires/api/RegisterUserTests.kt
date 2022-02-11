package com.wires.api

import com.wires.api.routing.API_VERSION
import com.wires.api.routing.requestparams.LoginUserParams
import com.wires.api.routing.requestparams.RegisterUserParams
import com.wires.api.routing.respondmodels.Token
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class RegisterUserTests {

    companion object {
        private const val REGISTER_PATH = "$API_VERSION/user/register"
        private const val LOGIN_PATH = "$API_VERSION/user/login"
    }

    @Test
    fun whenCorrectParams_registerSuccess() = testApplication {
        val response = client.post(REGISTER_PATH) {
            setBody(
                RegisterUserParams(
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
                RegisterUserParams(
                    username = "testUsername",
                    email = testEmail,
                    passwordHash = testPasswordHash
                ).toJson()
            )
            contentType(ContentType.Application.Json)
        }
        val response = client.post(LOGIN_PATH) {
            setBody(
                LoginUserParams(
                    email = testEmail,
                    passwordHash = testPasswordHash
                ).toJson()
            )
            contentType(ContentType.Application.Json)
        }
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText().toModel<Token>() shouldNotBe null
    }
}
