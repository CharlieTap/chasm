package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.memory.LinearMemory

internal expect fun borrowedBuffer(memory: LinearMemory): Any?
