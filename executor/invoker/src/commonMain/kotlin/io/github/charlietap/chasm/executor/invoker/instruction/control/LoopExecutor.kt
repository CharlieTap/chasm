package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
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
    val stack = context.vstack
    val cstack = context.cstack
    val params = instruction.functionType.params.types.size

    val label = ControlStack.Entry.Label(
        arity = params,
        depths = LabelStackDepths(
            instructions = cstack.instructionsDepth(),
            labels = cstack.labelsDepth(),
            values = stack.depth() - params,
        ),
        continuation = LoopDispatcher(instruction),
    )

    instructionBlockExecutor(cstack, label, instruction.instructions, null)
}
