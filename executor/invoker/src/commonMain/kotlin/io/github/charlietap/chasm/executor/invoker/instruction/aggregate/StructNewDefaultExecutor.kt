package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.definedType
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNewDefault,
) =
    StructNewDefaultExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        structNewExecutor = ::StructNewExecutor,
    )

internal inline fun StructNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNewDefault,
    crossinline definedTypeExpander: DefinedTypeExpander,
    crossinline structNewExecutor: Executor<AggregateInstruction.StructNew>,
) {
    val stack = context.vstack
    val typeIndex = instruction.typeIndex
    val frame = context.cstack.peekFrame()
    val definedType = frame.instance.definedType(typeIndex)

    val structType = definedTypeExpander(definedType).structType()
    structType.fields.forEach { fieldType ->
        val value = fieldType.default()
        stack.push(value)
    }

    structNewExecutor(context, AggregateInstruction.StructNew(typeIndex))
}
