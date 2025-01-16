package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.definedType
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

    val (stack) = context
    val typeIndex = instruction.typeIndex
    val frame = stack.peekFrame()
    val definedType = frame.instance
        .definedType(typeIndex)
        .bind()

    val structType = definedTypeExpander(definedType).structType().bind()
    structType.fields.forEach { fieldType ->
        val value = fieldType.default().bind()
        stack.push(value)
    }

    structNewExecutor(context, AggregateInstruction.StructNew(typeIndex))
}
