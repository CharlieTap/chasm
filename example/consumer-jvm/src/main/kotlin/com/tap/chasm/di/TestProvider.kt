package com.tap.chasm.di

import at.released.weh.host.EmbedderHost
import com.test.chasm.TestService
import com.test.chasm.testService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.io.FileNotFoundException

@ContributesTo(AppScope::class)
interface TestProvider {
    @Provides
    @SingleIn(AppScope::class)
    fun provideWasiHost(): EmbedderHost = EmbedderHost()

    @Provides
    fun provideTestService(wasiHost: EmbedderHost): TestService {
        val bytes = TestProvider::class.java.classLoader.getResourceAsStream("test.wasm")?.use {
            it.readBytes()
        } ?: throw FileNotFoundException("Could not find resource 'test.wasm' on the classpath")
        return testService(bytes, wasiHost)
    }
}
