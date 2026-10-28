package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.cir.ElementIndex
import io.github.charlietap.chasm.cir.TableIndex
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.i32Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.isImmediate
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot
import io.github.charlietap.chasm.cir.TableInstruction as CirTableInstruction

internal fun FunctionCompilationContext.emitTableGet(
    index: OperandSource,
    destinationSlot: Int,
    table: TableIndex,
) {
    if (index.sourceKind == OperandSourceKind.I32Immediate) {
        emit(CirTableInstruction.TableGetI(index.i32Immediate, destinationSlot, table))
    } else {
        emit(CirTableInstruction.TableGetS(index.sourceSlot, destinationSlot, table))
    }
}

internal fun FunctionCompilationContext.emitTableSet(
    value: OperandSource,
    index: OperandSource,
    table: TableIndex,
) {
    check(!value.isImmediate)
    val indexImmediate = index.sourceKind == OperandSourceKind.I32Immediate
    if (indexImmediate) {
        emit(CirTableInstruction.TableSetSi(value.sourceSlot, index.i32Immediate, table))
    } else {
        emit(CirTableInstruction.TableSetSs(value.sourceSlot, index.sourceSlot, table))
    }
}

internal fun FunctionCompilationContext.emitTableSize(
    table: TableIndex,
    destinationSlot: Int,
) {
    emit(CirTableInstruction.TableSizeS(destinationSlot, table))
}

internal fun FunctionCompilationContext.emitTableGrow(
    elements: OperandSource,
    value: OperandSource,
    destinationSlot: Int,
    table: TableIndex,
) {
    check(!value.isImmediate)
    val elementsImmediate = elements.sourceKind == OperandSourceKind.I32Immediate
    if (elementsImmediate) {
        emit(CirTableInstruction.TableGrowIs(elements.i32Immediate, value.sourceSlot, destinationSlot, table))
    } else {
        emit(CirTableInstruction.TableGrowSs(elements.sourceSlot, value.sourceSlot, destinationSlot, table))
    }
}

internal fun FunctionCompilationContext.emitTableCopy(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    sourceTable: TableIndex,
    destinationTable: TableIndex,
) = emitTableTernary(
    elements,
    sourceOffset,
    destinationOffset,
    { a, b, c -> emit(CirTableInstruction.TableCopyIii(a, b, c, sourceTable, destinationTable)) },
    { a, b, c -> emit(CirTableInstruction.TableCopyIis(a, b, c, sourceTable, destinationTable)) },
    { a, b, c -> emit(CirTableInstruction.TableCopyIsi(a, b, c, sourceTable, destinationTable)) },
    { a, b, c -> emit(CirTableInstruction.TableCopyIss(a, b, c, sourceTable, destinationTable)) },
    { a, b, c -> emit(CirTableInstruction.TableCopySii(a, b, c, sourceTable, destinationTable)) },
    { a, b, c -> emit(CirTableInstruction.TableCopySis(a, b, c, sourceTable, destinationTable)) },
    { a, b, c -> emit(CirTableInstruction.TableCopySsi(a, b, c, sourceTable, destinationTable)) },
    { a, b, c -> emit(CirTableInstruction.TableCopySss(a, b, c, sourceTable, destinationTable)) },
)

internal fun FunctionCompilationContext.emitTableInit(
    elements: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    element: ElementIndex,
    table: TableIndex,
) = emitTableTernary(
    elements,
    sourceOffset,
    destinationOffset,
    { a, b, c -> emit(CirTableInstruction.TableInitIii(a, b, c, element, table)) },
    { a, b, c -> emit(CirTableInstruction.TableInitIis(a, b, c, element, table)) },
    { a, b, c -> emit(CirTableInstruction.TableInitIsi(a, b, c, element, table)) },
    { a, b, c -> emit(CirTableInstruction.TableInitIss(a, b, c, element, table)) },
    { a, b, c -> emit(CirTableInstruction.TableInitSii(a, b, c, element, table)) },
    { a, b, c -> emit(CirTableInstruction.TableInitSis(a, b, c, element, table)) },
    { a, b, c -> emit(CirTableInstruction.TableInitSsi(a, b, c, element, table)) },
    { a, b, c -> emit(CirTableInstruction.TableInitSss(a, b, c, element, table)) },
)

internal fun FunctionCompilationContext.emitTableFill(
    elements: OperandSource,
    value: OperandSource,
    offset: OperandSource,
    table: TableIndex,
) {
    check(!value.isImmediate)
    val ai = elements.sourceKind == OperandSourceKind.I32Immediate
    val ci = offset.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) elements.i32Immediate else elements.sourceSlot
    val c = if (ci) offset.i32Immediate else offset.sourceSlot
    when {
        ai && ci -> emit(CirTableInstruction.TableFillIsi(a, value.sourceSlot, c, table))
        ai -> emit(CirTableInstruction.TableFillIss(a, value.sourceSlot, c, table))
        ci -> emit(CirTableInstruction.TableFillSsi(a, value.sourceSlot, c, table))
        else -> emit(CirTableInstruction.TableFillSss(a, value.sourceSlot, c, table))
    }
}

internal fun FunctionCompilationContext.emitElementDrop(
    element: ElementIndex,
) {
    emit(CirTableInstruction.ElemDrop(element))
}

private inline fun FunctionCompilationContext.emitTableTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> Unit,
    iis: (Int, Int, Int) -> Unit,
    isi: (Int, Int, Int) -> Unit,
    iss: (Int, Int, Int) -> Unit,
    sii: (Int, Int, Int) -> Unit,
    sis: (Int, Int, Int) -> Unit,
    ssi: (Int, Int, Int) -> Unit,
    sss: (Int, Int, Int) -> Unit,
) {
    val ai = first.sourceKind == OperandSourceKind.I32Immediate
    val bi = second.sourceKind == OperandSourceKind.I32Immediate
    val ci = third.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) first.i32Immediate else first.sourceSlot
    val b = if (bi) second.i32Immediate else second.sourceSlot
    val c = if (ci) third.i32Immediate else third.sourceSlot
    when {
        ai && bi && ci -> iii(a, b, c)
        ai && bi -> iis(a, b, c)
        ai && ci -> isi(a, b, c)
        ai -> iss(a, b, c)
        bi && ci -> sii(a, b, c)
        bi -> sis(a, b, c)
        ci -> ssi(a, b, c)
        else -> sss(a, b, c)
    }
}
