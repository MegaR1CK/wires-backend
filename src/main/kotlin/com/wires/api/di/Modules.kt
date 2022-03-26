package com.wires.api.di

import com.cloudinary.Cloudinary
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.wires.api")
class WiresModule {

    @Single
    fun provideCloudinary() = Cloudinary()
}
