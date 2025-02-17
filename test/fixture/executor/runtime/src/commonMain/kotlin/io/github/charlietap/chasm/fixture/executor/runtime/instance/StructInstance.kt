package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.StructInstance
import io.github.charlietap.chasm.fixture.ir.type.definedType
import io.github.charlietap.chasm.fixture.ir.type.structType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.StructType

fun structInstance(
    definedType: DefinedType = definedType(),
    structType: StructType = structType(),
    fields: LongArray = longArrayOf(),
) = StructInstance(
    definedType = definedType,
    structType = structType,
    fields = fields,
)
