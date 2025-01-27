package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.ArrayType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.definedType

fun arrayInstance(
    definedType: DefinedType = definedType(),
    arrayType: ArrayType = arrayType(),
    fields: MutableList<FieldValue> = mutableListOf(),
) = ArrayInstance(
    definedType = definedType,
    arrayType = arrayType,
    fields = fields,
)
