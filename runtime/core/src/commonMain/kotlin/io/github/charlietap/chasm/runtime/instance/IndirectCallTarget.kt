package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.function.CompiledFunction
import kotlin.jvm.JvmField

class IndirectCallTarget(
    @JvmField val reference: Long,
    @JvmField val type: Int,
    @JvmField val target: CompiledFunction,
)
