package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.ir.type.Limits
import io.github.charlietap.chasm.ir.type.MemoryType
import io.github.charlietap.chasm.ir.type.SharedStatus

fun memoryType(
    limits: Limits = limits(),
    shared: SharedStatus = unsharedStatus(),
) = MemoryType(
    limits = limits,
    shared = shared,
)
