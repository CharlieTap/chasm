package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack

fun cstack(
    frames: List<ActivationFrame> = emptyList(),
    handlers: List<ExceptionHandler> = emptyList(),
) = ControlStack(
    frames = frames,
    handlers = handlers,
)
