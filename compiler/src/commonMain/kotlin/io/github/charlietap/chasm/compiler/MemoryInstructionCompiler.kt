package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.context.data
import io.github.charlietap.chasm.compiler.context.memory
import io.github.charlietap.chasm.compiler.instruction.emitDataDrop
import io.github.charlietap.chasm.compiler.instruction.emitMemoryCopy
import io.github.charlietap.chasm.compiler.instruction.emitMemoryFill
import io.github.charlietap.chasm.compiler.instruction.emitMemoryGrow
import io.github.charlietap.chasm.compiler.instruction.emitMemoryInit
import io.github.charlietap.chasm.compiler.instruction.emitMemoryLoad
import io.github.charlietap.chasm.compiler.instruction.emitMemorySize
import io.github.charlietap.chasm.compiler.instruction.emitMemoryStore

internal fun compileMemoryInstruction(
    state: FunctionCompilationContext,
    instruction: MemoryInstruction,
    nextInstruction: Instruction?,
): Boolean = when (instruction) {
    is MemoryInstruction.Load -> {
        val address = state.pop()
        val resultType = when (instruction) {
            is MemoryInstruction.Load.I32 -> I32_TYPE
            is MemoryInstruction.Load.I64 -> I64_TYPE
            is MemoryInstruction.Load.F32 -> F32_TYPE
            is MemoryInstruction.Load.F64 -> F64_TYPE
        }
        val destination = destination(state, address, nextInstruction)
        state.emitMemoryLoad(
            instruction = instruction,
            address = address,
            destinationSlot = destination.slot,
            memory = state.compiler.memory(instruction.memoryIndex),
        )
        completeDestination(state, resultType, destination)
        destination.consumesNextInstruction
    }
    is MemoryInstruction.Store -> {
        val value = state.pop()
        val address = state.pop()
        state.emitMemoryStore(
            instruction = instruction,
            value = value,
            address = address,
            memory = state.compiler.memory(instruction.memoryIndex),
        )
        false
    }
    is MemoryInstruction.MemorySize -> {
        val destination = destination(state, null, nextInstruction)
        state.emitMemorySize(state.compiler.memory(instruction.memoryIndex), destination.slot)
        completeDestination(state, I32_TYPE, destination)
        destination.consumesNextInstruction
    }
    is MemoryInstruction.MemoryGrow -> {
        val pages = state.pop()
        val destination = destination(state, pages, nextInstruction)
        state.emitMemoryGrow(
            pages = pages,
            memory = state.compiler.memory(instruction.memoryIndex),
            destinationSlot = destination.slot,
        )
        completeDestination(state, I32_TYPE, destination)
        destination.consumesNextInstruction
    }
    is MemoryInstruction.MemoryInit -> {
        val bytes = state.pop()
        val sourceOffset = state.pop()
        val destinationOffset = state.pop()
        state.emitMemoryInit(
            bytes = bytes,
            sourceOffset = sourceOffset,
            destinationOffset = destinationOffset,
            memory = state.compiler.memory(instruction.memoryIndex),
            data = state.compiler.data(instruction.dataIndex),
        )
        false
    }
    is MemoryInstruction.DataDrop -> {
        state.emitDataDrop(state.compiler.data(instruction.dataIdx))
        false
    }
    is MemoryInstruction.MemoryCopy -> {
        val bytes = state.pop()
        val sourceOffset = state.pop()
        val destinationOffset = state.pop()
        state.emitMemoryCopy(
            bytes = bytes,
            sourceOffset = sourceOffset,
            destinationOffset = destinationOffset,
            sourceMemory = state.compiler.memory(instruction.srcIndex),
            destinationMemory = state.compiler.memory(instruction.dstIndex),
        )
        false
    }
    is MemoryInstruction.MemoryFill -> {
        val bytes = state.pop()
        val value = state.pop()
        val offset = state.pop()
        state.emitMemoryFill(
            bytes = bytes,
            value = value,
            offset = offset,
            memory = state.compiler.memory(instruction.memoryIndex),
        )
        false
    }
}
