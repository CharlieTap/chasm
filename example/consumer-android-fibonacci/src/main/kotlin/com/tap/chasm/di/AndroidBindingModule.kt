package com.tap.chasm.di

import android.content.Context
import android.content.res.AssetManager
import com.test.chasm.FibonacciService
import com.test.chasm.FibonacciServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AndroidBindingModule {
    @Singleton
    @Provides
    fun fibonacciService(
        @ApplicationContext context: Context,
    ): FibonacciService {
        return FibonacciServiceImpl(context.assets.open("fibonacci.wasm").readBytes())
    }
}
