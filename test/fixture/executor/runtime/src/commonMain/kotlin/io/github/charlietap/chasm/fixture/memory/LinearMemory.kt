package io.github.charlietap.chasm.fixture.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

fun linearMemory(
    min: LinearMemory.Pages = LinearMemory.Pages(1),
    max: LinearMemory.Pages? = null,
) = object : LinearMemory {
    override val min: LinearMemory.Pages = min
    override val max: LinearMemory.Pages? = max
}
