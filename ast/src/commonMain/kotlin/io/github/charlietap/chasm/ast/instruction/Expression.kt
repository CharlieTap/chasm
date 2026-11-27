package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.jvm.JvmInline

@InternalChasmApi
@JvmInline
value class Expression(val instructions: List<Instruction>) {
    constructor(vararg instructions: Instruction) : this(instructions.toList())

    companion object {
        val EMPTY = Expression(emptyList())
    }
}
