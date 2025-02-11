package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ast.module.Index

sealed interface AtomicMemoryInstruction : Instruction {

    sealed interface Load : AtomicMemoryInstruction {

        val memoryIndex: Index.MemoryIndex
        val memArg: MemArg

        sealed interface I32 : Load {
            data class I32Load(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32Load8U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32Load16U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32
        }

        sealed interface I64 : Load {
            data class I64Load(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64Load8U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64Load16U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64Load32U(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64
        }
    }

    sealed interface Store : AtomicMemoryInstruction {

        val memoryIndex: Index.MemoryIndex
        val memArg: MemArg

        sealed interface I32 : Store {
            data class I32Store(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32Store8(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32Store16(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32
        }

        sealed interface I64 : Store {
            data class I64Store(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64Store8(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64Store16(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64Store32(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64
        }
    }

    sealed interface ReadModifyWrite : AtomicMemoryInstruction {

        val memoryIndex: Index.MemoryIndex
        val memArg: MemArg

        sealed interface I32 : ReadModifyWrite {

            data class I32ReadModifyWriteAdd(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWriteSub(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWriteAnd(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWriteOr(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWriteXor(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWriteExchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite8Add(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite8Sub(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite8And(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite8Or(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite8Xor(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite8Exchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite16Add(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite16Sub(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite16And(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite16Or(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite16Xor(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32ReadModifyWrite16Exchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32
        }

        sealed interface I64 : ReadModifyWrite {

            data class I64ReadModifyWrite8Add(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite8Sub(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite8And(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite8Or(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite8Xor(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite8Exchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWriteAdd(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWriteSub(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWriteAnd(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWriteOr(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWriteXor(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWriteExchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite16Add(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite16Sub(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite16And(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite16Or(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite16Xor(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite16Exchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite32Add(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite32Sub(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite32And(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite32Or(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite32Xor(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64ReadModifyWrite32Exchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64
        }
    }

    sealed interface CompareExchange : AtomicMemoryInstruction {

        val memoryIndex: Index.MemoryIndex
        val memArg: MemArg

        sealed interface I32 : CompareExchange {
            data class I32CompareExchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32CompareExchange8(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32

            data class I32CompareExchange16(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I32
        }

        sealed interface I64 : CompareExchange {
            data class I64CompareExchange(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64CompareExchange8(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64CompareExchange16(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64

            data class I64CompareExchange32(override val memoryIndex: Index.MemoryIndex, override val memArg: MemArg) : I64
        }
    }

    data class Notify(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I32Wait(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data class I64Wait(val memoryIndex: Index.MemoryIndex, val memArg: MemArg) : AtomicMemoryInstruction

    data object Fence : AtomicMemoryInstruction
}
