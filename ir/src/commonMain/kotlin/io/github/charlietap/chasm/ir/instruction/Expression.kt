package io.github.charlietap.chasm.ir.instruction

import kotlin.jvm.JvmInline

@JvmInline
value class Expression(val instructions: List<Instruction>) {
    constructor(vararg instructions: Instruction) : this(instructions.toList())

    companion object {
        val EMPTY = Expression(emptyList())
    }
}
