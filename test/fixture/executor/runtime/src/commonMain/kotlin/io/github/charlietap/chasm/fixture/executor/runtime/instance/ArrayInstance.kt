package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.fixture.ir.type.arrayType
import io.github.charlietap.chasm.fixture.ir.type.definedType
import io.github.charlietap.chasm.ir.type.ArrayType
import io.github.charlietap.chasm.ir.type.DefinedType

fun arrayInstance(
    definedType: DefinedType = definedType(),
    arrayType: ArrayType = arrayType(),
    fields: LongArray = longArrayOf(),
) = ArrayInstance(
    definedType = definedType,
    arrayType = arrayType,
    fields = fields,
)
