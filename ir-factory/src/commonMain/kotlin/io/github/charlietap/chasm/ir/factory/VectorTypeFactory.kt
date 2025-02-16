package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.ir.type.VectorType as IRVectorType

internal inline fun VectorTypeFactory(
    vectorType: VectorType,
): IRVectorType {
    return when (vectorType) {
        VectorType.V128 -> IRVectorType.V128
    }
}
