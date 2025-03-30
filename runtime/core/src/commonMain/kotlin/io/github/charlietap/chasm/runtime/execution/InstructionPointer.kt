package io.github.charlietap.chasm.runtime.execution

import kotlin.jvm.JvmInline

@JvmInline
value class InstructionPointer(val pointer: Int) {

    operator fun plus(offset: Int): InstructionPointer =
        InstructionPointer(pointer + offset)

    operator fun minus(offset: Int): InstructionPointer =
        InstructionPointer(pointer - offset)
}
