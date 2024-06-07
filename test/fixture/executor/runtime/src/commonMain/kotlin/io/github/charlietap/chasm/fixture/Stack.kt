package io.github.charlietap.chasm.fixture

import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.instruction.ExecutionInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.instruction.executionInstruction
import io.github.charlietap.chasm.fixture.value.executionValue

fun stack(
    frames: ArrayDeque<Stack.Entry.ActivationFrame> = ArrayDeque(),
    instructions: ArrayDeque<Stack.Entry.Instruction> = ArrayDeque(),
    labels: ArrayDeque<Stack.Entry.Label> = ArrayDeque(),
    values: ArrayDeque<Stack.Entry.Value> = ArrayDeque(),
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
    instruction: ExecutionInstruction = executionInstruction(),
) = Stack.Entry.Instruction(
    instruction = instruction,
)

fun label(
    arity: Arity.Return = Arity.Return.SIDE_EFFECT,
    stackValuesDepth: Int = 0,
    continuation: List<ExecutionInstruction> = emptyList(),
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
