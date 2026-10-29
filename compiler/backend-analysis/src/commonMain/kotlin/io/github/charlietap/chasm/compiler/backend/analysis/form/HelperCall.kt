package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.DataIndex
import io.github.charlietap.chasm.cir.ElementIndex
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.TableIndex
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.ANY_CONVERT_EXTERN
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_FILL
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_INIT_DATA
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_INIT_ELEMENT
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_NEW
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_NEW_DATA
import io.github.charlietap.chasm.compiler.backend.abi.ARRAY_NEW_ELEMENT
import io.github.charlietap.chasm.compiler.backend.abi.DATA_DROP
import io.github.charlietap.chasm.compiler.backend.abi.DATA_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.ELEMENT_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.ELEM_DROP
import io.github.charlietap.chasm.compiler.backend.abi.EXTERN_CONVERT_ANY
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_GROW
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INIT
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MEMORY_SIZE_PAGES
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_COPY
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_FILL
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_GET
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_GROW
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_INIT
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_SET
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_SIZE

sealed interface HelperArgument {
    val words: Int get() = 1

    data object Context : HelperArgument

    class Object(
        val resource: Resource,
        val type: String,
    ) : HelperArgument

    class RuntimeType(
        val type: TypeIndex,
    ) : HelperArgument

    class I32(
        val operand: Operand,
    ) : HelperArgument

    class Encoded(
        val operand: Operand,
    ) : HelperArgument {
        override val words: Int get() = 2
    }
}

class HelperCall(
    val method: MethodRef,
    val arguments: List<HelperArgument>,
    val destination: Int?,
    val growsMemory: Boolean = false,
    val producesReference: Boolean = false,
) {
    val words: Int get() = arguments.sumOf { it.words }
}

