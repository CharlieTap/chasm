package io.github.charlietap.chasm.predecoder.instruction.tablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableCopyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableFillDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableGrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableInitDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableSetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableSizeDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.elementAddress
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.FusedTableInstruction as RuntimeFusedTableInstruction

internal fun FusedTableInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedTableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedTableInstruction.TableCopy -> strictTableCopyInstruction(context, instruction).bind()
        is FusedTableInstruction.TableFill -> strictTableFillInstruction(context, instruction).bind()
        is FusedTableInstruction.TableGet -> strictTableGetInstruction(context, instruction).bind()
        is FusedTableInstruction.TableGrow -> strictTableGrowInstruction(context, instruction).bind()
        is FusedTableInstruction.TableInit -> strictTableInitInstruction(context, instruction).bind()
        is FusedTableInstruction.TableSet -> strictTableSetInstruction(context, instruction).bind()
        is FusedTableInstruction.TableSize -> strictTableSizeInstruction(context, instruction).bind()
    }
}

private fun strictTableCopyInstruction(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableCopy,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val srcTable = resolveTable(context, instruction.srcTableIdx).bind()
    val destTable = resolveTable(context, instruction.destTableIdx).bind()

    val elementsToCopyImmediate = strictTableIndexImmediate(instruction.elementsToCopy)
    val elementsToCopySlot = strictTableOperandSlot(instruction.elementsToCopy)
    val srcOffsetImmediate = strictTableIndexImmediate(instruction.srcOffset)
    val srcOffsetSlot = strictTableOperandSlot(instruction.srcOffset)
    val dstOffsetImmediate = strictTableIndexImmediate(instruction.dstOffset)
    val dstOffsetSlot = strictTableOperandSlot(instruction.dstOffset)

    when {
        elementsToCopyImmediate != null && srcOffsetImmediate != null && dstOffsetImmediate != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopyIii(
                    elementsToCopy = elementsToCopyImmediate,
                    srcOffset = srcOffsetImmediate,
                    dstOffset = dstOffsetImmediate,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        elementsToCopyImmediate != null && srcOffsetImmediate != null && dstOffsetSlot != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopyIis(
                    elementsToCopy = elementsToCopyImmediate,
                    srcOffset = srcOffsetImmediate,
                    dstOffsetSlot = dstOffsetSlot,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        elementsToCopyImmediate != null && srcOffsetSlot != null && dstOffsetImmediate != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopyIsi(
                    elementsToCopy = elementsToCopyImmediate,
                    srcOffsetSlot = srcOffsetSlot,
                    dstOffset = dstOffsetImmediate,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        elementsToCopyImmediate != null && srcOffsetSlot != null && dstOffsetSlot != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopyIss(
                    elementsToCopy = elementsToCopyImmediate,
                    srcOffsetSlot = srcOffsetSlot,
                    dstOffsetSlot = dstOffsetSlot,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        elementsToCopySlot != null && srcOffsetImmediate != null && dstOffsetImmediate != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopySii(
                    elementsToCopySlot = elementsToCopySlot,
                    srcOffset = srcOffsetImmediate,
                    dstOffset = dstOffsetImmediate,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        elementsToCopySlot != null && srcOffsetImmediate != null && dstOffsetSlot != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopySis(
                    elementsToCopySlot = elementsToCopySlot,
                    srcOffset = srcOffsetImmediate,
                    dstOffsetSlot = dstOffsetSlot,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        elementsToCopySlot != null && srcOffsetSlot != null && dstOffsetImmediate != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopySsi(
                    elementsToCopySlot = elementsToCopySlot,
                    srcOffsetSlot = srcOffsetSlot,
                    dstOffset = dstOffsetImmediate,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        elementsToCopySlot != null && srcOffsetSlot != null && dstOffsetSlot != null -> {
            TableCopyDispatcher(
                RuntimeFusedTableInstruction.TableCopySss(
                    elementsToCopySlot = elementsToCopySlot,
                    srcOffsetSlot = srcOffsetSlot,
                    dstOffsetSlot = dstOffsetSlot,
                    srcTable = srcTable,
                    destTable = destTable,
                ),
            )
        }
        else -> unsupportedUnloweredTableInstruction()
    }
}

private fun strictTableFillInstruction(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableFill,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val table = resolveTable(context, instruction.tableIdx).bind()

    val elementsToFillImmediate = strictTableIndexImmediate(instruction.elementsToFill)
    val elementsToFillSlot = strictTableOperandSlot(instruction.elementsToFill)
    val fillValueImmediate = strictTableImmediate(instruction.fillValue)
    val fillValueSlot = strictTableOperandSlot(instruction.fillValue)
    val tableOffsetImmediate = strictTableIndexImmediate(instruction.tableOffset)
    val tableOffsetSlot = strictTableOperandSlot(instruction.tableOffset)

    when {
        elementsToFillImmediate != null && fillValueImmediate != null && tableOffsetImmediate != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillIii(
                    elementsToFill = elementsToFillImmediate,
                    fillValue = fillValueImmediate,
                    tableOffset = tableOffsetImmediate,
                    table = table,
                ),
            )
        }
        elementsToFillImmediate != null && fillValueImmediate != null && tableOffsetSlot != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillIis(
                    elementsToFill = elementsToFillImmediate,
                    fillValue = fillValueImmediate,
                    tableOffsetSlot = tableOffsetSlot,
                    table = table,
                ),
            )
        }
        elementsToFillImmediate != null && fillValueSlot != null && tableOffsetImmediate != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillIsi(
                    elementsToFill = elementsToFillImmediate,
                    fillValueSlot = fillValueSlot,
                    tableOffset = tableOffsetImmediate,
                    table = table,
                ),
            )
        }
        elementsToFillImmediate != null && fillValueSlot != null && tableOffsetSlot != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillIss(
                    elementsToFill = elementsToFillImmediate,
                    fillValueSlot = fillValueSlot,
                    tableOffsetSlot = tableOffsetSlot,
                    table = table,
                ),
            )
        }
        elementsToFillSlot != null && fillValueImmediate != null && tableOffsetImmediate != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillSii(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValue = fillValueImmediate,
                    tableOffset = tableOffsetImmediate,
                    table = table,
                ),
            )
        }
        elementsToFillSlot != null && fillValueImmediate != null && tableOffsetSlot != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillSis(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValue = fillValueImmediate,
                    tableOffsetSlot = tableOffsetSlot,
                    table = table,
                ),
            )
        }
        elementsToFillSlot != null && fillValueSlot != null && tableOffsetImmediate != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillSsi(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValueSlot = fillValueSlot,
                    tableOffset = tableOffsetImmediate,
                    table = table,
                ),
            )
        }
        elementsToFillSlot != null && fillValueSlot != null && tableOffsetSlot != null -> {
            TableFillDispatcher(
                RuntimeFusedTableInstruction.TableFillSss(
                    elementsToFillSlot = elementsToFillSlot,
                    fillValueSlot = fillValueSlot,
                    tableOffsetSlot = tableOffsetSlot,
                    table = table,
                ),
            )
        }
        else -> unsupportedUnloweredTableInstruction()
    }
}

