package com.wires.api.authentication

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
