package com.tap.chasm.di

import com.test.chasm.TestService
import com.test.chasm.TestServiceImpl
import dev.zacsweers.metro.Provides
import java.io.FileNotFoundException

interface TestProvider {
    @Provides
    fun provideTestService(): TestService {
        val bytes = TestProvider::class.java.classLoader.getResourceAsStream("test.wasm")?.use {
            it.readBytes()
        } ?: throw FileNotFoundException("Could not find resource 'fibonacci.wasm' on the classpath")
        return TestServiceImpl(bytes)
    }
}