private fun strictTableGetInstruction(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableGet,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val table = resolveTable(context, instruction.tableIdx).bind()
    val destinationSlot = strictTableDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredTableInstruction()
    val elementIndexImmediate = strictTableIndexImmediate(instruction.elementIndex)
    val elementIndexSlot = strictTableOperandSlot(instruction.elementIndex)

    when {
        elementIndexImmediate != null -> {
            TableGetDispatcher(
                RuntimeFusedTableInstruction.TableGetI(
                    elementIndex = elementIndexImmediate,
                    destinationSlot = destinationSlot,
                    table = table,
                ),
            )
        }
        elementIndexSlot != null -> {
            TableGetDispatcher(
                RuntimeFusedTableInstruction.TableGetS(
                    elementIndexSlot = elementIndexSlot,
                    destinationSlot = destinationSlot,
                    table = table,
                ),
            )
        }
        else -> unsupportedUnloweredTableInstruction()
    }
}

private fun strictTableGrowInstruction(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableGrow,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val table = resolveTable(context, instruction.tableIdx).bind()
    val destinationSlot = strictTableDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredTableInstruction()
    val max = table.type.limits.max?.toInt() ?: Int.MAX_VALUE

    val elementsToAddImmediate = strictTableIndexImmediate(instruction.elementsToAdd)
    val elementsToAddSlot = strictTableOperandSlot(instruction.elementsToAdd)
    val referenceValueImmediate = strictTableImmediate(instruction.referenceValue)
    val referenceValueSlot = strictTableOperandSlot(instruction.referenceValue)

    when {
        elementsToAddImmediate != null && referenceValueImmediate != null -> {
            TableGrowDispatcher(
                RuntimeFusedTableInstruction.TableGrowIi(
                    elementsToAdd = elementsToAddImmediate,
                    referenceValue = referenceValueImmediate,
                    destinationSlot = destinationSlot,
                    table = table,
                    max = max,
                ),
            )
        }
        elementsToAddImmediate != null && referenceValueSlot != null -> {
            TableGrowDispatcher(
                RuntimeFusedTableInstruction.TableGrowIs(
                    elementsToAdd = elementsToAddImmediate,
                    referenceValueSlot = referenceValueSlot,
                    destinationSlot = destinationSlot,
                    table = table,
                    max = max,
                ),
            )
        }
        elementsToAddSlot != null && referenceValueImmediate != null -> {
            TableGrowDispatcher(
                RuntimeFusedTableInstruction.TableGrowSi(
                    elementsToAddSlot = elementsToAddSlot,
                    referenceValue = referenceValueImmediate,
                    destinationSlot = destinationSlot,
                    table = table,
                    max = max,
                ),
            )
        }
        elementsToAddSlot != null && referenceValueSlot != null -> {
            TableGrowDispatcher(
                RuntimeFusedTableInstruction.TableGrowSs(
                    elementsToAddSlot = elementsToAddSlot,
                    referenceValueSlot = referenceValueSlot,
                    destinationSlot = destinationSlot,
                    table = table,
                    max = max,
                ),
            )
        }
        else -> unsupportedUnloweredTableInstruction()
    }
}

