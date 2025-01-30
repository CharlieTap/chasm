package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.ControlStack
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
) = TryTableExecutor(
    context = context,
    instruction = instruction,
    blockExecutor = ::InstructionBlockExecutor,
)

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
    crossinline blockExecutor: InstructionBlockExecutor,
) {
    val cstack = context.cstack
    val vstack = context.vstack

    val label = ControlStack.Entry.Label(
        arity = instruction.results,
        depths = LabelStackDepths(
            instructions = cstack.instructionsDepth(),
            labels = cstack.labelsDepth(),
            values = vstack.depth() - instruction.params,
        ),
        continuation = null,
    )

    val handler = ExceptionHandler(
        instructions = instruction.handlers,
        framesDepth = cstack.framesDepth(),
        labelsDepth = cstack.labelsDepth(),
        instructionsDepth = cstack.instructionsDepth(),
    )

    blockExecutor(cstack, label, instruction.instructions, handler)
}
