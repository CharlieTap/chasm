package io.github.charlietap.chasm.executor.memory.factory

import io.github.charlietap.chasm.executor.memory.ByteArrayLinearMemory
import io.github.charlietap.chasm.executor.runtime.memory.LinearMemory

typealias LinearMemoryFactory = (pages: Int) -> LinearMemory

fun LinearMemoryFactory(
    pages: Int,
): LinearMemory = ByteArrayLinearMemory(LinearMemory.Pages(pages))