fun Instruction.helperCall(): HelperCall? = when (val i = this) {
    is MemoryInstruction.MemorySizeS -> HelperCall(MEMORY_SIZE_PAGES, listOf(memory(i.memory)), i.destinationSlot)
    is MemoryInstruction.MemoryGrowI -> memoryGrow(i.memory, immediate(i.pagesToAdd), i.destinationSlot)
    is MemoryInstruction.MemoryGrowS -> memoryGrow(i.memory, slot(i.pagesToAddSlot), i.destinationSlot)
    is MemoryInstruction.MemoryInitIii -> memoryInit(i.memory, i.data, immediate(i.sourceOffset), immediate(i.destinationOffset), immediate(i.bytesToCopy))
    is MemoryInstruction.MemoryInitIis -> memoryInit(i.memory, i.data, immediate(i.sourceOffset), slot(i.destinationOffsetSlot), immediate(i.bytesToCopy))
    is MemoryInstruction.MemoryInitIsi -> memoryInit(i.memory, i.data, slot(i.sourceOffsetSlot), immediate(i.destinationOffset), immediate(i.bytesToCopy))
    is MemoryInstruction.MemoryInitIss -> memoryInit(i.memory, i.data, slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot), immediate(i.bytesToCopy))
    is MemoryInstruction.MemoryInitSii -> memoryInit(i.memory, i.data, immediate(i.sourceOffset), immediate(i.destinationOffset), slot(i.bytesToCopySlot))
    is MemoryInstruction.MemoryInitSis -> memoryInit(i.memory, i.data, immediate(i.sourceOffset), slot(i.destinationOffsetSlot), slot(i.bytesToCopySlot))
    is MemoryInstruction.MemoryInitSsi -> memoryInit(i.memory, i.data, slot(i.sourceOffsetSlot), immediate(i.destinationOffset), slot(i.bytesToCopySlot))
    is MemoryInstruction.MemoryInitSss -> memoryInit(i.memory, i.data, slot(i.sourceOffsetSlot), slot(i.destinationOffsetSlot), slot(i.bytesToCopySlot))
    is MemoryInstruction.DataDrop -> HelperCall(DATA_DROP, listOf(data(i.data)), null)

    is TableInstruction.TableGetI -> HelperCall(TABLE_GET, listOf(table(i.table), int(immediate(i.elementIndex))), i.destinationSlot, producesReference = true)
    is TableInstruction.TableGetS -> HelperCall(TABLE_GET, listOf(table(i.table), int(slot(i.elementIndexSlot))), i.destinationSlot, producesReference = true)
    is TableInstruction.TableSetSi -> HelperCall(TABLE_SET, listOf(table(i.table), int(immediate(i.elementIndex)), long(slot(i.valueSlot))), null)
    is TableInstruction.TableSetSs -> HelperCall(TABLE_SET, listOf(table(i.table), int(slot(i.elementIndexSlot)), long(slot(i.valueSlot))), null)
    is TableInstruction.TableSizeS -> HelperCall(TABLE_SIZE, listOf(table(i.table)), i.destinationSlot)
    is TableInstruction.TableGrowIs -> tableGrow(i.table, immediate(i.elementsToAdd), i.referenceValueSlot, i.destinationSlot)
    is TableInstruction.TableGrowSs -> tableGrow(i.table, slot(i.elementsToAddSlot), i.referenceValueSlot, i.destinationSlot)
    is TableInstruction.TableFillIsi -> tableFill(i.table, immediate(i.tableOffset), i.fillValueSlot, immediate(i.elementsToFill))
    is TableInstruction.TableFillIss -> tableFill(i.table, slot(i.tableOffsetSlot), i.fillValueSlot, immediate(i.elementsToFill))
    is TableInstruction.TableFillSsi -> tableFill(i.table, immediate(i.tableOffset), i.fillValueSlot, slot(i.elementsToFillSlot))
    is TableInstruction.TableFillSss -> tableFill(i.table, slot(i.tableOffsetSlot), i.fillValueSlot, slot(i.elementsToFillSlot))
    is TableInstruction.TableCopyIii -> tableCopy(i.srcTable, i.destTable, immediate(i.srcOffset), immediate(i.dstOffset), immediate(i.elementsToCopy))
    is TableInstruction.TableCopyIis -> tableCopy(i.srcTable, i.destTable, immediate(i.srcOffset), slot(i.dstOffsetSlot), immediate(i.elementsToCopy))
    is TableInstruction.TableCopyIsi -> tableCopy(i.srcTable, i.destTable, slot(i.srcOffsetSlot), immediate(i.dstOffset), immediate(i.elementsToCopy))
    is TableInstruction.TableCopyIss -> tableCopy(i.srcTable, i.destTable, slot(i.srcOffsetSlot), slot(i.dstOffsetSlot), immediate(i.elementsToCopy))
    is TableInstruction.TableCopySii -> tableCopy(i.srcTable, i.destTable, immediate(i.srcOffset), immediate(i.dstOffset), slot(i.elementsToCopySlot))
    is TableInstruction.TableCopySis -> tableCopy(i.srcTable, i.destTable, immediate(i.srcOffset), slot(i.dstOffsetSlot), slot(i.elementsToCopySlot))
    is TableInstruction.TableCopySsi -> tableCopy(i.srcTable, i.destTable, slot(i.srcOffsetSlot), immediate(i.dstOffset), slot(i.elementsToCopySlot))
    is TableInstruction.TableCopySss -> tableCopy(i.srcTable, i.destTable, slot(i.srcOffsetSlot), slot(i.dstOffsetSlot), slot(i.elementsToCopySlot))
    is TableInstruction.TableInitIii -> tableInit(i.table, i.element, immediate(i.segmentOffset), immediate(i.tableOffset), immediate(i.elementsToInitialise))
    is TableInstruction.TableInitIis -> tableInit(i.table, i.element, immediate(i.segmentOffset), slot(i.tableOffsetSlot), immediate(i.elementsToInitialise))
    is TableInstruction.TableInitIsi -> tableInit(i.table, i.element, slot(i.segmentOffsetSlot), immediate(i.tableOffset), immediate(i.elementsToInitialise))
    is TableInstruction.TableInitIss -> tableInit(i.table, i.element, slot(i.segmentOffsetSlot), slot(i.tableOffsetSlot), immediate(i.elementsToInitialise))
    is TableInstruction.TableInitSii -> tableInit(i.table, i.element, immediate(i.segmentOffset), immediate(i.tableOffset), slot(i.elementsToInitialiseSlot))
    is TableInstruction.TableInitSis -> tableInit(i.table, i.element, immediate(i.segmentOffset), slot(i.tableOffsetSlot), slot(i.elementsToInitialiseSlot))
    is TableInstruction.TableInitSsi -> tableInit(i.table, i.element, slot(i.segmentOffsetSlot), immediate(i.tableOffset), slot(i.elementsToInitialiseSlot))
    is TableInstruction.TableInitSss -> tableInit(i.table, i.element, slot(i.segmentOffsetSlot), slot(i.tableOffsetSlot), slot(i.elementsToInitialiseSlot))
    is TableInstruction.ElemDrop -> HelperCall(ELEM_DROP, listOf(element(i.element)), null)

    is AggregateInstruction.ArrayNewIi -> arrayNew(i.type, immediate(i.size), immediate(i.value), i.destinationSlot)
    is AggregateInstruction.ArrayNewIs -> arrayNew(i.type, immediate(i.size), slot(i.valueSlot), i.destinationSlot)
    is AggregateInstruction.ArrayNewSi -> arrayNew(i.type, slot(i.sizeSlot), immediate(i.value), i.destinationSlot)
    is AggregateInstruction.ArrayNewSs -> arrayNew(i.type, slot(i.sizeSlot), slot(i.valueSlot), i.destinationSlot)
    is AggregateInstruction.ArrayNewDataIi ->
        arrayNewData(i.type, i.data, immediate(i.sourceOffset), immediate(i.arrayLength), i.fieldWidthInBytes, i.destinationSlot)
    is AggregateInstruction.ArrayNewDataIs ->
        arrayNewData(i.type, i.data, immediate(i.sourceOffset), slot(i.arrayLengthSlot), i.fieldWidthInBytes, i.destinationSlot)
    is AggregateInstruction.ArrayNewDataSi ->
        arrayNewData(i.type, i.data, slot(i.sourceOffsetSlot), immediate(i.arrayLength), i.fieldWidthInBytes, i.destinationSlot)
    is AggregateInstruction.ArrayNewDataSs ->
        arrayNewData(i.type, i.data, slot(i.sourceOffsetSlot), slot(i.arrayLengthSlot), i.fieldWidthInBytes, i.destinationSlot)
    is AggregateInstruction.ArrayNewElementIi ->
        arrayNewElement(i.type, i.element, immediate(i.sourceOffset), immediate(i.arrayLength), i.destinationSlot)
    is AggregateInstruction.ArrayNewElementIs ->
        arrayNewElement(i.type, i.element, immediate(i.sourceOffset), slot(i.arrayLengthSlot), i.destinationSlot)
    is AggregateInstruction.ArrayNewElementSi ->
        arrayNewElement(i.type, i.element, slot(i.sourceOffsetSlot), immediate(i.arrayLength), i.destinationSlot)
    is AggregateInstruction.ArrayNewElementSs ->
        arrayNewElement(i.type, i.element, slot(i.sourceOffsetSlot), slot(i.arrayLengthSlot), i.destinationSlot)
    is AggregateInstruction.ArrayFillIii -> arrayFill(i.addressSlot, immediate(i.arrayElementOffset), immediate(i.fillValue), immediate(i.elementsToFill))
    is AggregateInstruction.ArrayFillIis -> arrayFill(i.addressSlot, slot(i.arrayElementOffsetSlot), immediate(i.fillValue), immediate(i.elementsToFill))
    is AggregateInstruction.ArrayFillIsi -> arrayFill(i.addressSlot, immediate(i.arrayElementOffset), slot(i.fillValueSlot), immediate(i.elementsToFill))
    is AggregateInstruction.ArrayFillIss -> arrayFill(i.addressSlot, slot(i.arrayElementOffsetSlot), slot(i.fillValueSlot), immediate(i.elementsToFill))
    is AggregateInstruction.ArrayFillSii -> arrayFill(i.addressSlot, immediate(i.arrayElementOffset), immediate(i.fillValue), slot(i.elementsToFillSlot))
    is AggregateInstruction.ArrayFillSis -> arrayFill(i.addressSlot, slot(i.arrayElementOffsetSlot), immediate(i.fillValue), slot(i.elementsToFillSlot))
    is AggregateInstruction.ArrayFillSsi -> arrayFill(i.addressSlot, immediate(i.arrayElementOffset), slot(i.fillValueSlot), slot(i.elementsToFillSlot))
    is AggregateInstruction.ArrayFillSss -> arrayFill(i.addressSlot, slot(i.arrayElementOffsetSlot), slot(i.fillValueSlot), slot(i.elementsToFillSlot))
    is AggregateInstruction.ArrayInitDataIii ->
        arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data, immediate(i.sourceOffset), immediate(i.elementsToCopy), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitDataIis ->
        arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data, immediate(i.sourceOffset), immediate(i.elementsToCopy), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitDataIsi ->
        arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitDataIss ->
        arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitDataSii ->
        arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data, immediate(i.sourceOffset), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitDataSis ->
        arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data, immediate(i.sourceOffset), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitDataSsi ->
        arrayInitData(i.addressSlot, immediate(i.destinationOffset), i.data, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitDataSss ->
        arrayInitData(i.addressSlot, slot(i.destinationOffsetSlot), i.data, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot), i.fieldWidthInBytes)
    is AggregateInstruction.ArrayInitElementIii ->
        arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element, immediate(i.sourceOffset), immediate(i.elementsToCopy))
    is AggregateInstruction.ArrayInitElementIis ->
        arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element, immediate(i.sourceOffset), immediate(i.elementsToCopy))
    is AggregateInstruction.ArrayInitElementIsi ->
        arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy))
    is AggregateInstruction.ArrayInitElementIss ->
        arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element, slot(i.sourceOffsetSlot), immediate(i.elementsToCopy))
    is AggregateInstruction.ArrayInitElementSii ->
        arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element, immediate(i.sourceOffset), slot(i.elementsToCopySlot))
    is AggregateInstruction.ArrayInitElementSis ->
        arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element, immediate(i.sourceOffset), slot(i.elementsToCopySlot))
    is AggregateInstruction.ArrayInitElementSsi ->
        arrayInitElement(i.addressSlot, immediate(i.destinationOffset), i.element, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
    is AggregateInstruction.ArrayInitElementSss ->
        arrayInitElement(i.addressSlot, slot(i.destinationOffsetSlot), i.element, slot(i.sourceOffsetSlot), slot(i.elementsToCopySlot))
    is AggregateInstruction.AnyConvertExternS ->
        HelperCall(ANY_CONVERT_EXTERN, listOf(long(slot(i.valueSlot))), i.destinationSlot, producesReference = true)
    is AggregateInstruction.ExternConvertAnyS ->
        HelperCall(EXTERN_CONVERT_ANY, listOf(long(slot(i.valueSlot))), i.destinationSlot, producesReference = true)
    else -> null
}

