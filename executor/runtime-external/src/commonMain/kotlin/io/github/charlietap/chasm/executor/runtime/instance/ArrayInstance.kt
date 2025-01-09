package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.value.FieldValue

data class ArrayInstance(
    val definedType: DefinedType,
    val fields: MutableList<FieldValue>,
)
