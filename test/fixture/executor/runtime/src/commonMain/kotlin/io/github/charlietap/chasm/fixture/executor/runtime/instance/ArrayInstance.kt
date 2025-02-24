package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.DefinedType

fun arrayInstance(
    definedType: DefinedType = definedType(),
    arrayType: ArrayType = arrayType(),
    fields: LongArray = longArrayOf(),
) = ArrayInstance(
    definedType = definedType,
    arrayType = arrayType,
    fields = fields,
)
