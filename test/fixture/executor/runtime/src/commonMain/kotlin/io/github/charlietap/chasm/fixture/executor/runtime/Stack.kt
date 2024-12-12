package io.github.charlietap.chasm.fixture.executor.runtime

import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionValue

fun stack(
    frames: List<Stack.Entry.ActivationFrame> = emptyList(),
    instructions: List<Stack.Entry.Instruction> = emptyList(),
    labels: List<Stack.Entry.Label> = emptyList(),
    values: List<Stack.Entry.Value> = emptyList(),
) = Stack(
    frames = frames,
    instructions = instructions,
    labels = labels,
    values = values,
)

fun frameState(
    locals: MutableList<ExecutionValue> = mutableListOf(),
    moduleInstance: ModuleInstance = moduleInstance(),
) = Stack.Entry.ActivationFrame.State(
    locals = locals,
    module = moduleInstance,
)

fun frame(
    arity: Arity.Return = Arity.Return.SIDE_EFFECT,
    stackLabelsDepth: Int = 0,
    stackValuesDepth: Int = 0,
    state: Stack.Entry.ActivationFrame.State = frameState(),
) = Stack.Entry.ActivationFrame(
    arity = arity,
    stackLabelsDepth = stackLabelsDepth,
    stackValuesDepth = stackValuesDepth,
    state = state,
)

fun instruction(
    instruction: DispatchableInstruction = dispatchableInstruction(),
    tag: Stack.Entry.InstructionTag = Stack.Entry.InstructionTag.NON_ADMIN,
    handler: ExceptionHandler? = null,
) = Stack.Entry.Instruction(
    instruction = instruction,
    tag = tag,
    handler = handler,
)

fun label(
    arity: Arity.Return = Arity.Return.SIDE_EFFECT,
    stackValuesDepth: Int = 0,
    continuation: List<DispatchableInstruction> = emptyList(),
) = Stack.Entry.Label(
    arity = arity,
    stackValuesDepth = stackValuesDepth,
    continuation = continuation,
)

fun value(
    value: ExecutionValue = executionValue(),
) = Stack.Entry.Value(
    value = value,
)
