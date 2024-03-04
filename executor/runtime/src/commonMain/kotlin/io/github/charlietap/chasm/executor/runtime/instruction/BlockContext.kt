package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.Stack

data class BlockContext(
    val depth: Int,
    val label: Stack.Entry.Label,
)
