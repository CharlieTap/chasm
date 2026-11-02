package io.github.charlietap.chasm.compiler.frontend.diagnostic

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction

/**
 * The index in [body] a trap is reported at when it happens in a compiled
 * instruction covering source instructions [start] until [end], or
 * [NO_SOURCE_INDEX] when the instruction has no source. A fused instruction
 * reports its first source instruction that can trap.
 */
fun trapSourceIndex(
    body: List<Instruction>,
    start: Int,
    end: Int,
): Int {
    if (start < 0 || start >= body.size || end <= start) return NO_SOURCE_INDEX
    for (index in start until minOf(end, body.size)) {
        if (body[index].canTrap()) return index
    }
    return start
}

/**
 * The memory [instruction] accesses when it is a memory instruction, packed
 * as `(first + 1) or ((second + 1) shl 16)`, where first is the memory, or a
 * copy's source, and second is a copy's destination; zero otherwise.
 */
fun trapSiteMemories(
    instruction: Instruction?,
): Int = when (instruction) {
    is MemoryInstruction.Load -> packMemories(instruction.memoryIndex.idx.toInt())
    is MemoryInstruction.Store -> packMemories(instruction.memoryIndex.idx.toInt())
    is MemoryInstruction.MemoryFill -> packMemories(instruction.memoryIndex.idx.toInt())
    is MemoryInstruction.MemoryInit -> packMemories(instruction.memoryIndex.idx.toInt())
    is MemoryInstruction.MemoryCopy -> packMemories(instruction.srcIndex.idx.toInt(), instruction.dstIndex.idx.toInt())
    else -> 0
}

// Indices too large to pack are left unknown, so the access is matched by its memory instead.
private fun packMemories(
    first: Int,
    second: Int = -1,
): Int = if (first + 1 > MAX_PACKED || second + 1 > MAX_PACKED) 0 else (first + 1) or ((second + 1) shl 16)

private const val MAX_PACKED = 0x7FFF

const val NO_SOURCE_INDEX = -1

private fun Instruction.canTrap(): Boolean = when (this) {
    ControlInstruction.Unreachable,
    is ControlInstruction.Call,
    is ControlInstruction.CallIndirect,
    is ControlInstruction.CallRef,
    is ControlInstruction.ReturnCall,
    is ControlInstruction.ReturnCallIndirect,
    is ControlInstruction.ReturnCallRef,
    is ControlInstruction.Throw,
    ControlInstruction.ThrowRef,
    -> true

    is MemoryInstruction.Load,
    is MemoryInstruction.Store,
    is MemoryInstruction.MemoryCopy,
    is MemoryInstruction.MemoryFill,
    is MemoryInstruction.MemoryInit,
    -> true

    is TableInstruction.TableGet,
    is TableInstruction.TableSet,
    is TableInstruction.TableCopy,
    is TableInstruction.TableFill,
    is TableInstruction.TableInit,
    -> true

    ReferenceInstruction.RefAsNonNull,
    is ReferenceInstruction.RefCast,
    -> true

    is AggregateInstruction -> true

    NumericInstruction.I32DivS,
    NumericInstruction.I32DivU,
    NumericInstruction.I32RemS,
    NumericInstruction.I32RemU,
    NumericInstruction.I64DivS,
    NumericInstruction.I64DivU,
    NumericInstruction.I64RemS,
    NumericInstruction.I64RemU,
    NumericInstruction.I32TruncF32S,
    NumericInstruction.I32TruncF32U,
    NumericInstruction.I32TruncF64S,
    NumericInstruction.I32TruncF64U,
    NumericInstruction.I64TruncF32S,
    NumericInstruction.I64TruncF32U,
    NumericInstruction.I64TruncF64S,
    NumericInstruction.I64TruncF64U,
    -> true

    else -> false
}
