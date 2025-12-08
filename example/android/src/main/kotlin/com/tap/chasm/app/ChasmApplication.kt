package com.tap.chasm.app

import android.app.Application
import com.tap.chasm.di.AppGraph
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class ChasmApplication : Application(), MetroApplication {
    val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }
    override val appComponentProviders: MetroAppComponentProviders
        get() = appGraph
}
