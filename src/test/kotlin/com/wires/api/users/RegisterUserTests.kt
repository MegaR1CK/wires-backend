package com.wires.api.users

import com.wires.api.getRandomString
import com.wires.api.routing.requestparams.RegisterUserParams
import com.wires.api.toJson
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class RegisterUserTests {

    companion object {
        private const val REGISTER_PATH = "/v1/user/register"
        private const val EMAIL_LENGTH = 7
    }

    @Test
    fun whenCorrectParams_registerSuccess() = testApplication {
        val response = client.post(REGISTER_PATH) {
            setBody(
                RegisterUserParams(
                    username = "testUsername",
                    email = "${getRandomString(EMAIL_LENGTH)}@test.com",
                    passwordHash = "randomHash"
                ).toJson()
            )
            contentType(ContentType.Application.Json)
        }
        response.status shouldBe HttpStatusCode.Created
    }

    fun launchAllRegisterTests() {
        whenCorrectParams_registerSuccess()
    }
}
