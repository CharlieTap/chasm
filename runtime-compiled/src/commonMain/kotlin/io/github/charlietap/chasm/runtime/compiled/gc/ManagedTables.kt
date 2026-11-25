@file:JvmName("ManagedTables")

package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.type.RTT
import kotlin.jvm.JvmName

@InternalChasmApi
fun tableGet(
    table: ReferenceTable,
    index: Int,
): Any? {
    val references = table.elements
    if (index < 0 || index >= references.size) throw InvocationException(InvocationError.TableOperationOutOfBounds)
    return references[index]
}

@InternalChasmApi
fun tableSet(
    table: ReferenceTable,
    index: Int,
    value: Any?,
) {
    val references = table.elements
    if (index < 0 || index >= references.size) throw InvocationException(InvocationError.TableOperationOutOfBounds)
    references[index] = value
}

@InternalChasmApi
fun tableSize(
    table: ReferenceTable,
): Int {
    return table.elements.size
}

@InternalChasmApi
fun tableGrow(
    table: ReferenceTable,
    count: Int,
    value: Any?,
): Int {

    val previous = tableGrowWithoutTrap(table, count, value)
    if (previous < 0 && table.trapOnGrowFailure) {
        throw InvocationException(InvocationError.TableGrowFailed)
    }

    return previous
}

@InternalChasmApi
fun tableGrowWithoutTrap(
    table: ReferenceTable,
    count: Int,
    value: Any?,
): Int {
    val references = table.elements
    val size = references.size
    if (count == 0) return size
    val grown = size.toLong() + count.toUInt().toLong()
    if (count < 0 && grown > Int.MAX_VALUE || grown > table.instance.maximumElements) return -1
    val storage = try {
        references.copyOf(grown.toInt())
    } catch (_: OutOfMemoryError) {
        return -1
    }
    storage.fill(value, size, grown.toInt())
    table.elements = storage
    table.instance.type.limits.min = grown.toULong()
    return size
}

@InternalChasmApi
fun tableFill(
    table: ReferenceTable,
    offset: Int,
    value: Any?,
    count: Int,
) {
    val references = table.elements
    checkTableRange(references.size, offset, count)
    references.fill(value, offset, offset + count)
}

@InternalChasmApi
fun tableCopy(
    source: ReferenceTable,
    destination: ReferenceTable,
    sourceOffset: Int,
    destinationOffset: Int,
    count: Int,
) {
    val from = source.elements
    val to = destination.elements
    checkTableRange(from.size, sourceOffset, count)
    checkTableRange(to.size, destinationOffset, count)
    from.copyInto(to, destinationOffset, sourceOffset, sourceOffset + count)
}

@InternalChasmApi
fun tableInit(
    table: ReferenceTable,
    segment: ReferenceSegment,
    segmentOffset: Int,
    tableOffset: Int,
    count: Int,
) {
    val references = table.elements
    val elements = segment.elements
    checkTableRange(elements.size, segmentOffset, count)
    checkTableRange(references.size, tableOffset, count)
    elements.copyInto(references, tableOffset, segmentOffset, segmentOffset + count)
}

@InternalChasmApi
fun elementDrop(
    segment: ReferenceSegment,
) {
    segment.elements = emptyArray()
}

@InternalChasmApi
fun indirectFunction(
    context: ExecutionContext,
    table: ReferenceTable,
    type: Int,
    index: Int,
): ManagedFunction {

    val references = table.elements
    if (index < 0 || index >= references.size) throw InvocationException(InvocationError.TableElementLookupFailed(index))
    val function = references[index] as? ManagedFunction ?: throw InvocationException(InvocationError.FunctionReferenceExpected)
    if (function.rtt != type && !context.heap.matchesRuntimeType(RTT(function.rtt), RTT(type))) {
        throw InvocationException(InvocationError.IndirectCallHasIncorrectFunctionType)
    }

    return function
}

@InternalChasmApi
fun indirectEntry(
    context: ExecutionContext,
    table: ReferenceTable,
    type: Int,
    index: Int,
): Any? {
    return indirectFunction(context, table, type, index).entry
}

private fun checkTableRange(
    size: Int,
    offset: Int,
    count: Int,
) {
    if (offset.toUInt().toLong() + count.toUInt().toLong() > size) throw InvocationException(InvocationError.TableOperationOutOfBounds)
}
