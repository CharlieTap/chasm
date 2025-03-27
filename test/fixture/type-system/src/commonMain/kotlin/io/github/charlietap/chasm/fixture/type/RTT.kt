package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.RTT

fun rtt(
    superTypes: List<RTT> = emptyList(),
) = RTT(
    superTypes = superTypes,
)
