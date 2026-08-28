package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.isImmediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.table.TableInstructionDispatcher
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.instruction.TableInstruction as RuntimeTableInstruction

internal fun FunctionCompilationContext.emitTableGet(
    index: OperandSource,
    destinationSlot: Int,
    table: TableInstance,
) {
    val instruction = if (index.sourceKind == OperandSourceKind.I32Immediate) {
        RuntimeTableInstruction.TableGetI(index.i32Immediate, destinationSlot, table)
    } else {
        RuntimeTableInstruction.TableGetS(index.sourceSlot, destinationSlot, table)
    }
    emitTableInstruction(instruction)
}

internal fun FunctionCompilationContext.emitTableSet(
    value: OperandSource,
    index: OperandSource,
    table: TableInstance,
) {
    check(!value.isImmediate)
    val indexImmediate = index.sourceKind == OperandSourceKind.I32Immediate
    val instruction = if (indexImmediate) {
        RuntimeTableInstruction.TableSetSi(value.sourceSlot, index.i32Immediate, table)
    } else {
        RuntimeTableInstruction.TableSetSs(value.sourceSlot, index.sourceSlot, table)
    }
    emitTableInstruction(instruction)
}

internal fun FunctionCompilationContext.emitTableSize(table: TableInstance, destinationSlot: Int) {
    emitTableInstruction(RuntimeTableInstruction.TableSizeS(destinationSlot, table))
}

internal fun FunctionCompilationContext.emitTableGrow(
    elements: OperandSource,
    value: OperandSource,
    destinationSlot: Int,
    table: TableInstance,
) {
    check(!value.isImmediate)
    val elementsImmediate = elements.sourceKind == OperandSourceKind.I32Immediate
    val max = table.type.limits.max?.toInt() ?: Int.MAX_VALUE
    val instruction = if (elementsImmediate) {
        RuntimeTableInstruction.TableGrowIs(elements.i32Immediate, value.sourceSlot, destinationSlot, table, max)
    } else {
        RuntimeTableInstruction.TableGrowSs(elements.sourceSlot, value.sourceSlot, destinationSlot, table, max)
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
    { a, b, c -> RuntimeTableInstruction.TableCopyIii(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> RuntimeTableInstruction.TableCopyIis(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> RuntimeTableInstruction.TableCopyIsi(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> RuntimeTableInstruction.TableCopyIss(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> RuntimeTableInstruction.TableCopySii(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> RuntimeTableInstruction.TableCopySis(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> RuntimeTableInstruction.TableCopySsi(a, b, c, sourceTable, destinationTable) },
    { a, b, c -> RuntimeTableInstruction.TableCopySss(a, b, c, sourceTable, destinationTable) },
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
    { a, b, c -> RuntimeTableInstruction.TableInitIii(a, b, c, element, table) },
    { a, b, c -> RuntimeTableInstruction.TableInitIis(a, b, c, element, table) },
    { a, b, c -> RuntimeTableInstruction.TableInitIsi(a, b, c, element, table) },
    { a, b, c -> RuntimeTableInstruction.TableInitIss(a, b, c, element, table) },
    { a, b, c -> RuntimeTableInstruction.TableInitSii(a, b, c, element, table) },
    { a, b, c -> RuntimeTableInstruction.TableInitSis(a, b, c, element, table) },
    { a, b, c -> RuntimeTableInstruction.TableInitSsi(a, b, c, element, table) },
    { a, b, c -> RuntimeTableInstruction.TableInitSss(a, b, c, element, table) },
)

internal fun FunctionCompilationContext.emitTableFill(
    elements: OperandSource,
    value: OperandSource,
    offset: OperandSource,
    table: TableInstance,
) {
    check(!value.isImmediate)
    val ai = elements.sourceKind == OperandSourceKind.I32Immediate
    val ci = offset.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) elements.i32Immediate else elements.sourceSlot
    val c = if (ci) offset.i32Immediate else offset.sourceSlot
    val instruction = when {
        ai && ci -> RuntimeTableInstruction.TableFillIsi(a, value.sourceSlot, c, table)
        ai -> RuntimeTableInstruction.TableFillIss(a, value.sourceSlot, c, table)
        ci -> RuntimeTableInstruction.TableFillSsi(a, value.sourceSlot, c, table)
        else -> RuntimeTableInstruction.TableFillSss(a, value.sourceSlot, c, table)
    }
    emitTableInstruction(instruction)
}

internal fun FunctionCompilationContext.emitElementDrop(element: ElementInstance) {
    val instruction = RuntimeTableInstruction.ElemDrop(element)
    emitTableInstruction(instruction)
}

private inline fun FunctionCompilationContext.emitTableTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> RuntimeTableInstruction,
    iis: (Int, Int, Int) -> RuntimeTableInstruction,
    isi: (Int, Int, Int) -> RuntimeTableInstruction,
    iss: (Int, Int, Int) -> RuntimeTableInstruction,
    sii: (Int, Int, Int) -> RuntimeTableInstruction,
    sis: (Int, Int, Int) -> RuntimeTableInstruction,
    ssi: (Int, Int, Int) -> RuntimeTableInstruction,
    sss: (Int, Int, Int) -> RuntimeTableInstruction,
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

private fun FunctionCompilationContext.emitTableInstruction(instruction: RuntimeTableInstruction) {
    emit(instruction, ::TableInstructionDispatcher)
}
