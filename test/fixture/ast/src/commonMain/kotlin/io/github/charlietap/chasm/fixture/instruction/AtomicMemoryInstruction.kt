package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.module.memoryIndex

fun atomicMemoryInstruction(): AtomicMemoryInstruction = atomicFenceInstruction()

fun atomicNotifyInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Notify(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicWaitInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.I32Wait(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicWaitInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.I64Wait(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun atomicFenceInstruction() = AtomicMemoryInstruction.Fence

fun i32AtomicLoadInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Load.I32.I32Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicLoadInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Load.I64.I64Load(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicStoreInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Store.I32.I32Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicStoreInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Store.I64.I64Store(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicLoad8UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Load.I32.I32Load8U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicLoad8UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Load.I64.I64Load8U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicLoad16UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Load.I32.I32Load16U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicLoad16UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Load.I64.I64Load16U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicLoad32UInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Load.I64.I64Load32U(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicStore8Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Store.I32.I32Store8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicStore8Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Store.I64.I64Store8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicStore16Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Store.I32.I32Store16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicStore16Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Store.I64.I64Store16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicStore32Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.Store.I64.I64Store32(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWriteAddInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAdd(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWriteAddInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAdd(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWriteSubInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteSub(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWriteSubInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteSub(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWriteAndInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAnd(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWriteAndInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAnd(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWriteOrInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteOr(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWriteOrInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteOr(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWriteXorInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteXor(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWriteXorInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteXor(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWriteExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteExchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWriteExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteExchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite8AddInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Add(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite8SubInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Sub(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite8AndInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8And(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite8OrInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Or(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite8XorInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Xor(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite8ExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Exchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite8AddInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Add(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite8SubInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Sub(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite8AndInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8And(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite8OrInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Or(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite8XorInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Xor(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite8ExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Exchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite16AddInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Add(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite16SubInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Sub(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite16AndInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16And(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite16OrInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Or(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite16XorInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Xor(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicReadModifyWrite16ExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Exchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite16AddInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Add(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite16SubInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Sub(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite16AndInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16And(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite16OrInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Or(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite16XorInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Xor(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite16ExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Exchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite32AddInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Add(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite32SubInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Sub(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite32AndInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32And(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite32OrInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Or(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite32XorInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Xor(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicReadModifyWrite32ExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Exchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicCompareExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicCompareExchangeInstruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicCompareExchange8Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicCompareExchange8Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange8(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i32AtomicCompareExchange16Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicCompareExchange16Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange16(
    memoryIndex = memoryIndex,
    memArg = memArg,
)

fun i64AtomicCompareExchange32Instruction(
    memoryIndex: Index.MemoryIndex = memoryIndex(),
    memArg: MemArg = memArg(),
) = AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange32(
    memoryIndex = memoryIndex,
    memArg = memArg,
)
