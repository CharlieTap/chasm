package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.MemoryType

fun publicMemoryType(
    limits: Limits = publicLimits(),
) = MemoryType(limits)
