package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popValue
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
): Result<Unit, InvocationError> = TryTableExecutor(
    context = context,
    instruction = instruction,
    expander = ::BlockTypeExpander,
    blockExecutor = ::InstructionBlockExecutor,
)

internal inline fun TryTableExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.TryTable,
    crossinline expander: BlockTypeExpander,
    crossinline blockExecutor: InstructionBlockExecutor,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val frame = stack.peekFrame().bind()
    val functionType = expander(frame.instance, instruction.blockType).bind()

    val paramArity = functionType?.let {
        Arity.Argument(functionType.params.types.size)
    } ?: Arity.Argument.NULLARY

    val returnArity = functionType?.let {
        Arity.Return(functionType.results.types.size)
    } ?: Arity.Return.SIDE_EFFECT

    val params = List(paramArity.value) {
        stack.popValue().bind().value
    }

    val label = Stack.Entry.Label(
        arity = returnArity,
        stackValuesDepth = stack.valuesDepth(),
        continuation = emptyList(),
    )

    val handler = ExceptionHandler(
        instructions = instruction.handlers,
    )

    blockExecutor(stack, label, instruction.instructions, params, handler).bind()
}
