package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.embedding.engine.Engine
import kotlin.jvm.JvmField

class Rtt(
    @JvmField val id: Int,
    @JvmField val key: String,
    @JvmField val engine: Engine,
    supertype: Rtt?,
    @JvmField val layout: StorageLayout?,
) {

    @JvmField
    val depth: Int = if (supertype == null) 0 else supertype.depth + 1

    @JvmField
    val supertypes: Array<Rtt> = Array(depth + 1) { index ->
        if (supertype != null && index < depth) supertype.supertypes[index] else this
    }
}
