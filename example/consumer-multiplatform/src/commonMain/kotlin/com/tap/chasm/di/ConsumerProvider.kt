package com.tap.chasm.di

import com.tap.chasm.binary.binaryLoaderFactory
import com.test.chasm.FactorialService
import com.test.chasm.InteropService
import com.test.chasm.StringService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import com.test.chasm.factorialService as createFactorialService
import com.test.chasm.interopService as createInteropService
import com.test.chasm.stringService as createStringService

@ContributesTo(AppScope::class)
interface ConsumerProvider {
    @Provides
    fun factorialService(): FactorialService = runBlocking {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("factorial.wasm")
        createFactorialService(bytes)
    }

    @Provides
    fun stringService(): StringService = runBlocking {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("truncate.wasm")
        createStringService(bytes)
    }

    @Provides
    fun interopService(): InteropService = runBlocking {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("interop.wasm")
        createInteropService(bytes)
    }
}
