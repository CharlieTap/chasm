package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.stack.ControlStack

fun cstack(
    handlers: List<ExceptionHandler> = [],
) = ControlStack(handlers)
