package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType

fun memoryType(
    limits: Limits = limits(),
) = MemoryType(
    limits = limits,
)
