package com.tap.chasm.di

import com.tap.chasm.binary.binaryLoaderFactory
import com.test.chasm.FactorialService
import com.test.chasm.FactorialServiceImpl
import com.test.chasm.InteropService
import com.test.chasm.InteropServiceImpl
import com.test.chasm.StringService
import com.test.chasm.StringServiceImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface ConsumerProvider {
    @Provides
    fun factorialService(): FactorialService = runBlocking {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("factorial.wasm")
        FactorialServiceImpl.create(bytes)
    }

    @Provides
    fun stringService(): StringService = runBlocking {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("truncate.wasm")
        StringServiceImpl.create(bytes)
    }

    @Provides
    fun interopService(): InteropService = runBlocking {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("interop.wasm")
        InteropServiceImpl.create(bytes)
    }
}
