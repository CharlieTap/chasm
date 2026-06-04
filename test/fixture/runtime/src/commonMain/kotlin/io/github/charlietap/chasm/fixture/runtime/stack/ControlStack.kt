package io.github.charlietap.chasm.fixture.runtime.stack

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths

fun cstack(
    frames: List<ActivationFrame> = [],
    handlers: List<ExceptionHandler> = [],
    instructions: List<DispatchableInstruction> = [],
    labels: List<ControlStack.Entry.Label> = [],
) = ControlStack(
    frames = frames,
    handlers = handlers,
    instructions = instructions,
    labels = labels,
)

fun label(
    arity: Int = 0,
    depths: StackDepths = stackDepths(),
    continuation: DispatchableInstruction? = null,
) = ControlStack.Entry.Label(
    arity = arity,
    depths = depths,
    continuation = continuation,
)
