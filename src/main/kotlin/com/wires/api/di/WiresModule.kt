package com.wires.api.di

import com.cloudinary.Cloudinary
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.wires.api")
class WiresModule {

    @Single
    fun provideCloudinary() = Cloudinary()

    @Single
    @ExperimentalSerializationApi
    fun provideSerializer() = Json {
        explicitNulls = false
    }
}
