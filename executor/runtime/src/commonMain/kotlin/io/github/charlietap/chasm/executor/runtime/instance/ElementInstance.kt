package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

data class ElementInstance(
    val type: ReferenceType,
    val elements: MutableList<ReferenceValue>,
    val dropped: Boolean = false,
)
