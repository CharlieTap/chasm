package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import kotlin.jvm.JvmInline

@JvmInline
value class Expression(val instructions: Array<DispatchableInstruction>) {
    companion object {
        val EMPTY = Expression(emptyArray())
    }
}
