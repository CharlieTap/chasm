package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.executor.invoker.ext.bind
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayCopyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayCopy,
) =
    ArrayCopyExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
    )

internal inline fun ArrayCopyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayCopy,
    crossinline definedTypeExpander: DefinedTypeExpander,
) {

    // x = dest
    // y = src
    val (stack) = context
    val frame = stack.peekFrame()
    val destDefinedType = frame.instance
        .definedType(instruction.destinationTypeIndex)
        .bind()

    val destArrayType = definedTypeExpander(destDefinedType).arrayType().bind()
    if (destArrayType.fieldType.mutability != Mutability.Var) {
        Err(InvocationError.ArrayCopyOnAConstArray).bind()
    }

    val elementsToCopy = stack.popI32()
    val sourceOffset = stack.popI32()
    val srcReference = stack.popArrayReference().bind()
    val destinationOffset = stack.popI32()
    val destReference = stack.popArrayReference().bind()

    val source = srcReference.instance
    val destination = destReference.instance

    if (destinationOffset + elementsToCopy > destination.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind()
    }

    if (sourceOffset + elementsToCopy > source.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind()
    }

    if (elementsToCopy == 0) return

    if (destinationOffset <= sourceOffset) {
        // forward copy
        repeat(elementsToCopy) { copyOffset ->
            val field = source.fields[sourceOffset + copyOffset]
            destination.fields[destinationOffset + copyOffset] = field
        }
    } else {
        // backward copy
        repeat(elementsToCopy) { copyOffset ->
            val field = source.fields[sourceOffset + elementsToCopy - 1 - copyOffset]
            destination.fields[destinationOffset + elementsToCopy - 1 - copyOffset] = field
        }
    }
}
