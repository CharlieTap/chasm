package io.github.charlietap.chasm.fixture.executor.runtime.stack

import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths

fun stackDepths(): StackDepths = frameStackDepths()

fun frameStackDepths(
    handlers: Int = 0,
    instructions: Int = 0,
    labels: Int = 0,
    values: Int = 0,
): StackDepths = FrameStackDepths(
    handlers = handlers,
    instructions = instructions,
    labels = labels,
    values = values,
)

fun labelStackDepths(
    instructions: Int = 0,
    labels: Int = 0,
    values: Int = 0,
): StackDepths = LabelStackDepths(
    instructions = instructions,
    labels = labels,
    values = values,
)
