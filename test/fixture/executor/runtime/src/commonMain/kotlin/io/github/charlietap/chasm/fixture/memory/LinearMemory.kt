package io.github.charlietap.chasm.fixture.memory

import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

object NoOpLinearMemory : LinearMemory

fun linearMemory() = NoOpLinearMemory
