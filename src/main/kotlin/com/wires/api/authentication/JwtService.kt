package com.wires.api.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

@Single
class JwtService : KoinComponent {

    companion object {
        private const val ISSUER_NAME = "wires-api"
        private const val SUBJECT_NAME = "Authorization"
        private val REFRESH_TOKEN_LIFETIME = TimeUnit.DAYS.toMillis(30)
        private val ACCESS_TOKEN_LIFETIME = TimeUnit.MINUTES.toMillis(60)
    }

    private val algorithm = Algorithm.HMAC512(System.getenv("JWT_SECRET"))

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER_NAME)
        .build()

    fun generateTokenPair(userId: Int): GeneratedTokens {
        return GeneratedTokens(
            accessToken = generateAccessToken(userId),
            refreshToken = UUID.randomUUID().toString(),
            refreshTokenExpiresAt = System.currentTimeMillis() + REFRESH_TOKEN_LIFETIME
        )
    }

    private fun generateAccessToken(userId: Int): String = JWT.create()
        .withSubject(SUBJECT_NAME)
        .withIssuer(ISSUER_NAME)
        .withClaim("id", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFETIME))
        .sign(algorithm)
}
