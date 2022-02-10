package com.wires.api.extensions

import com.wires.api.database.models.User
import com.wires.api.database.tables.Users
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow?.toUser(): User? {
    return this?.let { row ->
        User(
            id = row[Users.id].value,
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            passwordSalt = row[Users.passwordSalt]
        )
    }
}
