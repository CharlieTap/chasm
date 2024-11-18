package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.module.Index
import kotlin.jvm.JvmInline

sealed interface AtomicMemoryInstruction : Instruction {

    data class I32Load(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Load(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32Store(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Store(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I32Load8U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Load8U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32Load16U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Load16U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Load32U(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I32Store8(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Store8(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32Store16(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Store16(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64Store32(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I32ReadModifyWriteAdd(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWriteSub(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWriteAnd(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWriteOr(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWriteXor(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWriteExchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I64ReadModifyWriteAdd(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWriteSub(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWriteAnd(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWriteOr(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWriteXor(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWriteExchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I32ReadModifyWrite8Add(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite8Sub(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite8And(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite8Or(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite8Xor(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite8Exchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I64ReadModifyWrite8Add(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite8Sub(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite8And(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite8Or(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite8Xor(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite8Exchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I32ReadModifyWrite16Add(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite16Sub(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite16And(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite16Or(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite16Xor(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32ReadModifyWrite16Exchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I64ReadModifyWrite16Add(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite16Sub(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite16And(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite16Or(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite16Xor(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite16Exchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I64ReadModifyWrite32Add(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite32Sub(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite32And(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite32Or(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite32Xor(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64ReadModifyWrite32Exchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I32CompareExchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64CompareExchange(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32CompareExchange8(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64CompareExchange8(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I32CompareExchange16(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64CompareExchange16(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction
    data class I64CompareExchange32(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    @JvmInline
    value class Notify(val memoryIndex: Index.MemoryIndex) : AtomicMemoryInstruction

    @JvmInline
    value class I32Wait(val memoryIndex: Index.MemoryIndex) : AtomicMemoryInstruction

    @JvmInline
    value class I64Wait(val memoryIndex: Index.MemoryIndex) : AtomicMemoryInstruction

    data object Fence : AtomicMemoryInstruction
}
