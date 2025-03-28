package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

fun rtt(
    type: DefinedType = definedType(),
    cache: Map<DefinedType, RTT> = emptyMap(),
) = RTT(
    type = type,
    cache = cache,
)
