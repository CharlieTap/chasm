package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayNewDefault,
) =
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
) {

    val stack = context.vstack
    val typeIndex = instruction.typeIndex
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)

    val arrayType = definedTypeExpander(definedType).arrayType()

    val size = stack.popI32()
    val value = arrayType.fieldType.default()
    repeat(size) {
        stack.push(value)
    }

    arrayNewFixedExecutor(context, AggregateInstruction.ArrayNewFixed(typeIndex, size.toUInt()))
}
