package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction as IRAtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.MemArg as IRMemArg
import io.github.charlietap.chasm.ir.module.Index.MemoryIndex as IRMemoryIndex

internal fun AtomicMemoryInstructionFactory(
    instruction: AtomicMemoryInstruction,
): IRAtomicMemoryInstruction = AtomicMemoryInstructionFactory(
    instruction = instruction,
    memoryIndexFactory = ::MemoryIndexFactory,
    memArgFactory = ::MemArgFactory,
)

internal inline fun AtomicMemoryInstructionFactory(
    instruction: AtomicMemoryInstruction,
    memoryIndexFactory: IRFactory<Index.MemoryIndex, IRMemoryIndex>,
    memArgFactory: IRFactory<MemArg, IRMemArg>,
): IRAtomicMemoryInstruction {
    return when (instruction) {
        is AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange -> IRAtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange16 -> IRAtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange16(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange8 -> IRAtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange8(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange -> IRAtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange16 -> IRAtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange16(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange32 -> IRAtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange32(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange8 -> IRAtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange8(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        AtomicMemoryInstruction.Fence -> IRAtomicMemoryInstruction.Fence
        is AtomicMemoryInstruction.I32Wait -> IRAtomicMemoryInstruction.I32Wait(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.I64Wait -> IRAtomicMemoryInstruction.I64Wait(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Load.I32.I32Load -> IRAtomicMemoryInstruction.Load.I32.I32Load(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Load.I32.I32Load16U -> IRAtomicMemoryInstruction.Load.I32.I32Load16U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Load.I32.I32Load8U -> IRAtomicMemoryInstruction.Load.I32.I32Load8U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Load.I64.I64Load -> IRAtomicMemoryInstruction.Load.I64.I64Load(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Load.I64.I64Load16U -> IRAtomicMemoryInstruction.Load.I64.I64Load16U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Load.I64.I64Load32U -> IRAtomicMemoryInstruction.Load.I64.I64Load32U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Load.I64.I64Load8U -> IRAtomicMemoryInstruction.Load.I64.I64Load8U(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Notify -> IRAtomicMemoryInstruction.Notify(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Store.I32.I32Store -> IRAtomicMemoryInstruction.Store.I32.I32Store(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Store.I32.I32Store16 -> IRAtomicMemoryInstruction.Store.I32.I32Store16(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Store.I32.I32Store8 -> IRAtomicMemoryInstruction.Store.I32.I32Store8(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Store.I64.I64Store -> IRAtomicMemoryInstruction.Store.I64.I64Store(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Store.I64.I64Store16 -> IRAtomicMemoryInstruction.Store.I64.I64Store16(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Store.I64.I64Store32 -> IRAtomicMemoryInstruction.Store.I64.I64Store32(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.Store.I64.I64Store8 -> IRAtomicMemoryInstruction.Store.I64.I64Store8(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Add -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Add(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16And -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16And(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Exchange -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Exchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Or -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Or(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Sub -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Sub(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Xor -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Xor(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Add -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Add(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8And -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8And(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Exchange -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Exchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Or -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Or(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Sub -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Sub(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Xor -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Xor(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAdd -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAdd(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAnd -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAnd(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteExchange -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteExchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteOr -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteOr(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteSub -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteSub(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteXor -> IRAtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteXor(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Add -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Add(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16And -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16And(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Exchange -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Exchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Or -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Or(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Sub -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Sub(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Xor -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Xor(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Add -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Add(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32And -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32And(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Exchange -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Exchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Or -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Or(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Sub -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Sub(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Xor -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Xor(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Add -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Add(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8And -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8And(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Exchange -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Exchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Or -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Or(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Sub -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Sub(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Xor -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Xor(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAdd -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAdd(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAnd -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAnd(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteExchange -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteExchange(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteOr -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteOr(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteSub -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteSub(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
        is AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteXor -> IRAtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteXor(
            memoryIndex = memoryIndexFactory(instruction.memoryIndex),
            memArg = memArgFactory(instruction.memArg),
        )
    }
}
