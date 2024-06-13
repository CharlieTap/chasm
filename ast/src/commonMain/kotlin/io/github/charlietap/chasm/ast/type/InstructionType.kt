package io.github.charlietap.chasm.ast.type

data class InstructionType(
    val inputs: ResultType,
    val outputs: ResultType,
)
