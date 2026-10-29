@file:JvmName("CompiledOps")

package io.github.charlietap.chasm.runtime.compiled

import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.isExternReference
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.ext.toExternReference
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.type.RTT
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.jvm.JvmName

fun memorySize(
    memory: MemoryInstance,
): Long = memory.type.limits.min.toLong()

fun memoryGrow(
    context: ExecutionContext,
    memory: MemoryInstance,
    pages: Int,
): Long {
    val previous = memory.grow(pages)
    if (previous < 0 && context.store.config.limits.trapOnGrowFailure) {
        throw InvocationException(InvocationError.MemoryGrowFailed)
    }
    return previous.toLong()
}

fun memoryInit(
    memory: MemoryInstance,
    data: DataInstance,
    sourceOffset: Int,
    destinationOffset: Int,
    count: Int,
) = LinearMemoryInitialiser(data.bytes, memory.data, sourceOffset, destinationOffset, count, data.bytes.size, memory.size)

fun dataDrop(
    data: DataInstance,
) {
    data.bytes = DataInstance.EMPTY
}

fun tableGet(
    table: TableInstance,
    index: Int,
): Long = table.element(index)

fun tableSet(
    table: TableInstance,
    index: Int,
    value: Long,
) = tableBounds {
    table.elements[index] = value
}

fun tableSize(
    table: TableInstance,
): Long = table.elements.size.toLong()

fun tableGrow(
    context: ExecutionContext,
    table: TableInstance,
    count: Int,
    value: Long,
): Long {
    val previous = table.grow(count, value)
    if (previous < 0 && context.store.config.limits.trapOnGrowFailure) {
        throw InvocationException(InvocationError.TableGrowFailed)
    }
    return previous.toLong()
}

fun tableFill(
    table: TableInstance,
    offset: Int,
    value: Long,
    count: Int,
) = tableBounds {
    table.elements.fill(value, offset, offset + count)
}

fun tableCopy(
    source: TableInstance,
    destination: TableInstance,
    sourceOffset: Int,
    destinationOffset: Int,
    count: Int,
) = tableBounds {
    source.elements.copyInto(destination.elements, destinationOffset, sourceOffset, sourceOffset + count)
}

fun tableInit(
    table: TableInstance,
    element: ElementInstance,
    sourceOffset: Int,
    destinationOffset: Int,
    count: Int,
) = tableBounds {
    element.elements.copyInto(table.elements, destinationOffset, sourceOffset, sourceOffset + count)
}

fun elemDrop(
    element: ElementInstance,
) {
    element.elements = longArrayOf()
}

fun arrayNew(
    context: ExecutionContext,
    rtt: Int,
    size: Int,
    value: Long,
): Long = arrayBounds {
    context.heap.allocateArrayFilled(context, RTT(rtt), size, value)
}

fun arrayNewFixed(
    context: ExecutionContext,
    rtt: Int,
    values: LongArray,
    size: Int,
): Long = arrayBounds {
    context.heap.allocateArrayFromElements(context, RTT(rtt), values, 0, size)
}

fun arrayNewData(
    context: ExecutionContext,
    rtt: Int,
    data: DataInstance,
    sourceOffset: Int,
    length: Int,
    fieldWidth: Int,
): Long = arrayBounds { context.heap.allocateArrayFromData(context, RTT(rtt), data.bytes, sourceOffset, length, fieldWidth) }

fun arrayNewElement(
    context: ExecutionContext,
    rtt: Int,
    element: ElementInstance,
    sourceOffset: Int,
    length: Int,
): Long = arrayBounds { context.heap.allocateArrayFromElements(context, RTT(rtt), element.elements, sourceOffset, length) }

fun arrayFill(
    context: ExecutionContext,
    reference: Long,
    offset: Int,
    value: Long,
    count: Int,
) = arrayBounds {
    context.heap.fillArrayChecked(reference, offset, count, value)
}

fun arrayInitData(
    context: ExecutionContext,
    reference: Long,
    destinationOffset: Int,
    data: DataInstance,
    sourceOffset: Int,
    count: Int,
    fieldWidth: Int,
) = arrayBounds { context.heap.initializeArrayFromData(reference, destinationOffset, data.bytes, sourceOffset, count, fieldWidth) }

fun arrayInitElement(
    context: ExecutionContext,
    reference: Long,
    destinationOffset: Int,
    element: ElementInstance,
    sourceOffset: Int,
    count: Int,
) = arrayBounds { context.heap.initializeArrayFromElements(reference, destinationOffset, element.elements, sourceOffset, count) }

fun anyConvertExtern(
    reference: Long,
): Long = when {
    reference.isNullableReference() -> ReferenceValue.Null(AbstractHeapType.Any).toLong()
    reference.isExternReference() -> reference.toExternReference().referenceValue.toLongFromBoxed()
    else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
}

fun externConvertAny(
    reference: Long,
): Long = if (reference.isNullableReference()) {
    ReferenceValue.Null(AbstractHeapType.Extern).toLong()
} else {
    ReferenceValue.Extern(reference.toReferenceValue()).toLongFromBoxed()
}

private inline fun tableBounds(
    operation: () -> Unit,
) {
    try {
        operation()
    } catch (_: IndexOutOfBoundsException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    } catch (_: IllegalArgumentException) {
        throw InvocationException(InvocationError.TableOperationOutOfBounds)
    }
}
