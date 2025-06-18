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
class LibModule {

    @Singleton
    @Provides
    fun assetManager(
        @ApplicationContext context: Context,
    ): AssetManager {
        return context.assets
    }

    @Singleton
    @Provides
    fun fibonacciService(
        assetManager: AssetManager,
    ): FibonacciService {
        return FibonacciServiceImpl(assetManager.open("fibonacci.wasm").readBytes())
    }
}
