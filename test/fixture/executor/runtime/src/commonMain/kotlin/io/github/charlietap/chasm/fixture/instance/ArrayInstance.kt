package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.fixture.type.definedType

fun arrayInstance(
    definedType: DefinedType = definedType(),
    fields: MutableList<FieldValue> = mutableListOf(),
) = ArrayInstance(
    definedType = definedType,
    fields = fields,
)
