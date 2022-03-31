package com.wires.api.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.wires.api.model.User
import org.koin.core.annotation.Single
import java.util.*

@Single
class JwtService {

    companion object {
        private const val ISSUER_NAME = "wires-api"
        private const val SUBJECT_NAME = "Authorization"
    }

    private val algorithm = Algorithm.HMAC512(System.getenv("JWT_SECRET"))

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER_NAME)
        .build()

    fun generateToken(user: User): String = JWT.create()
        .withSubject(SUBJECT_NAME)
        .withIssuer(ISSUER_NAME)
        .withClaim("id", user.id)
        .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000 * 24))
        .sign(algorithm)
}
