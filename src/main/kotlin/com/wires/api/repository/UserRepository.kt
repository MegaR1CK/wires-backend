package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.UserEntity
import com.wires.api.database.params.UserInsertParams
import com.wires.api.database.params.UserUpdateParams
import com.wires.api.database.tables.Users
import com.wires.api.mappers.UserMapper
import com.wires.api.model.User
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class UserRepository : KoinComponent {

    private val userMapper: UserMapper by inject()

    suspend fun registerUser(userParams: UserInsertParams) = dbQuery {
        Users.insert { statement ->
            statement[username] = userParams.username
            statement[email] = userParams.email
            statement[passwordHash] = userParams.passwordHash
            statement[passwordSalt] = userParams.passwordSalt
        }
    }

    suspend fun findUserByEmail(email: String): User? = dbQuery {
        UserEntity
            .find { Users.email eq email }
            .firstOrNull()
            ?.let(userMapper::fromEntityToModel)
    }

    suspend fun findUserById(userId: Int): User? = dbQuery {
        UserEntity
            .findById(userId)
            ?.let(userMapper::fromEntityToModel)
    }

    suspend fun updateUser(updateParams: UserUpdateParams) = dbQuery {
        UserEntity.findById(updateParams.id)?.let { user ->
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
