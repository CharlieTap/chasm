package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.fixture.type.rtt
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.runtime.instance.StructInstance
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.StructType

fun structInstance(
    rtt: RTT = rtt(),
    structType: StructType = structType(),
    fields: LongArray = longArrayOf(),
) = StructInstance(
    rtt = rtt,
    structType = structType,
    fields = fields,
)
