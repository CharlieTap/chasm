package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.LoopDispatcher
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths

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
    val params = instruction.functionType.params.types.size

    val label = Stack.Entry.Label(
        arity = params,
        depths = LabelStackDepths(
            instructions = stack.instructionsDepth(),
            labels = stack.labelsDepth(),
            values = stack.valuesDepth() - params,
        ),
        continuation = loopDispatcher(instruction),
    )

    instructionBlockExecutor(stack, label, instruction.instructions, null).bind()
}
