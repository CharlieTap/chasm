package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.invoker.ext.valueFromBytes
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.arrayType
import io.github.charlietap.chasm.executor.runtime.ext.data
import io.github.charlietap.chasm.executor.runtime.ext.dataAddress
import io.github.charlietap.chasm.executor.runtime.ext.definedType
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popArrayReference
import io.github.charlietap.chasm.executor.runtime.ext.popI32
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import io.github.charlietap.chasm.type.ext.bitWidth

internal fun ArrayInitDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitData,
): Result<Unit, InvocationError> =
    ArrayInitDataExecutor(
        context = context,
        instruction = instruction,
        definedTypeExpander = ::DefinedTypeExpander,
        fieldPacker = ::FieldPacker,
    )

internal fun ArrayInitDataExecutor(
    context: ExecutionContext,
    instruction: AggregateInstruction.ArrayInitData,
    definedTypeExpander: DefinedTypeExpander,
    fieldPacker: FieldPacker,
): Result<Unit, InvocationError> = binding {

    val(stack, store) = context
    val (typeIndex, dataIndex) = instruction
    val frame = stack.peekFrame().bind()
    val definedType = frame.instance
        .definedType(typeIndex)
        .bind()

    val arrayType = definedTypeExpander(definedType).arrayType().bind()

    val dataAddress = frame.instance
        .dataAddress(dataIndex)
        .bind()
    val dataInstance = store.data(dataAddress).bind()

    val elementsToCopy = stack.popI32().bind()
    val byteArrayOffset = stack.popI32().bind()
    val arrayOffset = stack.popI32().bind()

    val arrayReference = stack.popArrayReference().bind()
    val arrayInstance = arrayReference.instance

    val arrayElementSizeInBytes = arrayType.fieldType
        .bitWidth()
        .toResultOr {
            InvocationError.UnobservableBitWidth
        }.bind() / 8

    if (
        (arrayOffset + elementsToCopy > arrayInstance.fields.size) ||
        (byteArrayOffset + (elementsToCopy * arrayElementSizeInBytes) > dataInstance.bytes.size)
    ) {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }

    repeat(elementsToCopy) { offset ->

        val srcOffsetInByteArray = byteArrayOffset + (arrayElementSizeInBytes * offset)
        val endOffsetInByteArray = srcOffsetInByteArray + arrayElementSizeInBytes

        val byteArray = dataInstance.bytes.sliceArray(srcOffsetInByteArray until endOffsetInByteArray)
        val element = arrayType.fieldType.valueFromBytes(byteArray).bind()

        val fieldIndex = arrayOffset + offset
        val fieldValue = fieldPacker(element, arrayType.fieldType).bind()

        arrayInstance.fields[fieldIndex] = fieldValue
    }
}
