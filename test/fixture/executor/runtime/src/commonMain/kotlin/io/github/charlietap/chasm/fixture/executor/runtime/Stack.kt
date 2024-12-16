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

fun frame(
    arity: Arity.Return = Arity.Return.SIDE_EFFECT,
    instance: ModuleInstance = moduleInstance(),
    locals: MutableList<ExecutionValue> = mutableListOf(),
    stackInstructionsDepth: Int = 0,
    stackLabelsDepth: Int = 0,
    stackValuesDepth: Int = 0,
) = Stack.Entry.ActivationFrame(
    arity = arity,
    stackInstructionsDepth = stackInstructionsDepth,
    stackLabelsDepth = stackLabelsDepth,
    stackValuesDepth = stackValuesDepth,
    locals = locals,
    instance = instance,
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