private fun int(
    operand: Operand,
) = HelperArgument.I32(operand)

private fun long(
    operand: Operand,
) = HelperArgument.Encoded(operand)

private fun memory(
    index: MemoryIndex,
) = HelperArgument.Object(Resource.Memory(index.index), MEMORY_INSTANCE)

private fun table(
    index: TableIndex,
) = HelperArgument.Object(Resource.Table(index.index), TABLE_INSTANCE)

private fun data(
    index: DataIndex,
) = HelperArgument.Object(Resource.Data(index.index), DATA_INSTANCE)

private fun element(
    index: ElementIndex,
) = HelperArgument.Object(Resource.Element(index.index), ELEMENT_INSTANCE)

private fun memoryGrow(
    memory: MemoryIndex,
    pages: Operand,
    destination: Int,
) =
    HelperCall(MEMORY_GROW, listOf(HelperArgument.Context, memory(memory), int(pages)), destination, growsMemory = true)

private fun memoryInit(
    memory: MemoryIndex,
    data: DataIndex,
    sourceOffset: Operand,
    destinationOffset: Operand,
    count: Operand,
) =
    HelperCall(MEMORY_INIT, listOf(memory(memory), data(data), int(sourceOffset), int(destinationOffset), int(count)), null)

private fun tableGrow(
    table: TableIndex,
    count: Operand,
    valueSlot: Int,
    destination: Int,
) =
    HelperCall(TABLE_GROW, listOf(HelperArgument.Context, table(table), int(count), long(slot(valueSlot))), destination)

