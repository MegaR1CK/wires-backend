package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.UserEntity
import com.wires.api.database.params.PasswordUpdateParams
import com.wires.api.database.params.UserInsertParams
import com.wires.api.database.params.UserUpdateParams
import com.wires.api.database.tables.Users
import com.wires.api.extensions.toSeparatedString
import com.wires.api.mappers.UserMapper
import com.wires.api.model.User
import com.wires.api.model.UserPreview
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.update
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

    suspend fun findUserByUsername(username: String) = dbQuery {
        UserEntity
            .find { Users.username eq username }
            .firstOrNull()
            ?.let(userMapper::fromEntityToModel)
    }

    suspend fun findUsersByNamePart(query: String): List<UserPreview> = dbQuery {
        UserEntity
            .find {
                (Users.username like "$query%") or
                    (Users.firstName like "$query%") or
                    (Users.lastName like "$query%")
            }
            .map(userMapper::fromEntityToPreviewModel)
    }

    suspend fun updateUser(updateParams: UserUpdateParams) = dbQuery {
        Users.update({ Users.id eq updateParams.id }) { statement ->
            with(updateParams) {
                username?.let { statement[Users.username] = it }
                email?.let { statement[Users.email] = it }
                avatarUrl?.let { statement[Users.avatarUrl] = it }
                interests?.let { statement[Users.interests] = it.toSeparatedString() }
                firstName?.let { name -> statement[Users.firstName] = name.takeIf { it.isNotEmpty() } }
                lastName?.let { name -> statement[Users.lastName] = name.takeIf { it.isNotEmpty() } }
            }
        }
    }

    suspend fun changeUserPassword(passwordUpdateParams: PasswordUpdateParams) = dbQuery {
        Users.update({ Users.id eq passwordUpdateParams.id }) { statement ->
            statement[passwordSalt] = passwordUpdateParams.newPasswordSalt
            statement[passwordHash] = passwordUpdateParams.newPasswordHash
        }
    }
}
