package io.github.charlietap.chasm.fixture.executor.runtime.memory

import io.github.charlietap.chasm.runtime.memory.LinearMemory

object NoOpLinearMemory : LinearMemory

fun linearMemory() = NoOpLinearMemory
