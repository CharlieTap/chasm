package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.stack.FrameStackDepths
import io.github.charlietap.chasm.executor.runtime.stack.StackDepths
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue

fun stack(
    frames: List<Stack.Entry.ActivationFrame> = emptyList(),
    handlers: List<ExceptionHandler> = emptyList(),
    instructions: List<DispatchableInstruction> = emptyList(),
    labels: List<Stack.Entry.Label> = emptyList(),
    values: List<Stack.Entry.Value> = emptyList(),
) = Stack(
    frames = frames,
    handlers = handlers,
    instructions = instructions,
    labels = labels,
    values = values,
)

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

fun frame(
    arity: Int = 0,
    depths: StackDepths = stackDepths(),
    instance: ModuleInstance = moduleInstance(),
    locals: MutableList<ExecutionValue> = mutableListOf(),
) = Stack.Entry.ActivationFrame(
    arity = arity,
    depths = depths,
    locals = locals,
    instance = instance,
)

fun label(
    arity: Int = 0,
    stackInstructionsDepth: Int = 0,
    stackLabelsDepth: Int = 0,
    stackValuesDepth: Int = 0,
    continuation: List<DispatchableInstruction> = emptyList(),
) = Stack.Entry.Label(
    arity = arity,
    stackInstructionsDepth = stackInstructionsDepth,
    stackLabelsDepth = stackLabelsDepth,
    stackValuesDepth = stackValuesDepth,
    continuation = continuation,
)

fun value(
    value: ExecutionValue = executionValue(),
) = Stack.Entry.Value(
    value = value,
)
