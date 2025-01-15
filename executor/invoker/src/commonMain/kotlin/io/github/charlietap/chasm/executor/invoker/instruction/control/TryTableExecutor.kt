package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
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

    val (stack) = context

    val params = instruction.functionType.params.types.size
    val results = instruction.functionType.results.types.size

    val label = Stack.Entry.Label(
        arity = results,
        depths = LabelStackDepths(
            instructions = stack.instructionsDepth(),
            labels = stack.labelsDepth(),
            values = stack.valuesDepth() - params,
        ),
        continuation = null,
    )

    val handler = ExceptionHandler(
        instructions = instruction.handlers,
        framesDepth = stack.framesDepth(),
        labelsDepth = stack.labelsDepth(),
        instructionsDepth = stack.instructionsDepth(),
    )

    blockExecutor(stack, label, instruction.instructions, handler)
}