private fun tableFill(
    table: TableIndex,
    offset: Operand,
    valueSlot: Int,
    count: Operand,
) =
    HelperCall(TABLE_FILL, listOf(table(table), int(offset), long(slot(valueSlot)), int(count)), null)

private fun tableCopy(
    source: TableIndex,
    destination: TableIndex,
    sourceOffset: Operand,
    destinationOffset: Operand,
    count: Operand,
) =
    HelperCall(TABLE_COPY, listOf(table(source), table(destination), int(sourceOffset), int(destinationOffset), int(count)), null)

private fun tableInit(
    table: TableIndex,
    element: ElementIndex,
    segmentOffset: Operand,
    tableOffset: Operand,
    count: Operand,
) =
    HelperCall(TABLE_INIT, listOf(table(table), element(element), int(segmentOffset), int(tableOffset), int(count)), null)

private fun arrayNew(
    type: TypeIndex,
    size: Operand,
    value: Operand,
    destination: Int,
) = HelperCall(
    ARRAY_NEW,
    listOf(HelperArgument.Context, HelperArgument.RuntimeType(type), int(size), long(value)),
    destination,
    producesReference = true,
)

private fun arrayNewData(
    type: TypeIndex,
    data: DataIndex,
    sourceOffset: Operand,
    length: Operand,
    fieldWidth: Int,
    destination: Int,
) = HelperCall(
    ARRAY_NEW_DATA,
    listOf(HelperArgument.Context, HelperArgument.RuntimeType(type), data(data), int(sourceOffset), int(length), int(immediate(fieldWidth))),
    destination,
    producesReference = true,
)

