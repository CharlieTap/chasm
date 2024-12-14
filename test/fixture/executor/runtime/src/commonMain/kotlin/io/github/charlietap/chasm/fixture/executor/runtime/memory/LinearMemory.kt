package io.github.charlietap.chasm.fixture.executor.runtime.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

object NoOpLinearMemory : LinearMemory

fun linearMemory() = NoOpLinearMemory
