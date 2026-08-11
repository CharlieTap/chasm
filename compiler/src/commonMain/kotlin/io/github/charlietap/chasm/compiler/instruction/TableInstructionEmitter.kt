package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.table.ElemDropDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.tablefused.TableSuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.TableSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.TableInstruction as RuntimeTableInstruction

internal fun FunctionCompilationContext.emitTableGet(
    index: OperandSource,
    destinationSlot: Int,
    table: TableInstance,
) {
    val instruction = if (index.sourceKind == OperandSourceKind.I32Immediate) {
        TableSuperInstruction.TableGetI(index.i32Immediate, destinationSlot, table)
    } else {
        TableSuperInstruction.TableGetS(index.sourceSlot, destinationSlot, table)
    }
    emitTableInstruction(instruction)
}

internal fun FunctionCompilationContext.emitTableSet(
    value: OperandSource,
    index: OperandSource,
    table: TableInstance,
) {
    val valueImmediate = value.isImmediate
    val indexImmediate = index.sourceKind == OperandSourceKind.I32Immediate
    val instruction = when {
        valueImmediate && indexImmediate -> TableSuperInstruction.TableSetIi(value.sourceBits, index.i32Immediate, table)
        valueImmediate -> TableSuperInstruction.TableSetIs(value.sourceBits, index.sourceSlot, table)
        indexImmediate -> TableSuperInstruction.TableSetSi(value.sourceSlot, index.i32Immediate, table)
        else -> TableSuperInstruction.TableSetSs(value.sourceSlot, index.sourceSlot, table)
    }
    emitTableInstruction(instruction)
}

internal fun FunctionCompilationContext.emitTableSize(table: TableInstance, destinationSlot: Int) {
    emitTableInstruction(TableSuperInstruction.TableSizeS(destinationSlot, table))
}

internal fun FunctionCompilationContext.emitTableGrow(
    elements: OperandSource,
    value: OperandSource,
    destinationSlot: Int,
    table: TableInstance,
) {
    val elementsImmediate = elements.sourceKind == OperandSourceKind.I32Immediate
    val valueImmediate = value.isImmediate
    val max = table.type.limits.max?.toInt() ?: Int.MAX_VALUE
    val instruction = when {
        elementsImmediate && valueImmediate -> TableSuperInstruction.TableGrowIi(elements.i32Immediate, value.sourceBits, destinationSlot, table, max)
        elementsImmediate -> TableSuperInstruction.TableGrowIs(elements.i32Immediate, value.sourceSlot, destinationSlot, table, max)
        valueImmediate -> TableSuperInstruction.TableGrowSi(elements.sourceSlot, value.sourceBits, destinationSlot, table, max)
        else -> TableSuperInstruction.TableGrowSs(elements.sourceSlot, value.sourceSlot, destinationSlot, table, max)
    }
    emitTableInstruction(instruction)
}

internal fun FunctionCompilationContext.emitTableCopy(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    sourceTable: TableInstance,
    destinationTable: TableInstance,
) = emitTableTernary(
    elements,
    sourceOffset,
    destinationOffset,
    { a, b, c -> TableSuperInstruction.TableCopyIii(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> TableSuperInstruction.TableCopyIis(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> TableSuperInstruction.TableCopyIsi(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> TableSuperInstruction.TableCopyIss(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> TableSuperInstruction.TableCopySii(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> TableSuperInstruction.TableCopySis(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> TableSuperInstruction.TableCopySsi(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> TableSuperInstruction.TableCopySss(a, b, c, sourceTable, destinationTable) },
)

internal fun FunctionCompilationContext.emitTableInit(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    element: ElementInstance,
    table: TableInstance,
) = emitTableTernary(
    elements,
    sourceOffset,
    destinationOffset,
    { a, b, c -> TableSuperInstruction.TableInitIii(a, b, c, element, table) },
    { a, b, c -> TableSuperInstruction.TableInitIis(a, b, c, element, table) },
    { a, b, c -> TableSuperInstruction.TableInitIsi(a, b, c, element, table) },
    { a, b, c -> TableSuperInstruction.TableInitIss(a, b, c, element, table) },
    { a, b, c -> TableSuperInstruction.TableInitSii(a, b, c, element, table) },
    { a, b, c -> TableSuperInstruction.TableInitSis(a, b, c, element, table) },
    { a, b, c -> TableSuperInstruction.TableInitSsi(a, b, c, element, table) },
    { a, b, c -> TableSuperInstruction.TableInitSss(a, b, c, element, table) },
)

internal fun FunctionCompilationContext.emitTableFill(
    elements: OperandSource,
    value: OperandSource,
    offset: OperandSource,
    table: TableInstance,
) {
    val ai = elements.sourceKind == OperandSourceKind.I32Immediate
    val bi = value.isImmediate
    val ci = offset.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) elements.i32Immediate else elements.sourceSlot
    val b = value.sourceBits
    val c = if (ci) offset.i32Immediate else offset.sourceSlot
    val instruction = when {
        ai && bi && ci -> TableSuperInstruction.TableFillIii(a, b, c, table)
        ai && bi -> TableSuperInstruction.TableFillIis(a, b, c, table)
        ai && ci -> TableSuperInstruction.TableFillIsi(a, value.sourceSlot, c, table)
        ai -> TableSuperInstruction.TableFillIss(a, value.sourceSlot, c, table)
        bi && ci -> TableSuperInstruction.TableFillSii(a, b, c, table)
        bi -> TableSuperInstruction.TableFillSis(a, b, c, table)
        ci -> TableSuperInstruction.TableFillSsi(a, value.sourceSlot, c, table)
        else -> TableSuperInstruction.TableFillSss(a, value.sourceSlot, c, table)
    }
    emitTableInstruction(instruction)
}

internal fun FunctionCompilationContext.emitElementDrop(element: ElementInstance) {
    val instruction = RuntimeTableInstruction.ElemDrop(element)
    emit(instruction, ::ElemDropDispatcher)
}

private inline fun FunctionCompilationContext.emitTableTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> TableSuperInstruction,
    iis: (Int, Int, Int) -> TableSuperInstruction,
    isi: (Int, Int, Int) -> TableSuperInstruction,
    iss: (Int, Int, Int) -> TableSuperInstruction,
    sii: (Int, Int, Int) -> TableSuperInstruction,
    sis: (Int, Int, Int) -> TableSuperInstruction,
    ssi: (Int, Int, Int) -> TableSuperInstruction,
    sss: (Int, Int, Int) -> TableSuperInstruction,
) {
    val ai = first.sourceKind == OperandSourceKind.I32Immediate
    val bi = second.sourceKind == OperandSourceKind.I32Immediate
    val ci = third.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) first.i32Immediate else first.sourceSlot
    val b = if (bi) second.i32Immediate else second.sourceSlot
    val c = if (ci) third.i32Immediate else third.sourceSlot
    val instruction = when {
        ai && bi && ci -> iii(a, b, c)
        ai && bi -> iis(a, b, c)
        ai && ci -> isi(a, b, c)
        ai -> iss(a, b, c)
        bi && ci -> sii(a, b, c)
        bi -> sis(a, b, c)
        ci -> ssi(a, b, c)
        else -> sss(a, b, c)
    }
    emitTableInstruction(instruction)
}

private fun FunctionCompilationContext.emitTableInstruction(instruction: TableSuperInstruction) {
    emit(instruction, ::TableSuperInstructionDispatcher)
}
