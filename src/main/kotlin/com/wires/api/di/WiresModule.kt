package com.wires.api.di

import io.imagekit.sdk.ImageKit
import io.imagekit.sdk.utils.Utils
import io.ktor.server.application.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.wires.api")
class WiresModule {

    @Single
    fun provideImageKit(): ImageKit = ImageKit.getInstance().apply {
        config = Utils.getSystemConfig(Application::class.java)
    }

    @Single
    @ExperimentalSerializationApi
    fun provideSerializer() = Json {
        explicitNulls = false
    }
}
