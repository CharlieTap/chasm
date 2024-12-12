package io.github.charlietap.chasm.fixture.ast.type

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.SharedStatus

fun memoryType(
    limits: Limits = limits(),
    shared: SharedStatus = unsharedStatus(),
) = MemoryType(
    limits = limits,
    shared = shared,
)
