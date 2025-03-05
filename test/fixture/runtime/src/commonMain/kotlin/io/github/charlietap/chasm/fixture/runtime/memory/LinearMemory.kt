package io.github.charlietap.chasm.fixture.runtime.memory

import io.github.charlietap.chasm.runtime.memory.LinearMemory

object NoOpLinearMemory : LinearMemory

fun linearMemory() = NoOpLinearMemory
