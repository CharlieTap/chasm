package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.fixture.type.definedType

fun structInstance(
    definedType: DefinedType = definedType(),
    fields: MutableList<FieldValue> = mutableListOf(),
) = StructInstance(
    definedType = definedType,
    fields = fields,
)
