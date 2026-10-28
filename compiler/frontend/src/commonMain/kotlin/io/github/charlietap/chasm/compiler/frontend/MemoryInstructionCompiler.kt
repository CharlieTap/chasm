package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.context.data
import io.github.charlietap.chasm.compiler.frontend.context.memory
import io.github.charlietap.chasm.compiler.frontend.instruction.emitDataDrop
import io.github.charlietap.chasm.compiler.frontend.instruction.emitMemoryCopy
import io.github.charlietap.chasm.compiler.frontend.instruction.emitMemoryFill
import io.github.charlietap.chasm.compiler.frontend.instruction.emitMemoryGrow
import io.github.charlietap.chasm.compiler.frontend.instruction.emitMemoryInit
import io.github.charlietap.chasm.compiler.frontend.instruction.emitMemoryLoad
import io.github.charlietap.chasm.compiler.frontend.instruction.emitMemorySize
import io.github.charlietap.chasm.compiler.frontend.instruction.emitMemoryStore

internal fun compileMemoryInstruction(
    state: FunctionCompilationContext,
    instruction: MemoryInstruction,
): Unit = when (instruction) {
    is MemoryInstruction.Load -> {
        val address = state.pop()
        val resultType = when (instruction) {
            is MemoryInstruction.Load.I32 -> I32_TYPE
            is MemoryInstruction.Load.I64 -> I64_TYPE
            is MemoryInstruction.Load.F32 -> F32_TYPE
            is MemoryInstruction.Load.F64 -> F64_TYPE
        }
        val destination = destination(state, address)
        state.emitMemoryLoad(
            instruction = instruction,
            address = address,
            destinationSlot = destination.slot,
            memory = state.compiler.memory(instruction.memoryIndex),
        )
        completeDestination(state, resultType, destination)
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
    }
    is MemoryInstruction.MemorySize -> {
        val destination = destination(state, null)
        state.emitMemorySize(state.compiler.memory(instruction.memoryIndex), destination.slot)
        completeDestination(state, I32_TYPE, destination)
    }
    is MemoryInstruction.MemoryGrow -> {
        val pages = state.pop()
        val destination = destination(state, pages)
        state.emitMemoryGrow(
            pages = pages,
            memory = state.compiler.memory(instruction.memoryIndex),
            destinationSlot = destination.slot,
        )
        completeDestination(state, I32_TYPE, destination)
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
    }
    is MemoryInstruction.DataDrop -> {
        state.emitDataDrop(state.compiler.data(instruction.dataIdx))
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
    }
}
