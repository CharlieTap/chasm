package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.StackDepths

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

    val label = ControlStack.Entry.Label(
        arity = instruction.params,
        depths = StackDepths(
            handlers = cstack.handlersDepth(),
            instructions = cstack.instructionsDepth(),
            labels = cstack.labelsDepth(),
            values = stack.depth() - instruction.params,
        ),
        continuation = LoopDispatcher(instruction),
    )

    instructionBlockExecutor(cstack, label, instruction.instructions, null)
}
