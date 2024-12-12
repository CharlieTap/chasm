package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.executor.runtime.value.FieldValue
import io.github.charlietap.chasm.fixture.ast.type.definedType

fun structInstance(
    definedType: DefinedType = definedType(),
    fields: MutableList<FieldValue> = mutableListOf(),
) = StructInstance(
    definedType = definedType,
    fields = fields,
)
