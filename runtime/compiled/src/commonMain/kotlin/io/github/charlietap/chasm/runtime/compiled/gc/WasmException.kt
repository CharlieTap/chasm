package io.github.charlietap.chasm.runtime.compiled.gc

import kotlin.jvm.JvmField

class WasmException(
    @JvmField val tag: Int,
    @JvmField val words: LongArray,
    @JvmField val references: Array<Any?>,
)
