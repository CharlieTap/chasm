package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.context.element
import io.github.charlietap.chasm.compiler.context.table
import io.github.charlietap.chasm.compiler.instruction.emitElementDrop
import io.github.charlietap.chasm.compiler.instruction.emitTableCopy
import io.github.charlietap.chasm.compiler.instruction.emitTableFill
import io.github.charlietap.chasm.compiler.instruction.emitTableGet
import io.github.charlietap.chasm.compiler.instruction.emitTableGrow
import io.github.charlietap.chasm.compiler.instruction.emitTableInit
import io.github.charlietap.chasm.compiler.instruction.emitTableSet
import io.github.charlietap.chasm.compiler.instruction.emitTableSize
import io.github.charlietap.chasm.type.ValueType

internal fun compileTableInstruction(
    state: FunctionCompilationContext,
    instruction: TableInstruction,
    nextInstruction: Instruction?,
): Boolean = when (instruction) {
    is TableInstruction.TableGet -> {
        val index = state.pop()
        val table = state.compiler.table(instruction.tableIdx)
        val destination = destination(state, index, nextInstruction)
        state.emitTableGet(index, destination.slot, table)
        completeDestination(state, ValueType.Reference(table.type.referenceType), destination)
        destination.consumesNextInstruction
    }
    is TableInstruction.TableSet -> {
        val value = state.pop()
        val index = state.pop()
        state.emitTableSet(
            value = value,
            index = index,
            table = state.compiler.table(instruction.tableIdx),
        )
        false
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
        false
    }
    is TableInstruction.ElemDrop -> {
        state.emitElementDrop(state.compiler.element(instruction.elemIdx))
        false
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
        false
    }
    is TableInstruction.TableGrow -> {
        val elements = state.pop()
        val value = state.pop()
        val destination = destination(state, value, nextInstruction)
        state.emitTableGrow(
            elements = elements,
            value = value,
            destinationSlot = destination.slot,
            table = state.compiler.table(instruction.tableIdx),
        )
        completeDestination(state, I32_TYPE, destination)
        destination.consumesNextInstruction
    }
    is TableInstruction.TableSize -> {
        val destination = destination(state, null, nextInstruction)
        state.emitTableSize(state.compiler.table(instruction.tableIdx), destination.slot)
        completeDestination(state, I32_TYPE, destination)
        destination.consumesNextInstruction
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
        false
    }
}
