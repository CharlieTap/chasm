package com.tap.chasm.di

import android.content.Context
import com.test.chasm.FibonacciService
import com.test.chasm.fibonacciService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
interface FibonacciProvider {
    @Provides
    fun provideFibonacciService(context: Context): FibonacciService =
        fibonacciService(context.assets.open("fibonacci.wasm").readBytes())
}
