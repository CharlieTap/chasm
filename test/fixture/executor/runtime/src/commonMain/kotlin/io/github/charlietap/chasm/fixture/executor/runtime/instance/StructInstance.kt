package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.StructType

fun structInstance(
    definedType: DefinedType = definedType(),
    structType: StructType = structType(),
    fields: LongArray = longArrayOf(),
) = StructInstance(
    definedType = definedType,
    structType = structType,
    fields = fields,
)
