@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewDefault,
): Result<Unit, InvocationError> =
    ArrayNewDefaultExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        arrayNewFixedExecutor = ::ArrayNewFixedExecutor,
    )

internal inline fun ArrayNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewDefault,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed>,
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val typeIndex = instruction.typeIndex
    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val size = stack.popI32().bind()
    val value = arrayType.fieldType.default().bind()
    repeat(size) {
        stack.pushValue(value)
    }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, size.toUInt())).bind()
}
