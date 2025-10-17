package com.tap.chasm.app

import android.app.Application
import com.tap.chasm.di.AppGraph
import dev.zacsweers.metro.createGraphFactory

class ChasmApplication : Application() {
    val appGraph by lazy { createGraphFactory<AppGraph.Factory>().create(this) }
}
