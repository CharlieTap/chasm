package io.github.charlietap.chasm.ast.instruction

import kotlin.jvm.JvmInline

@JvmInline
value class Expression(val instructions: List<Instruction>) {
    constructor(vararg instructions: Instruction) : this(instructions.toList())
}
