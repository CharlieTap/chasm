package io.github.charlietap.chasm.validator.type

import io.github.charlietap.chasm.ast.type.ValueType

internal data class InferredResultType(
    val ellipses: Ellipses,
    val types: List<ValueType?>,
)
