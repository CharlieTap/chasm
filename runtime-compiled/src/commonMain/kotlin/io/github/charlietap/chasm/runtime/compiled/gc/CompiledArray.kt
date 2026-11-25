package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine
import kotlin.jvm.JvmField

@InternalChasmApi
abstract class CompiledArray(
    @JvmField val rtt: Rtt,
) : ManagedArray() {

    final override fun engine(): Engine {
        return rtt.engine
    }
}
