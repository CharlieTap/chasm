package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.ext.structType
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun StructNewDefaultExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.StructNewDefault,
): Result<Unit, InvocationError> =
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
): Result<Unit, InvocationError> = binding {

    val (stack) = context
    val typeIndex = instruction.typeIndex
    val frame = stack.peekFrame().bind()
    val definedType = frame.state.module.definedType(typeIndex).bind()

    val structType = definedTypeExpander(definedType).structType().bind()
    structType.fields.forEach { fieldType ->
        val value = fieldType.default().bind()
        stack.pushValue(value)
    }

    structNewExecutor(context, AggregateInstruction.StructNew(typeIndex)).bind()
}
