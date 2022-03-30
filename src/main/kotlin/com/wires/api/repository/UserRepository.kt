package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.User
import com.wires.api.database.params.UserInsertParams
import com.wires.api.database.params.UserUpdateParams
import com.wires.api.database.tables.Users
import org.jetbrains.exposed.sql.insert
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

    suspend fun findUserByEmail(email: String) = dbQuery {
        User.find { Users.email eq email }.firstOrNull()
    }

    suspend fun findUserById(userId: Int) = dbQuery {
        User.findById(userId)
    }

    suspend fun updateUser(updateParams: UserUpdateParams) = dbQuery {
        User.findById(updateParams.id)?.let { user ->
            with(updateParams) {
                username?.let { name -> user.username = name }
                email?.let { mail -> user.email = mail }
                passwordHash?.let { hash -> user.passwordHash = hash }
                passwordSalt?.let { salt -> user.passwordSalt = salt }
                avatarUrl?.let { url -> user.avatarUrl = url }
            }
        }
    }
}
