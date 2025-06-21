package com.tap.chasm.di

import com.test.chasm.FactorialService
import com.test.chasm.FactorialServiceImpl
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Module
import java.io.FileNotFoundException

@Module
@InstallIn(SingletonComponent::class)
class MultiplatformJvmBindingModule {
    @Singleton
    @Provides
    fun factorialService(): FactorialService {
        val bytes = MultiplatformJvmBindingModule::class.java.classLoader.getResourceAsStream("factorial.wasm")?.use {
            it.readBytes()
        } ?: throw FileNotFoundException("Could not find resource 'factorial.wasm' on the classpath")
        return FactorialServiceImpl(bytes)
    }
}
