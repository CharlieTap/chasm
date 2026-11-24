package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.embedding.engine.Engine
import kotlin.jvm.JvmField

abstract class CompiledArray(
    @JvmField val rtt: Rtt,
) : ManagedArray() {

    final override fun engine(): Engine {
        return rtt.engine
    }
}
