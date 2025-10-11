package com.tap.chasm.app

import android.app.Application
import com.tap.chasm.di.AndroidAppGraph
import dev.zacsweers.metro.createGraphFactory

class ChasmApplication : Application() {
    val appGraph by lazy { createGraphFactory<AndroidAppGraph.Factory>().create(this) }
}
