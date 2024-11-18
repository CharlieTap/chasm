package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType

fun memoryType(
    limits: Limits = limits(),
    shared: Boolean = false,
) = MemoryType(
    limits = limits,
    shared = shared,
)
