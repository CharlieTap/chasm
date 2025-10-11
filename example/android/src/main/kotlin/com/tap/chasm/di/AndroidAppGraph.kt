package com.tap.chasm.di

import android.app.Activity
import android.app.Application
import android.content.Context
import com.test.chasm.producer.ProducerService
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

interface AppGraph:
    FactorialProvider,
    FibonacciProvider,
    TestProvider

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : AppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides application: Application): AndroidAppGraph
    }

    @Provides fun provideContext(application: Application): Context = application

    @Multibinds
    val activityProviders: Map<KClass<out Activity>, Provider<Activity>>

    // Metro plugin currently prevents applying on a k/wasm project
    @Provides
    fun provideProducerService(): ProducerService = ProducerProvider.provideProducerService()
}


