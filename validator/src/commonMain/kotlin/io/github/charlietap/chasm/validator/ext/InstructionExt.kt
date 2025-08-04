package io.github.charlietap.chasm.validator.ext

import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction

fun Instruction?.size(): Int = when (this) {

    is AtomicMemoryInstruction.Notify -> 4
    is AtomicMemoryInstruction.I32Wait -> 4
    is AtomicMemoryInstruction.I64Wait -> 8

    is AtomicMemoryInstruction.Load.I32.I32Load -> 4
    is AtomicMemoryInstruction.Load.I32.I32Load8U -> 1
    is AtomicMemoryInstruction.Load.I32.I32Load16U -> 2
    is AtomicMemoryInstruction.Load.I64.I64Load -> 8
    is AtomicMemoryInstruction.Load.I64.I64Load8U -> 1
    is AtomicMemoryInstruction.Load.I64.I64Load16U -> 2
    is AtomicMemoryInstruction.Load.I64.I64Load32U -> 4

    is AtomicMemoryInstruction.Store.I32.I32Store -> 4
    is AtomicMemoryInstruction.Store.I32.I32Store8 -> 1
    is AtomicMemoryInstruction.Store.I32.I32Store16 -> 2
    is AtomicMemoryInstruction.Store.I64.I64Store -> 8
    is AtomicMemoryInstruction.Store.I64.I64Store8 -> 1
    is AtomicMemoryInstruction.Store.I64.I64Store16 -> 2
    is AtomicMemoryInstruction.Store.I64.I64Store32 -> 4

    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAdd -> 4
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Add -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Add -> 2

    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAnd -> 4
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8And -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16And -> 2

    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteExchange -> 4
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Exchange -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Exchange -> 2

    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteOr -> 4
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Or -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Or -> 2

    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteSub -> 4
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Sub -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Sub -> 2

    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteXor -> 4
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Xor -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Xor -> 2

    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAdd -> 8
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Add -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Add -> 2
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Add -> 4

    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAnd -> 8
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8And -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16And -> 2
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32And -> 4

    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteExchange -> 8
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Exchange -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Exchange -> 2
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Exchange -> 4

    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteOr -> 8
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Or -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Or -> 2
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Or -> 4

    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteSub -> 8
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Sub -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Sub -> 2
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Sub -> 4

    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteXor -> 8
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Xor -> 1
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Xor -> 2
    is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Xor -> 4

    is AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange -> 4
    is AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange8 -> 1
    is AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange16 -> 2

    is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange -> 8
    is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange8 -> 1
    is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange16 -> 2
    is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange32 -> 4

    is MemoryInstruction.Load.I32Load -> 4
    is MemoryInstruction.Load.I32Load8S -> 1
    is MemoryInstruction.Load.I32Load8U -> 1
    is MemoryInstruction.Load.I32Load16S -> 2
    is MemoryInstruction.Load.I32Load16U -> 2

    is MemoryInstruction.Load.I64Load -> 8
    is MemoryInstruction.Load.I64Load8S -> 1
    is MemoryInstruction.Load.I64Load8U -> 1
    is MemoryInstruction.Load.I64Load16S -> 2
    is MemoryInstruction.Load.I64Load16U -> 2
    is MemoryInstruction.Load.I64Load32S -> 4
    is MemoryInstruction.Load.I64Load32U -> 4

    is MemoryInstruction.Store.I32Store -> 4
    is MemoryInstruction.Store.I32Store8 -> 1
    is MemoryInstruction.Store.I32Store16 -> 2
    is MemoryInstruction.Store.I64Store -> 8
    is MemoryInstruction.Store.I64Store8 -> 1
    is MemoryInstruction.Store.I64Store16 -> 2
    is MemoryInstruction.Store.I64Store32 -> 4

    is MemoryInstruction.Load.F32Load -> 4
    is MemoryInstruction.Load.F64Load -> 8
    is MemoryInstruction.Store.F32Store -> 4
    is MemoryInstruction.Store.F64Store -> 8

    else -> 0
}
