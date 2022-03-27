package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.User
import com.wires.api.database.params.UserInsertParams
import com.wires.api.database.params.UserUpdateParams
import com.wires.api.database.tables.Users
import com.wires.api.extensions.toUser
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single
class UserRepository {

    suspend fun registerUser(userParams: UserInsertParams) = dbQuery {
        Users.insert { statement ->
            statement[username] = userParams.username
            statement[email] = userParams.email
            statement[passwordHash] = userParams.passwordHash
            statement[passwordSalt] = userParams.passwordSalt
        }
    }

    suspend fun findUserByEmail(email: String): User? = dbQuery {
        Users
            .select { Users.email.eq(email) }
            .map { it.toUser() }
            .singleOrNull()
    }

    suspend fun findUserById(id: Int): User? = dbQuery {
        Users
            .select { Users.id.eq(id) }
            .map { it.toUser() }
            .singleOrNull()
    }

    suspend fun updateUser(updateParams: UserUpdateParams) = dbQuery {
        Users.update({ Users.id.eq(updateParams.id) }) {
            with(updateParams) {
                username?.let { name -> it[Users.username] = name }
                email?.let { mail -> it[Users.email] = mail }
                passwordHash?.let { hash -> it[Users.passwordHash] = hash }
                passwordSalt?.let { salt -> it[Users.passwordSalt] = salt }
                passwordSalt?.let { salt -> it[Users.passwordSalt] = salt }
                avatarUrl?.let { url -> it[Users.avatarUrl] = url }
            }
        }
    }

    suspend fun getUsersList(usersIds: List<Int>) = dbQuery {
        Users
            .select { Users.id.inList(usersIds) }
            .mapNotNull { it.toUser() }
    }
}