private fun arrayNewElement(
    type: TypeIndex,
    element: ElementIndex,
    sourceOffset: Operand,
    length: Operand,
    destination: Int,
) = HelperCall(
    ARRAY_NEW_ELEMENT,
    listOf(HelperArgument.Context, HelperArgument.RuntimeType(type), element(element), int(sourceOffset), int(length)),
    destination,
    producesReference = true,
)

private fun arrayFill(
    addressSlot: Int,
    offset: Operand,
    value: Operand,
    count: Operand,
) =
    HelperCall(ARRAY_FILL, listOf(HelperArgument.Context, long(slot(addressSlot)), int(offset), long(value), int(count)), null)

private fun arrayInitData(
    addressSlot: Int,
    destinationOffset: Operand,
    data: DataIndex,
    sourceOffset: Operand,
    count: Operand,
    fieldWidth: Int,
) =
    HelperCall(
        ARRAY_INIT_DATA,
        listOf(
            HelperArgument.Context,
            long(slot(addressSlot)),
            int(destinationOffset),
            data(data),
            int(sourceOffset),
            int(count),
            int(immediate(fieldWidth)),
        ),
        null,
    )

private fun arrayInitElement(
    addressSlot: Int,
    destinationOffset: Operand,
    element: ElementIndex,
    sourceOffset: Operand,
    count: Operand,
) =
    HelperCall(
        ARRAY_INIT_ELEMENT,
        listOf(HelperArgument.Context, long(slot(addressSlot)), int(destinationOffset), element(element), int(sourceOffset), int(count)),
        null,
    )
