package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.User
import com.wires.api.database.params.InsertUserParams
import com.wires.api.database.tables.Users
import com.wires.api.extensions.toUser
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class UserRepository {

    suspend fun registerUser(userParams: InsertUserParams) = dbQuery {
        return@dbQuery Users.insert { statement ->
            statement[username] = userParams.username
            statement[email] = userParams.email
            statement[passwordHash] = userParams.passwordHash
            statement[passwordSalt] = userParams.passwordSalt
        }
    }

    suspend fun findUserByEmail(email: String): User? = dbQuery {
        Users.select { Users.email.eq(email) }.map { it.toUser() }.singleOrNull()
    }
}
