package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
): Result<Unit, InvocationError> =
    LoopExecutor(
        context = context,
        instruction = instruction,
        instructionBlockExecutor = ::InstructionBlockExecutor,
        loopDispatcher = ::LoopDispatcher,
    )

internal inline fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
    crossinline loopDispatcher: Dispatcher<ControlInstruction.Loop>,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val (blockType, instructions) = instruction
    val paramArity = instruction.functionType.let {
        Arity.Argument(it.params.types.size)
    }

    val label = Stack.Entry.Label(
        arity = paramArity,
        stackInstructionsDepth = stack.instructionsDepth(),
        stackLabelsDepth = stack.labelsDepth(),
        stackValuesDepth = stack.valuesDepth() - paramArity.value,
        continuation = listOf(
            loopDispatcher(ControlInstruction.Loop(blockType, instructions)),
        ),
    )

    instructionBlockExecutor(stack, label, instructions, null).bind()
}
