package com.tap.chasm.di

import com.test.chasm.TestService
import com.test.chasm.TestServiceImpl
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Module
import java.io.FileNotFoundException

@Module
@InstallIn(SingletonComponent::class)
class JvmBindingModule {
    @Singleton
    @Provides
    fun testService(): TestService {
        val bytes = JvmBindingModule::class.java.classLoader.getResourceAsStream("test.wasm")?.use {
            it.readBytes()
        } ?: throw FileNotFoundException("Could not find resource 'fibonacci.wasm' on the classpath")
        return TestServiceImpl(bytes)
    }
}
