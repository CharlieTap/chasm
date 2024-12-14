package io.github.charlietap.chasm.executor.runtime.function

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import kotlin.jvm.JvmInline

@JvmInline
value class Expression(val instructions: List<DispatchableInstruction>) {
    constructor(vararg instructions: DispatchableInstruction) : this(instructions.toList())

    companion object {
        val EMPTY = Expression(emptyList())
    }
}
