package com.wires.api.database

import com.wires.api.database.tables.* // ktlint-disable no-unused-imports
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object Database {

    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(
                Users, Posts, Comments, Channels, ChannelsMembers,
                Messages, MessagesReaders, Sessions, Devices, Images
            )
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = System.getenv("JDBC_DRIVER")
            jdbcUrl = System.getenv("DATABASE_URL")
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        System.getenv("DB_USER")?.let { username ->
            config.username = username
        }

        System.getenv("DB_PASSWORD")?.let { password ->
            config.password = password
        }
        config.validate()
        return HikariDataSource(config)
    }
}

suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
