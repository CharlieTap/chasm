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
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
): Result<Unit, InvocationError> =
    LoopExecutor(
        context = context,
        instruction = instruction,
        expander = ::BlockTypeExpander,
        instructionBlockExecutor = ::InstructionBlockExecutor,
        loopDispatcher = ::LoopDispatcher,
    )

internal inline fun LoopExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.Loop,
    crossinline expander: BlockTypeExpander,
    crossinline instructionBlockExecutor: InstructionBlockExecutor,
    crossinline loopDispatcher: Dispatcher<ControlInstruction.Loop>,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val (blockType, instructions) = instruction
    val frame = stack.peekFrame().bind()
    val functionType = expander(frame.instance, blockType).bind()
    val paramArity = functionType?.let {
        Arity.Argument(functionType.params.types.size)
    } ?: Arity.Argument.NULLARY

    val label = Stack.Entry.Label(
        arity = paramArity,
        stackValuesDepth = stack.valuesDepth() - paramArity.value,
        continuation = listOf(
            loopDispatcher(ControlInstruction.Loop(blockType, instructions)),
        ),
    )

    instructionBlockExecutor(stack, label, instructions, null).bind()
}
