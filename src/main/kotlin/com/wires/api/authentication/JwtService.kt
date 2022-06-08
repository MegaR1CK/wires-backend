package com.wires.api.authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.wires.api.database.params.RefreshTokenInsertParams
import com.wires.api.database.params.RefreshTokenUpdateParams
import com.wires.api.repository.TokensRepository
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date
import java.util.UUID
import java.util.concurrent.TimeUnit

@Single
class JwtService : KoinComponent {

    companion object {
        private const val ISSUER_NAME = "wires-api"
        private const val SUBJECT_NAME = "Authorization"
        private val REFRESH_TOKEN_LIFETIME = TimeUnit.DAYS.toMillis(30)
        private val ACCESS_TOKEN_LIFETIME = TimeUnit.MINUTES.toMillis(10)
    }

    private val algorithm = Algorithm.HMAC512(System.getenv("JWT_SECRET"))
    private val tokensRepository: TokensRepository by inject()

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER_NAME)
        .build()

    suspend fun generateTokenPair(userId: Int, oldRefreshToken: String? = null): TokenPair {
        val accessToken = generateAccessToken(userId)
        val refreshToken = UUID.randomUUID().toString()
        val refreshTokenExpiresAt = System.currentTimeMillis() + REFRESH_TOKEN_LIFETIME
        if (oldRefreshToken == null) {
            tokensRepository.insertRefreshToken(
                RefreshTokenInsertParams(
                    refreshToken = refreshToken,
                    userId = userId,
                    expiresAt = refreshTokenExpiresAt
                )
            )
        } else {
            tokensRepository.updateRefreshToken(
                RefreshTokenUpdateParams(
                    oldRefreshToken = oldRefreshToken,
                    newRefreshToken = refreshToken,
                    newExpiresAt = refreshTokenExpiresAt
                )
            )
        }
        return TokenPair(accessToken, refreshToken)
    }

    private fun generateAccessToken(userId: Int): String = JWT.create()
        .withSubject(SUBJECT_NAME)
        .withIssuer(ISSUER_NAME)
        .withClaim("id", userId)
        .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_LIFETIME))
        .sign(algorithm)
}
