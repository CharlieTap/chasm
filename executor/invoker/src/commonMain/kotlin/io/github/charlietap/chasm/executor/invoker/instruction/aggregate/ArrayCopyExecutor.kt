package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.Mutability
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander

internal fun ArrayCopyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayCopy,
): Result<Unit, InvocationError> =
    ArrayCopyExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
    )

internal inline fun ArrayCopyExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayCopy,
    crossinline definedTypeExpander: DefinedTypeExpander,
): Result<Unit, InvocationError> = binding {

    // x = dest
    // y = src
    val (stack) = context
    val frame = stack.peekFrame().bind()
    val destDefinedType = frame.instance
        .definedType(instruction.destinationTypeIndex)
        .bind()

    val destArrayType = definedTypeExpander(destDefinedType).arrayType().bind()
    if (destArrayType.fieldType.mutability != Mutability.Var) {
        Err(InvocationError.ArrayCopyOnAConstArray).bind<Unit>()
    }

    val elementsToCopy = stack.popI32().bind()
    val sourceOffset = stack.popI32().bind()
    val srcReference = stack.popArrayReference().bind()
    val destinationOffset = stack.popI32().bind()
    val destReference = stack.popArrayReference().bind()

    val source = srcReference.instance
    val destination = destReference.instance

    if (destinationOffset + elementsToCopy > destination.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (sourceOffset + elementsToCopy > source.fields.size) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    if (elementsToCopy == 0) return@binding

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
