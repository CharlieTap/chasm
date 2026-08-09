package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack

fun cstack(
    frames: List<ActivationFrame> = [],
    handlers: List<ExceptionHandler> = [],
) = ControlStack(
    frames = frames,
    handlers = handlers,
)
