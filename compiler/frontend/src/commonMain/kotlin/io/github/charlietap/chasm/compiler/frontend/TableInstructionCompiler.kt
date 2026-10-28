package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.context.element
import io.github.charlietap.chasm.compiler.frontend.context.table
import io.github.charlietap.chasm.compiler.frontend.context.tableType
import io.github.charlietap.chasm.compiler.frontend.instruction.emitElementDrop
import io.github.charlietap.chasm.compiler.frontend.instruction.emitTableCopy
import io.github.charlietap.chasm.compiler.frontend.instruction.emitTableFill
import io.github.charlietap.chasm.compiler.frontend.instruction.emitTableGet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitTableGrow
import io.github.charlietap.chasm.compiler.frontend.instruction.emitTableInit
import io.github.charlietap.chasm.compiler.frontend.instruction.emitTableSet
import io.github.charlietap.chasm.compiler.frontend.instruction.emitTableSize
import io.github.charlietap.chasm.type.ValueType

internal fun compileTableInstruction(
    state: FunctionCompilationContext,
    instruction: TableInstruction,
): Unit = when (instruction) {
    is TableInstruction.TableGet -> {
        val index = state.pop()
        val table = state.compiler.table(instruction.tableIdx)
        val destination = destination(state, index)
        state.emitTableGet(index, destination.slot, table)
        completeDestination(state, ValueType.Reference(state.compiler.tableType(instruction.tableIdx).referenceType), destination)
    }
    is TableInstruction.TableSet -> {
        val value = state.pop()
        val index = state.pop()
        state.emitTableSet(
            value = value,
            index = index,
            table = state.compiler.table(instruction.tableIdx),
        )
    }
    is TableInstruction.TableInit -> {
        val elements = state.pop()
        val sourceOffset = state.pop()
        val destinationOffset = state.pop()
        state.emitTableInit(
            elements = elements,
            sourceOffset = sourceOffset,
            destinationOffset = destinationOffset,
            element = state.compiler.element(instruction.elemIdx),
            table = state.compiler.table(instruction.tableIdx),
        )
    }
    is TableInstruction.ElemDrop -> {
        state.emitElementDrop(state.compiler.element(instruction.elemIdx))
    }
    is TableInstruction.TableCopy -> {
        val elements = state.pop()
        val sourceOffset = state.pop()
        val destinationOffset = state.pop()
        state.emitTableCopy(
            elements = elements,
            sourceOffset = sourceOffset,
            destinationOffset = destinationOffset,
            sourceTable = state.compiler.table(instruction.srcTableIdx),
            destinationTable = state.compiler.table(instruction.destTableIdx),
        )
    }
    is TableInstruction.TableGrow -> {
        val elements = state.pop()
        val value = state.pop()
        val destination = destination(state, value)
        state.emitTableGrow(
            elements = elements,
            value = value,
            destinationSlot = destination.slot,
            table = state.compiler.table(instruction.tableIdx),
        )
        completeDestination(state, I32_TYPE, destination)
    }
    is TableInstruction.TableSize -> {
        val destination = destination(state, null)
        state.emitTableSize(state.compiler.table(instruction.tableIdx), destination.slot)
        completeDestination(state, I32_TYPE, destination)
    }
    is TableInstruction.TableFill -> {
        val elements = state.pop()
        val value = state.pop()
        val offset = state.pop()
        state.emitTableFill(
            elements = elements,
            value = value,
            offset = offset,
            table = state.compiler.table(instruction.tableIdx),
        )
    }
}
