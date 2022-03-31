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

    private const val DATABASE_URL_PATTERN = "(postgres)://([a-z]+):([a-z0-9]+)@(.+):([0-9]{4})/([a-z0-9]+)"
    private const val JDBC_DATABASE_URL_PATTERN = "jdbc:postgresql:[a-z_]+\\?user=[a-z]+&password=[a-z0-9]+"
    private const val JDBC_URL_PREFIX = "jdbc:postgresql://"

    fun init() {
        Database.connect(hikari())
        transaction {
            SchemaUtils.create(Users, Posts, Comments, Channels, ChannelsMembers, Messages)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = System.getenv("JDBC_DRIVER")
            jdbcUrl = databaseUrlToJdbc(System.getenv("DATABASE_URL"))
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

    private fun databaseUrlToJdbc(databaseUrl: String): String? {
        if (databaseUrl.matches(Regex(JDBC_DATABASE_URL_PATTERN))) return databaseUrl
        val databaseUrlMatchResult = Regex(DATABASE_URL_PATTERN).matchEntire(databaseUrl) ?: return null
        val host = databaseUrlMatchResult.groupValues[4]
        val port = databaseUrlMatchResult.groupValues[5]
        val database = databaseUrlMatchResult.groupValues[6]
        val user = databaseUrlMatchResult.groupValues[2]
        val password = databaseUrlMatchResult.groupValues[3]
        return "$JDBC_URL_PREFIX$host:$port/$database?user=$user&password=$password"
    }
}

suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}
