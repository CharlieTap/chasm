package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.executor.runtime.stack.stackDepths

fun stack(
    frames: List<ActivationFrame> = emptyList(),
    handlers: List<ExceptionHandler> = emptyList(),
    instructions: List<DispatchableInstruction> = emptyList(),
    labels: List<Stack.Entry.Label> = emptyList(),
    values: List<ExecutionValue> = emptyList(),
) = Stack(
    frames = frames,
    handlers = handlers,
    instructions = instructions,
    labels = labels,
    values = values,
)

fun label(
    arity: Int = 0,
    depths: StackDepths = stackDepths(),
    continuation: DispatchableInstruction? = null,
) = Stack.Entry.Label(
    arity = arity,
    depths = depths,
    continuation = continuation,
)
