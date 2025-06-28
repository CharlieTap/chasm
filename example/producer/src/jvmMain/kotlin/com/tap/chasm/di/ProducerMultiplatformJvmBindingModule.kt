package com.tap.chasm.di


import com.test.chasm.producer.ProducerService
import com.test.chasm.producer.ProducerServiceImpl
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import dagger.Module
import java.io.FileNotFoundException

@Module
@InstallIn(SingletonComponent::class)
class ProducerMultiplatformJvmBindingModule {
    @Singleton
    @Provides
    fun producerService(): ProducerService {
        val bytes = ProducerMultiplatformJvmBindingModule::class.java.classLoader.getResourceAsStream("producer.wasm")?.use {
            it.readBytes()
        } ?: throw FileNotFoundException("Could not find resource 'producer.wasm' on the classpath")
        return ProducerServiceImpl(bytes)
    }
}
