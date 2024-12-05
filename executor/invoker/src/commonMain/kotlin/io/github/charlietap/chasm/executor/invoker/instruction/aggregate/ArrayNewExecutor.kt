package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekValue
import io.github.charlietap.chasm.executor.runtime.ext.popI32

internal fun ArrayNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNew,
): Result<Unit, InvocationError> =
    ArrayNewExecutor(
        context = context,
        instruction = instruction,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
    )

internal inline fun ArrayNewExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNew,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val typeIndex = instruction.typeIndex
    val size = stack.popI32().bind()
    val value = stack.peekValue().bind()
    repeat(size - 1) {
        stack.push(value)
    }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, size.toUInt())).bind()
}
