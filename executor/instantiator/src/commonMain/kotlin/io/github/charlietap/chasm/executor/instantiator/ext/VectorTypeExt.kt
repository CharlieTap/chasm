package io.github.charlietap.chasm.executor.instantiator.ext

import io.github.charlietap.chasm.ast.type.VectorType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.VectorValue

internal fun VectorType.default(): ExecutionValue = when (this) {
    VectorType.V128 -> VectorValue.V128(ByteArray(16) { 0 })
}
