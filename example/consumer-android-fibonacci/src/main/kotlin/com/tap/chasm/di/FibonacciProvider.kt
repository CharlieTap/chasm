package com.tap.chasm.di

import android.content.Context
import com.test.chasm.FibonacciService
import com.test.chasm.FibonacciServiceImpl
import dev.zacsweers.metro.Provides

interface FibonacciProvider {
    @Provides
    fun provideFibonacciService(context: Context): FibonacciService =
        FibonacciServiceImpl(context.assets.open("fibonacci.wasm").readBytes())
}
