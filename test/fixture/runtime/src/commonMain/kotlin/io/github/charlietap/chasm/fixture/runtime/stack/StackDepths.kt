package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.runtime.stack.StackDepths

fun stackDepths(
    handlers: Int = 0,
    instructions: Int = 0,
    labels: Int = 0,
    values: Int = 0,
): StackDepths = StackDepths(
    handlers = handlers,
    instructions = instructions,
    labels = labels,
    values = values,
)
