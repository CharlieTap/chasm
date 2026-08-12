package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.runtime.instance.ArrayInstance
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.type.ArrayType

fun arrayInstance(
    rtt: RTT = rtt(),
    arrayType: ArrayType = arrayType(),
    fields: LongArray = longArrayOf(),
) = ArrayInstance(
    rtt = rtt,
    arrayType = arrayType,
    fields = fields,
)
