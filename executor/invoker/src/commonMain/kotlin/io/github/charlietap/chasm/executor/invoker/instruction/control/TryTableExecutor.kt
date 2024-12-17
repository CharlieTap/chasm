package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
): Result<Unit, InvocationError> = TryTableExecutor(
    context = context,
    instruction = instruction,
    blockExecutor = ::InstructionBlockExecutor,
)

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
    crossinline blockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val (stack) = context

    val paramArity = instruction.functionType.let {
        Arity.Argument(it.params.types.size)
    }

    val returnArity = instruction.functionType.let {
        Arity.Return(it.results.types.size)
    }

    val label = Stack.Entry.Label(
        arity = returnArity,
        stackInstructionsDepth = stack.instructionsDepth(),
        stackLabelsDepth = stack.labelsDepth(),
        stackValuesDepth = stack.valuesDepth() - paramArity.value,
        continuation = emptyList(),
    )

    val handler = ExceptionHandler(
        instructions = instruction.handlers,
        framesDepth = stack.framesDepth(),
        labelsDepth = stack.labelsDepth(),
        instructionsDepth = stack.instructionsDepth(),
    )

    blockExecutor(stack, label, instruction.instructions, handler).bind()
}
