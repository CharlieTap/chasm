package io.github.charlietap.chasm.executor.instantiator.ext

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal fun ReferenceType.default(): ExecutionValue = when (this) {
    ReferenceType.Funcref -> ReferenceValue.Null(this)
    ReferenceType.Externref -> ReferenceValue.Null(this)
}
