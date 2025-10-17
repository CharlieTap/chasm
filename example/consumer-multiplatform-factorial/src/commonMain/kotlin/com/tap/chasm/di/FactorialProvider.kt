package com.tap.chasm.di

import com.tap.chasm.binary.binaryLoaderFactory
import com.test.chasm.FactorialService
import com.test.chasm.FactorialServiceImpl
import com.test.chasm.StringService
import com.test.chasm.StringServiceImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface FactorialProvider {
    @Provides
    fun factorialService(): FactorialService {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("factorial.wasm")
        return FactorialServiceImpl(bytes)
    }

    @Provides
    fun stringService(): StringService {
        val loader = binaryLoaderFactory()
        val bytes = loader.load("truncate.wasm")
        return StringServiceImpl(bytes)
    }
}