private fun strictTableInitInstruction(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableInit,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val table = resolveTable(context, instruction.tableIdx).bind()
    val element = resolveElement(context, instruction.elemIdx).bind()

    val elementsToInitialiseImmediate = strictTableIndexImmediate(instruction.elementsToInitialise)
    val elementsToInitialiseSlot = strictTableOperandSlot(instruction.elementsToInitialise)
    val segmentOffsetImmediate = strictTableIndexImmediate(instruction.segmentOffset)
    val segmentOffsetSlot = strictTableOperandSlot(instruction.segmentOffset)
    val tableOffsetImmediate = strictTableIndexImmediate(instruction.tableOffset)
    val tableOffsetSlot = strictTableOperandSlot(instruction.tableOffset)

    when {
        elementsToInitialiseImmediate != null && segmentOffsetImmediate != null && tableOffsetImmediate != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitIii(
                    elementsToInitialise = elementsToInitialiseImmediate,
                    segmentOffset = segmentOffsetImmediate,
                    tableOffset = tableOffsetImmediate,
                    element = element,
                    table = table,
                ),
            )
        }
        elementsToInitialiseImmediate != null && segmentOffsetImmediate != null && tableOffsetSlot != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitIis(
                    elementsToInitialise = elementsToInitialiseImmediate,
                    segmentOffset = segmentOffsetImmediate,
                    tableOffsetSlot = tableOffsetSlot,
                    element = element,
                    table = table,
                ),
            )
        }
        elementsToInitialiseImmediate != null && segmentOffsetSlot != null && tableOffsetImmediate != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitIsi(
                    elementsToInitialise = elementsToInitialiseImmediate,
                    segmentOffsetSlot = segmentOffsetSlot,
                    tableOffset = tableOffsetImmediate,
                    element = element,
                    table = table,
                ),
            )
        }
        elementsToInitialiseImmediate != null && segmentOffsetSlot != null && tableOffsetSlot != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitIss(
                    elementsToInitialise = elementsToInitialiseImmediate,
                    segmentOffsetSlot = segmentOffsetSlot,
                    tableOffsetSlot = tableOffsetSlot,
                    element = element,
                    table = table,
                ),
            )
        }
        elementsToInitialiseSlot != null && segmentOffsetImmediate != null && tableOffsetImmediate != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitSii(
                    elementsToInitialiseSlot = elementsToInitialiseSlot,
                    segmentOffset = segmentOffsetImmediate,
                    tableOffset = tableOffsetImmediate,
                    element = element,
                    table = table,
                ),
            )
        }
        elementsToInitialiseSlot != null && segmentOffsetImmediate != null && tableOffsetSlot != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitSis(
                    elementsToInitialiseSlot = elementsToInitialiseSlot,
                    segmentOffset = segmentOffsetImmediate,
                    tableOffsetSlot = tableOffsetSlot,
                    element = element,
                    table = table,
                ),
            )
        }
        elementsToInitialiseSlot != null && segmentOffsetSlot != null && tableOffsetImmediate != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitSsi(
                    elementsToInitialiseSlot = elementsToInitialiseSlot,
                    segmentOffsetSlot = segmentOffsetSlot,
                    tableOffset = tableOffsetImmediate,
                    element = element,
                    table = table,
                ),
            )
        }
        elementsToInitialiseSlot != null && segmentOffsetSlot != null && tableOffsetSlot != null -> {
            TableInitDispatcher(
                RuntimeFusedTableInstruction.TableInitSss(
                    elementsToInitialiseSlot = elementsToInitialiseSlot,
                    segmentOffsetSlot = segmentOffsetSlot,
                    tableOffsetSlot = tableOffsetSlot,
                    element = element,
                    table = table,
                ),
            )
        }
        else -> unsupportedUnloweredTableInstruction()
    }
}

