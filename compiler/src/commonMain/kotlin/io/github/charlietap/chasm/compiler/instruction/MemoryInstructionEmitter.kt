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
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInstructionDispatcher
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
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
    val immediate = address.sourceKind == OperandSourceKind.I32Immediate
    val memArg = instruction.memArg.toRuntime()
    val precomputedAddress = if (immediate) precomputedEffectiveAddress(address.i32Immediate, memArg.offset) else null
    val addressValue = precomputedAddress ?: address.i32Immediate
    val runtimeMemArg = if (precomputedAddress == null) memArg else RuntimeMemArg(0)
    val sourceSlot = address.sourceSlot
    val runtimeInstruction = when (instruction) {
        is MemoryInstruction.Load.I32.I32Load -> if (immediate) RuntimeMemoryInstruction.I32LoadI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I32LoadS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I32.I32Load8S -> if (immediate) RuntimeMemoryInstruction.I32Load8SI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I32Load8SS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I32.I32Load8U -> if (immediate) RuntimeMemoryInstruction.I32Load8UI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I32Load8US(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I32.I32Load16S -> if (immediate) RuntimeMemoryInstruction.I32Load16SI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I32Load16SS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I32.I32Load16U -> if (immediate) RuntimeMemoryInstruction.I32Load16UI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I32Load16US(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I64.I64Load -> if (immediate) RuntimeMemoryInstruction.I64LoadI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I64LoadS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I64.I64Load8S -> if (immediate) RuntimeMemoryInstruction.I64Load8SI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I64Load8SS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I64.I64Load8U -> if (immediate) RuntimeMemoryInstruction.I64Load8UI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I64Load8US(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I64.I64Load16S -> if (immediate) RuntimeMemoryInstruction.I64Load16SI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I64Load16SS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I64.I64Load16U -> if (immediate) RuntimeMemoryInstruction.I64Load16UI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I64Load16US(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I64.I64Load32S -> if (immediate) RuntimeMemoryInstruction.I64Load32SI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I64Load32SS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.I64.I64Load32U -> if (immediate) RuntimeMemoryInstruction.I64Load32UI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.I64Load32US(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.F32.F32Load -> if (immediate) RuntimeMemoryInstruction.F32LoadI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.F32LoadS(sourceSlot, destinationSlot, memory, runtimeMemArg)
        is MemoryInstruction.Load.F64.F64Load -> if (immediate) RuntimeMemoryInstruction.F64LoadI(addressValue, destinationSlot, memory, runtimeMemArg) else RuntimeMemoryInstruction.F64LoadS(sourceSlot, destinationSlot, memory, runtimeMemArg)
    }
    emit(runtimeInstruction, ::MemoryInstructionDispatcher)
}

internal fun FunctionCompilationContext.emitMemoryStore(
    instruction: MemoryInstruction.Store,
    value: OperandSource,
    address: OperandSource,
    memory: MemoryInstance,
) {
    val addressImmediate = address.sourceKind == OperandSourceKind.I32Immediate
    val memArg = instruction.memArg.toRuntime()
    val precomputedAddress = if (addressImmediate) precomputedEffectiveAddress(address.i32Immediate, memArg.offset) else null
    val addressValue = precomputedAddress ?: address.i32Immediate
    val runtimeMemArg = if (precomputedAddress == null) memArg else RuntimeMemArg(0)
    val runtimeInstruction = when (instruction) {
        is MemoryInstruction.Store.I32.I32Store -> i32Store(value, addressImmediate, addressValue, memory, runtimeMemArg, RuntimeMemoryInstruction::I32StoreIi, RuntimeMemoryInstruction::I32StoreIs, RuntimeMemoryInstruction::I32StoreSi, RuntimeMemoryInstruction::I32StoreSs)
        is MemoryInstruction.Store.I32.I32Store8 -> i32Store(value, addressImmediate, addressValue, memory, runtimeMemArg, RuntimeMemoryInstruction::I32Store8Ii, RuntimeMemoryInstruction::I32Store8Is, RuntimeMemoryInstruction::I32Store8Si, RuntimeMemoryInstruction::I32Store8Ss)
        is MemoryInstruction.Store.I32.I32Store16 -> i32Store(value, addressImmediate, addressValue, memory, runtimeMemArg, RuntimeMemoryInstruction::I32Store16Ii, RuntimeMemoryInstruction::I32Store16Is, RuntimeMemoryInstruction::I32Store16Si, RuntimeMemoryInstruction::I32Store16Ss)
        is MemoryInstruction.Store.I64.I64Store -> i64Store(value, addressImmediate, addressValue, memory, runtimeMemArg, RuntimeMemoryInstruction::I64StoreIi, RuntimeMemoryInstruction::I64StoreIs, RuntimeMemoryInstruction::I64StoreSi, RuntimeMemoryInstruction::I64StoreSs)
        is MemoryInstruction.Store.I64.I64Store8 -> i64Store(value, addressImmediate, addressValue, memory, runtimeMemArg, RuntimeMemoryInstruction::I64Store8Ii, RuntimeMemoryInstruction::I64Store8Is, RuntimeMemoryInstruction::I64Store8Si, RuntimeMemoryInstruction::I64Store8Ss)
        is MemoryInstruction.Store.I64.I64Store16 -> i64Store(value, addressImmediate, addressValue, memory, runtimeMemArg, RuntimeMemoryInstruction::I64Store16Ii, RuntimeMemoryInstruction::I64Store16Is, RuntimeMemoryInstruction::I64Store16Si, RuntimeMemoryInstruction::I64Store16Ss)
        is MemoryInstruction.Store.I64.I64Store32 -> i64Store(value, addressImmediate, addressValue, memory, runtimeMemArg, RuntimeMemoryInstruction::I64Store32Ii, RuntimeMemoryInstruction::I64Store32Is, RuntimeMemoryInstruction::I64Store32Si, RuntimeMemoryInstruction::I64Store32Ss)
        is MemoryInstruction.Store.F32.F32Store -> f32Store(value, addressImmediate, addressValue, memory, runtimeMemArg)
        is MemoryInstruction.Store.F64.F64Store -> f64Store(value, addressImmediate, addressValue, memory, runtimeMemArg)
    }
    emit(runtimeInstruction, ::MemoryInstructionDispatcher)
}

internal fun FunctionCompilationContext.emitMemorySize(memory: MemoryInstance, destinationSlot: Int) {
    val instruction = RuntimeMemoryInstruction.MemorySizeS(destinationSlot, memory)
    emit(instruction, ::MemoryInstructionDispatcher)
}

internal fun FunctionCompilationContext.emitMemoryGrow(
    pages: OperandSource,
    memory: MemoryInstance,
    destinationSlot: Int,
) {
    val max = memory.type.limits.max?.toInt() ?: LinearMemory.MAX_PAGES
    val instruction = if (pages.sourceKind == OperandSourceKind.I32Immediate) {
        RuntimeMemoryInstruction.MemoryGrowI(pages.i32Immediate, destinationSlot, memory, max)
    } else {
        RuntimeMemoryInstruction.MemoryGrowS(pages.sourceSlot, destinationSlot, memory, max)
    }
    emit(instruction, ::MemoryInstructionDispatcher)
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
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitIii(a, b, c, memory, data) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitIis(a, b, c, memory, data) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitIsi(a, b, c, memory, data) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitIss(a, b, c, memory, data) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitSii(a, b, c, memory, data) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitSis(a, b, c, memory, data) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitSsi(a, b, c, memory, data) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryInitSss(a, b, c, memory, data) },
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
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopyIii(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopyIis(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopyIsi(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopyIss(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopySii(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopySis(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopySsi(a, b, c, sourceMemory, destinationMemory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryCopySss(a, b, c, sourceMemory, destinationMemory) },
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
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillIii(a, b, c, memory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillIis(a, b, c, memory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillIsi(a, b, c, memory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillIss(a, b, c, memory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillSii(a, b, c, memory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillSis(a, b, c, memory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillSsi(a, b, c, memory) },
    { a, b, c -> RuntimeMemoryInstruction.MemoryFillSss(a, b, c, memory) },
)

internal fun FunctionCompilationContext.emitDataDrop(data: DataInstance) {
    val instruction = RuntimeMemoryInstruction.DataDrop(data)
    emit(instruction, ::MemoryInstructionDispatcher)
}

private inline fun FunctionCompilationContext.emitMemoryTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> RuntimeMemoryInstruction,
    iis: (Int, Int, Int) -> RuntimeMemoryInstruction,
    isi: (Int, Int, Int) -> RuntimeMemoryInstruction,
    iss: (Int, Int, Int) -> RuntimeMemoryInstruction,
    sii: (Int, Int, Int) -> RuntimeMemoryInstruction,
    sis: (Int, Int, Int) -> RuntimeMemoryInstruction,
    ssi: (Int, Int, Int) -> RuntimeMemoryInstruction,
    sss: (Int, Int, Int) -> RuntimeMemoryInstruction,
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
    emit(instruction, ::MemoryInstructionDispatcher)
}

private inline fun i32Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryInstance,
    memArg: RuntimeMemArg,
    ii: (Int, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
    `is`: (Int, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
    si: (Int, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
    ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
): RuntimeMemoryInstruction {
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
    ii: (Long, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
    `is`: (Long, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
    si: (Int, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
    ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> RuntimeMemoryInstruction,
): RuntimeMemoryInstruction {
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
): RuntimeMemoryInstruction {
    val immediate = value.sourceKind == OperandSourceKind.F32Immediate
    return when {
        immediate && addressImmediate -> RuntimeMemoryInstruction.F32StoreIi(value.f32Immediate, address, memory, memArg)
        immediate -> RuntimeMemoryInstruction.F32StoreIs(value.f32Immediate, address, memory, memArg)
        addressImmediate -> RuntimeMemoryInstruction.F32StoreSi(value.sourceSlot, address, memory, memArg)
        else -> RuntimeMemoryInstruction.F32StoreSs(value.sourceSlot, address, memory, memArg)
    }
}

private fun f64Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryInstance,
    memArg: RuntimeMemArg,
): RuntimeMemoryInstruction {
    val immediate = value.sourceKind == OperandSourceKind.F64Immediate
    return when {
        immediate && addressImmediate -> RuntimeMemoryInstruction.F64StoreIi(value.f64Immediate, address, memory, memArg)
        immediate -> RuntimeMemoryInstruction.F64StoreIs(value.f64Immediate, address, memory, memArg)
        addressImmediate -> RuntimeMemoryInstruction.F64StoreSi(value.sourceSlot, address, memory, memArg)
        else -> RuntimeMemoryInstruction.F64StoreSs(value.sourceSlot, address, memory, memArg)
    }
}

private fun AstMemArg.toRuntime() = RuntimeMemArg(offset.toInt())

internal fun precomputedEffectiveAddress(address: Int, offset: Int): Int? {
    return if (address >= 0 && offset >= 0 && address <= Int.MAX_VALUE - offset) address + offset else null
}
