package com.wires.api.utils

import at.favre.lib.crypto.bcrypt.BCrypt
import org.koin.core.annotation.Single
import java.security.SecureRandom

@Single
class Cryptor {

    companion object {
        private const val SALT_SIZE = 16
        private const val BCRYPT_HASH_ITERATIONS_COUNT = 8
    }

    fun generateSalt(): String {
        return ByteArray(SALT_SIZE).apply { SecureRandom().nextBytes(this) }.toHexString()
    }

    fun getBcryptHash(string: String?, salt: String?): String? {
        if (string == null || salt == null) return null
        return BCrypt
            .withDefaults()
            .hash(BCRYPT_HASH_ITERATIONS_COUNT, salt.toHexByteArray(), string.toByteArray())
            .decodeToString()
    }

    fun checkBcryptHash(string: String, salt: String, hash: String): Boolean {
        return getBcryptHash(string, salt) == hash
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

    private fun String.toHexByteArray() = chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
