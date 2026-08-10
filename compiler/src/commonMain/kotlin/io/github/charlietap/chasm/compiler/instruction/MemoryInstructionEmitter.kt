package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.f32Immediate
import io.github.charlietap.chasm.compiler.operand.f64Immediate
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.DataDropDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.MemorySuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.ast.instruction.MemArg as AstMemArg
import io.github.charlietap.chasm.runtime.instruction.MemArg as RuntimeMemArg
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction as RuntimeMemoryInstruction

internal fun FunctionCompilationContext.emitMemoryLoad(
    instruction: MemoryInstruction.Load,
    address: OperandSource,
    destinationSlot: Int,
    memory: MemoryInstance,
) {
    val memArg = instruction.memArg.toRuntime()
    val immediate = address.sourceKind == OperandSourceKind.I32Immediate
    val addressValue = address.i32Immediate
    val sourceSlot = address.sourceSlot
    val runtimeInstruction = when (instruction) {
        is MemoryInstruction.Load.I32.I32Load -> if (immediate) MemorySuperInstruction.I32LoadI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I32LoadS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I32.I32Load8S -> if (immediate) MemorySuperInstruction.I32Load8SI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I32Load8SS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I32.I32Load8U -> if (immediate) MemorySuperInstruction.I32Load8UI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I32Load8US(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I32.I32Load16S -> if (immediate) MemorySuperInstruction.I32Load16SI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I32Load16SS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I32.I32Load16U -> if (immediate) MemorySuperInstruction.I32Load16UI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I32Load16US(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I64.I64Load -> if (immediate) MemorySuperInstruction.I64LoadI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I64LoadS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I64.I64Load8S -> if (immediate) MemorySuperInstruction.I64Load8SI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I64Load8SS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I64.I64Load8U -> if (immediate) MemorySuperInstruction.I64Load8UI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I64Load8US(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I64.I64Load16S -> if (immediate) MemorySuperInstruction.I64Load16SI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I64Load16SS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I64.I64Load16U -> if (immediate) MemorySuperInstruction.I64Load16UI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I64Load16US(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I64.I64Load32S -> if (immediate) MemorySuperInstruction.I64Load32SI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I64Load32SS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.I64.I64Load32U -> if (immediate) MemorySuperInstruction.I64Load32UI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.I64Load32US(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.F32.F32Load -> if (immediate) MemorySuperInstruction.F32LoadI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.F32LoadS(sourceSlot, destinationSlot, memory, memArg)
        is MemoryInstruction.Load.F64.F64Load -> if (immediate) MemorySuperInstruction.F64LoadI(addressValue, destinationSlot, memory, memArg) else MemorySuperInstruction.F64LoadS(sourceSlot, destinationSlot, memory, memArg)
    }
    program.append(MemorySuperInstructionDispatcher(runtimeInstruction))
}

internal fun FunctionCompilationContext.emitMemoryStore(
    instruction: MemoryInstruction.Store,
    value: OperandSource,
    address: OperandSource,
    memory: MemoryInstance,
) {
    val memArg = instruction.memArg.toRuntime()
    val addressImmediate = address.sourceKind == OperandSourceKind.I32Immediate
    val addressValue = address.i32Immediate
    val runtimeInstruction = when (instruction) {
        is MemoryInstruction.Store.I32.I32Store -> i32Store(value, addressImmediate, addressValue, memory, memArg, MemorySuperInstruction::I32StoreIi, MemorySuperInstruction::I32StoreIs, MemorySuperInstruction::I32StoreSi, MemorySuperInstruction::I32StoreSs)
        is MemoryInstruction.Store.I32.I32Store8 -> i32Store(value, addressImmediate, addressValue, memory, memArg, MemorySuperInstruction::I32Store8Ii, MemorySuperInstruction::I32Store8Is, MemorySuperInstruction::I32Store8Si, MemorySuperInstruction::I32Store8Ss)
        is MemoryInstruction.Store.I32.I32Store16 -> i32Store(value, addressImmediate, addressValue, memory, memArg, MemorySuperInstruction::I32Store16Ii, MemorySuperInstruction::I32Store16Is, MemorySuperInstruction::I32Store16Si, MemorySuperInstruction::I32Store16Ss)
        is MemoryInstruction.Store.I64.I64Store -> i64Store(value, addressImmediate, addressValue, memory, memArg, MemorySuperInstruction::I64StoreIi, MemorySuperInstruction::I64StoreIs, MemorySuperInstruction::I64StoreSi, MemorySuperInstruction::I64StoreSs)
        is MemoryInstruction.Store.I64.I64Store8 -> i64Store(value, addressImmediate, addressValue, memory, memArg, MemorySuperInstruction::I64Store8Ii, MemorySuperInstruction::I64Store8Is, MemorySuperInstruction::I64Store8Si, MemorySuperInstruction::I64Store8Ss)
        is MemoryInstruction.Store.I64.I64Store16 -> i64Store(value, addressImmediate, addressValue, memory, memArg, MemorySuperInstruction::I64Store16Ii, MemorySuperInstruction::I64Store16Is, MemorySuperInstruction::I64Store16Si, MemorySuperInstruction::I64Store16Ss)
        is MemoryInstruction.Store.I64.I64Store32 -> i64Store(value, addressImmediate, addressValue, memory, memArg, MemorySuperInstruction::I64Store32Ii, MemorySuperInstruction::I64Store32Is, MemorySuperInstruction::I64Store32Si, MemorySuperInstruction::I64Store32Ss)
        is MemoryInstruction.Store.F32.F32Store -> f32Store(value, addressImmediate, addressValue, memory, memArg)
        is MemoryInstruction.Store.F64.F64Store -> f64Store(value, addressImmediate, addressValue, memory, memArg)
    }
    program.append(MemorySuperInstructionDispatcher(runtimeInstruction))
}

internal fun FunctionCompilationContext.emitMemorySize(memory: MemoryInstance, destinationSlot: Int) {
    program.append(MemorySuperInstructionDispatcher(MemorySuperInstruction.MemorySizeS(destinationSlot, memory)))
}

internal fun FunctionCompilationContext.emitMemoryGrow(
    pages: OperandSource,
    memory: MemoryInstance,
    destinationSlot: Int,
) {
    val max = memory.type.limits.max?.toInt() ?: LinearMemory.MAX_PAGES
    val instruction = if (pages.sourceKind == OperandSourceKind.I32Immediate) {
        MemorySuperInstruction.MemoryGrowI(pages.i32Immediate, destinationSlot, memory, max)
    } else {
        MemorySuperInstruction.MemoryGrowS(pages.sourceSlot, destinationSlot, memory, max)
    }
    program.append(MemorySuperInstructionDispatcher(instruction))
}

internal fun FunctionCompilationContext.emitMemoryInit(
    bytes: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    memory: MemoryInstance,
    data: DataInstance,
) = emitMemoryTernary(
    bytes,
    sourceOffset,
    destinationOffset,
    { a, b, c -> MemorySuperInstruction.MemoryInitIii(a, b, c, memory, data) },
    { a, b, c -> MemorySuperInstruction.MemoryInitIis(a, b, c, memory, data) },
    { a, b, c -> MemorySuperInstruction.MemoryInitIsi(a, b, c, memory, data) },
    { a, b, c -> MemorySuperInstruction.MemoryInitIss(a, b, c, memory, data) },
    { a, b, c -> MemorySuperInstruction.MemoryInitSii(a, b, c, memory, data) },
    { a, b, c -> MemorySuperInstruction.MemoryInitSis(a, b, c, memory, data) },
    { a, b, c -> MemorySuperInstruction.MemoryInitSsi(a, b, c, memory, data) },
    { a, b, c -> MemorySuperInstruction.MemoryInitSss(a, b, c, memory, data) },
)

internal fun FunctionCompilationContext.emitMemoryCopy(
    bytes: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    sourceMemory: MemoryInstance,
    destinationMemory: MemoryInstance,
) = emitMemoryTernary(
    bytes,
    sourceOffset,
    destinationOffset,
    { a, b, c -> MemorySuperInstruction.MemoryCopyIii(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> MemorySuperInstruction.MemoryCopyIis(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> MemorySuperInstruction.MemoryCopyIsi(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> MemorySuperInstruction.MemoryCopyIss(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> MemorySuperInstruction.MemoryCopySii(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> MemorySuperInstruction.MemoryCopySis(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> MemorySuperInstruction.MemoryCopySsi(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> MemorySuperInstruction.MemoryCopySss(a, b, c, sourceMemory, destinationMemory) },
)

internal fun FunctionCompilationContext.emitMemoryFill(
    bytes: OperandSource,
    value: OperandSource,
    offset: OperandSource,
    memory: MemoryInstance,
) = emitMemoryTernary(
    bytes,
    value,
    offset,
    { a, b, c -> MemorySuperInstruction.MemoryFillIii(a, b, c, memory) },
    { a, b, c -> MemorySuperInstruction.MemoryFillIis(a, b, c, memory) },
    { a, b, c -> MemorySuperInstruction.MemoryFillIsi(a, b, c, memory) },
    { a, b, c -> MemorySuperInstruction.MemoryFillIss(a, b, c, memory) },
    { a, b, c -> MemorySuperInstruction.MemoryFillSii(a, b, c, memory) },
    { a, b, c -> MemorySuperInstruction.MemoryFillSis(a, b, c, memory) },
    { a, b, c -> MemorySuperInstruction.MemoryFillSsi(a, b, c, memory) },
    { a, b, c -> MemorySuperInstruction.MemoryFillSss(a, b, c, memory) },
)

internal fun FunctionCompilationContext.emitDataDrop(data: DataInstance) {
    program.append(DataDropDispatcher(RuntimeMemoryInstruction.DataDrop(data)))
}

private inline fun FunctionCompilationContext.emitMemoryTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> MemorySuperInstruction,
    iis: (Int, Int, Int) -> MemorySuperInstruction,
    isi: (Int, Int, Int) -> MemorySuperInstruction,
    iss: (Int, Int, Int) -> MemorySuperInstruction,
    sii: (Int, Int, Int) -> MemorySuperInstruction,
    sis: (Int, Int, Int) -> MemorySuperInstruction,
    ssi: (Int, Int, Int) -> MemorySuperInstruction,
    sss: (Int, Int, Int) -> MemorySuperInstruction,
) {
    val ai = first.sourceKind == OperandSourceKind.I32Immediate
    val bi = second.sourceKind == OperandSourceKind.I32Immediate
    val ci = third.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) first.i32Immediate else first.sourceSlot
    val b = if (bi) second.i32Immediate else second.sourceSlot
    val c = if (ci) third.i32Immediate else third.sourceSlot
    val instruction = when {
        ai && bi && ci -> iii(a, b, c)
        ai && bi -> iis(a, b, c)
        ai && ci -> isi(a, b, c)
        ai -> iss(a, b, c)
        bi && ci -> sii(a, b, c)
        bi -> sis(a, b, c)
        ci -> ssi(a, b, c)
        else -> sss(a, b, c)
    }
    program.append(MemorySuperInstructionDispatcher(instruction))
}

private inline fun i32Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryInstance,
    memArg: RuntimeMemArg,
    ii: (Int, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
    `is`: (Int, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
    si: (Int, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
    ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
): MemorySuperInstruction {
    val immediate = value.sourceKind == OperandSourceKind.I32Immediate
    val valueBits = if (immediate) value.i32Immediate else value.sourceSlot
    return when {
        immediate && addressImmediate -> ii(valueBits, address, memory, memArg)
        immediate -> `is`(valueBits, address, memory, memArg)
        addressImmediate -> si(valueBits, address, memory, memArg)
        else -> ss(valueBits, address, memory, memArg)
    }
}

private inline fun i64Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryInstance,
    memArg: RuntimeMemArg,
    ii: (Long, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
    `is`: (Long, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
    si: (Int, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
    ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> MemorySuperInstruction,
): MemorySuperInstruction {
    val immediate = value.sourceKind == OperandSourceKind.I64Immediate
    val immediateValue = value.i64Immediate
    val valueSlot = value.sourceSlot
    return when {
        immediate && addressImmediate -> ii(immediateValue, address, memory, memArg)
        immediate -> `is`(immediateValue, address, memory, memArg)
        addressImmediate -> si(valueSlot, address, memory, memArg)
        else -> ss(valueSlot, address, memory, memArg)
    }
}

private fun f32Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryInstance,
    memArg: RuntimeMemArg,
): MemorySuperInstruction {
    val immediate = value.sourceKind == OperandSourceKind.F32Immediate
    return when {
        immediate && addressImmediate -> MemorySuperInstruction.F32StoreIi(value.f32Immediate, address, memory, memArg)
        immediate -> MemorySuperInstruction.F32StoreIs(value.f32Immediate, address, memory, memArg)
        addressImmediate -> MemorySuperInstruction.F32StoreSi(value.sourceSlot, address, memory, memArg)
        else -> MemorySuperInstruction.F32StoreSs(value.sourceSlot, address, memory, memArg)
    }
}

private fun f64Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryInstance,
    memArg: RuntimeMemArg,
): MemorySuperInstruction {
    val immediate = value.sourceKind == OperandSourceKind.F64Immediate
    return when {
        immediate && addressImmediate -> MemorySuperInstruction.F64StoreIi(value.f64Immediate, address, memory, memArg)
        immediate -> MemorySuperInstruction.F64StoreIs(value.f64Immediate, address, memory, memArg)
        addressImmediate -> MemorySuperInstruction.F64StoreSi(value.sourceSlot, address, memory, memArg)
        else -> MemorySuperInstruction.F64StoreSs(value.sourceSlot, address, memory, memArg)
    }
}

private fun AstMemArg.toRuntime() = RuntimeMemArg(
    align = align.toInt(),
    offset = offset.toInt(),
)
