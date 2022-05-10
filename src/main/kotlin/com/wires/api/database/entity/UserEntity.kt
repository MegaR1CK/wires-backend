package com.wires.api.database.entity

import com.wires.api.database.tables.Users
import io.ktor.server.auth.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(id: EntityID<Int>) : IntEntity(id), Principal {
    companion object : IntEntityClass<UserEntity>(Users)
    var email by Users.email
    var username by Users.username
    var passwordHash by Users.passwordHash
    var passwordSalt by Users.passwordSalt
    var avatar by ImageEntity optionalReferencedOn Users.avatarUrl
    var interests by Users.interests
    var firstName by Users.firstName
    var lastName by Users.lastName
}
