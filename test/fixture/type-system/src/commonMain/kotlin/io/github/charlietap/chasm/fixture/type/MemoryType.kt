package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.SharedStatus

fun memoryType(
    limits: Limits = limits(),
    shared: SharedStatus = unsharedStatus(),
) = MemoryType(
    limits = limits,
    shared = shared,
)
