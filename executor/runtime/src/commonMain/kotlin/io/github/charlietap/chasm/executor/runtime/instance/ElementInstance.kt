package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

data class ElementInstance(
    private val referenceType: ReferenceType,
    private val references: MutableList<ReferenceValue>,
)
