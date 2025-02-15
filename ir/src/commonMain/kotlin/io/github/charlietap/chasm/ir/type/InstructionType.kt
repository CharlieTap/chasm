package io.github.charlietap.chasm.ir.type

data class InstructionType(
    val inputs: ResultType,
    val outputs: ResultType,
)
