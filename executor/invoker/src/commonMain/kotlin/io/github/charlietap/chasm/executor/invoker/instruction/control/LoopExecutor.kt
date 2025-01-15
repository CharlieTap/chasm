package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths

internal fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
) = LoopExecutor(
    context = context,
    instruction = instruction,
    instructionBlockExecutor = ::InstructionBlockExecutor,
)

internal inline fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
) {
    val (stack) = context
    val params = instruction.functionType.params.types.size

    val label = Stack.Entry.Label(
        arity = params,
        depths = LabelStackDepths(
            instructions = stack.instructionsDepth(),
            labels = stack.labelsDepth(),
            values = stack.valuesDepth() - params,
        ),
        continuation = LoopDispatcher(instruction),
    )

    instructionBlockExecutor(stack, label, instruction.instructions, null)
}
