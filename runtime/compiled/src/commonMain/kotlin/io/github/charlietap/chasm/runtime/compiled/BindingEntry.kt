package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.runtime.function.CompiledFunction
import kotlin.jvm.JvmField

abstract class BindingEntry(
    @JvmField val binding: Array<Any?>,
    @JvmField val id: Int,
) : CompiledFunction
