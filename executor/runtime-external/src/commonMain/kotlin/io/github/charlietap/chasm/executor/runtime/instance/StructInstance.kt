package io.github.charlietap.chasm.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.StructType
import io.github.charlietap.chasm.executor.runtime.value.FieldValue

data class StructInstance(
    val definedType: DefinedType,
    val structType: StructType,
    val fields: MutableList<FieldValue>,
)
