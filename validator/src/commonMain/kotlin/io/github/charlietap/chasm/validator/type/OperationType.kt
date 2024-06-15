package io.github.charlietap.chasm.validator.type

internal data class OperationType(
    val inputs: List<InferredResultType>,
    val outputs: List<InferredResultType>,
)