private fun strictTableSetInstruction(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableSet,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val table = resolveTable(context, instruction.tableIdx).bind()

    val valueImmediate = strictTableImmediate(instruction.value)
    val valueSlot = strictTableOperandSlot(instruction.value)
    val elementIndexImmediate = strictTableIndexImmediate(instruction.elementIdx)
    val elementIndexSlot = strictTableOperandSlot(instruction.elementIdx)

    when {
        valueImmediate != null && elementIndexImmediate != null -> {
            TableSetDispatcher(
                RuntimeFusedTableInstruction.TableSetIi(
                    value = valueImmediate,
                    elementIndex = elementIndexImmediate,
                    table = table,
                ),
            )
        }
        valueImmediate != null && elementIndexSlot != null -> {
            TableSetDispatcher(
                RuntimeFusedTableInstruction.TableSetIs(
                    value = valueImmediate,
                    elementIndexSlot = elementIndexSlot,
                    table = table,
                ),
            )
        }
        valueSlot != null && elementIndexImmediate != null -> {
            TableSetDispatcher(
                RuntimeFusedTableInstruction.TableSetSi(
                    valueSlot = valueSlot,
                    elementIndex = elementIndexImmediate,
                    table = table,
                ),
            )
        }
        valueSlot != null && elementIndexSlot != null -> {
            TableSetDispatcher(
                RuntimeFusedTableInstruction.TableSetSs(
                    valueSlot = valueSlot,
                    elementIndexSlot = elementIndexSlot,
                    table = table,
                ),
            )
        }
        else -> unsupportedUnloweredTableInstruction()
    }
}

private fun strictTableSizeInstruction(
    context: PredecodingContext,
    instruction: FusedTableInstruction.TableSize,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val table = resolveTable(context, instruction.tableIdx).bind()
    val destinationSlot = strictTableDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredTableInstruction()
    TableSizeDispatcher(
        RuntimeFusedTableInstruction.TableSizeS(
            destinationSlot = destinationSlot,
            table = table,
        ),
    )
}

private fun strictTableImmediate(
    operand: FusedOperand,
): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun strictTableIndexImmediate(
    operand: FusedOperand,
): Int? = strictTableImmediate(operand)?.toInt()

private fun strictTableOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictTableDestinationSlot(
    destination: FusedDestination,
): Int? = when (destination) {
    is FusedDestination.FrameSlot -> destination.offset
    is FusedDestination.LocalSet -> destination.index.idx
    else -> null
}

private fun resolveElement(
    context: PredecodingContext,
    elementIdx: Index.ElementIndex,
): Result<ElementInstance, ModuleTrapError> = binding {
    val address = context.instance.elementAddress(elementIdx).bind()
    context.store.element(address)
}

private fun resolveTable(
    context: PredecodingContext,
    tableIdx: Index.TableIndex,
): Result<TableInstance, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(tableIdx).bind()
    context.store.table(address)
}

private fun unsupportedUnloweredTableInstruction(): DispatchableInstruction =
    error("table fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")
