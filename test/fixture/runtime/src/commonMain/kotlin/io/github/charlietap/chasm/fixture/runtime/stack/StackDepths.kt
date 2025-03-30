package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.runtime.stack.StackDepths

fun stackDepths(
    handlers: Int = 0,
    values: Int = 0,
): StackDepths = StackDepths(
    handlers = handlers,
    values = values,
)
