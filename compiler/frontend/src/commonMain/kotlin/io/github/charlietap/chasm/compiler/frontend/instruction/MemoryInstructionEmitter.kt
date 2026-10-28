package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.cir.DataIndex
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.f32Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.f64Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.i32Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.i64Immediate
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot
import io.github.charlietap.chasm.ast.instruction.MemArg as AstMemArg
import io.github.charlietap.chasm.cir.MemArg as CirMemArg
import io.github.charlietap.chasm.cir.MemoryInstruction as CirMemoryInstruction

internal fun FunctionCompilationContext.emitMemoryLoad(
    instruction: MemoryInstruction.Load,
    address: OperandSource,
    destinationSlot: Int,
    memory: MemoryIndex,
) {
    val immediate = address.sourceKind == OperandSourceKind.I32Immediate
    val memArg = instruction.memArg.toCir()
    val precomputedAddress = if (immediate) precomputedEffectiveAddress(address.i32Immediate, memArg.offset) else null
    val addressValue = precomputedAddress ?: address.i32Immediate
    val cirMemArg = if (precomputedAddress == null) memArg else CirMemArg(0)
    val sourceSlot = address.sourceSlot
    when (instruction) {
        is MemoryInstruction.Load.I32.I32Load -> if (immediate) emit(CirMemoryInstruction.I32LoadI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I32LoadS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I32.I32Load8S -> if (immediate) emit(CirMemoryInstruction.I32Load8SI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I32Load8SS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I32.I32Load8U -> if (immediate) emit(CirMemoryInstruction.I32Load8UI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I32Load8US(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I32.I32Load16S -> if (immediate) emit(CirMemoryInstruction.I32Load16SI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I32Load16SS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I32.I32Load16U -> if (immediate) emit(CirMemoryInstruction.I32Load16UI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I32Load16US(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I64.I64Load -> if (immediate) emit(CirMemoryInstruction.I64LoadI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I64LoadS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I64.I64Load8S -> if (immediate) emit(CirMemoryInstruction.I64Load8SI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I64Load8SS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I64.I64Load8U -> if (immediate) emit(CirMemoryInstruction.I64Load8UI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I64Load8US(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I64.I64Load16S -> if (immediate) emit(CirMemoryInstruction.I64Load16SI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I64Load16SS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I64.I64Load16U -> if (immediate) emit(CirMemoryInstruction.I64Load16UI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I64Load16US(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I64.I64Load32S -> if (immediate) emit(CirMemoryInstruction.I64Load32SI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I64Load32SS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.I64.I64Load32U -> if (immediate) emit(CirMemoryInstruction.I64Load32UI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.I64Load32US(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.F32.F32Load -> if (immediate) emit(CirMemoryInstruction.F32LoadI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.F32LoadS(sourceSlot, destinationSlot, memory, cirMemArg))
        is MemoryInstruction.Load.F64.F64Load -> if (immediate) emit(CirMemoryInstruction.F64LoadI(addressValue, destinationSlot, memory, cirMemArg)) else emit(CirMemoryInstruction.F64LoadS(sourceSlot, destinationSlot, memory, cirMemArg))
    }
}

internal fun FunctionCompilationContext.emitMemoryStore(
    instruction: MemoryInstruction.Store,
    value: OperandSource,
    address: OperandSource,
    memory: MemoryIndex,
) {
    val addressImmediate = address.sourceKind == OperandSourceKind.I32Immediate
    val memArg = instruction.memArg.toCir()
    val precomputedAddress = if (addressImmediate) precomputedEffectiveAddress(address.i32Immediate, memArg.offset) else null
    val addressValue = precomputedAddress ?: address.i32Immediate
    val cirMemArg = if (precomputedAddress == null) memArg else CirMemArg(0)
    when (instruction) {
        is MemoryInstruction.Store.I32.I32Store -> i32Store(
            value,
            addressImmediate,
            addressValue,
            { bits, address -> emit(CirMemoryInstruction.I32StoreIi(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32StoreIs(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32StoreSi(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32StoreSs(bits, address, memory, cirMemArg)) },
        )
        is MemoryInstruction.Store.I32.I32Store8 -> i32Store(
            value,
            addressImmediate,
            addressValue,
            { bits, address -> emit(CirMemoryInstruction.I32Store8Ii(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32Store8Is(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32Store8Si(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32Store8Ss(bits, address, memory, cirMemArg)) },
        )
        is MemoryInstruction.Store.I32.I32Store16 -> i32Store(
            value,
            addressImmediate,
            addressValue,
            { bits, address -> emit(CirMemoryInstruction.I32Store16Ii(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32Store16Is(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32Store16Si(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I32Store16Ss(bits, address, memory, cirMemArg)) },
        )
        is MemoryInstruction.Store.I64.I64Store -> i64Store(
            value,
            addressImmediate,
            addressValue,
            { bits, address -> emit(CirMemoryInstruction.I64StoreIi(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64StoreIs(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64StoreSi(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64StoreSs(bits, address, memory, cirMemArg)) },
        )
        is MemoryInstruction.Store.I64.I64Store8 -> i64Store(
            value,
            addressImmediate,
            addressValue,
            { bits, address -> emit(CirMemoryInstruction.I64Store8Ii(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store8Is(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store8Si(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store8Ss(bits, address, memory, cirMemArg)) },
        )
        is MemoryInstruction.Store.I64.I64Store16 -> i64Store(
            value,
            addressImmediate,
            addressValue,
            { bits, address -> emit(CirMemoryInstruction.I64Store16Ii(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store16Is(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store16Si(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store16Ss(bits, address, memory, cirMemArg)) },
        )
        is MemoryInstruction.Store.I64.I64Store32 -> i64Store(
            value,
            addressImmediate,
            addressValue,
            { bits, address -> emit(CirMemoryInstruction.I64Store32Ii(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store32Is(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store32Si(bits, address, memory, cirMemArg)) },
            { bits, address -> emit(CirMemoryInstruction.I64Store32Ss(bits, address, memory, cirMemArg)) },
        )
        is MemoryInstruction.Store.F32.F32Store -> f32Store(value, addressImmediate, addressValue, memory, cirMemArg)
        is MemoryInstruction.Store.F64.F64Store -> f64Store(value, addressImmediate, addressValue, memory, cirMemArg)
    }
}

internal fun FunctionCompilationContext.emitMemorySize(
    memory: MemoryIndex,
    destinationSlot: Int,
) {
    emit(CirMemoryInstruction.MemorySizeS(destinationSlot, memory))
}

internal fun FunctionCompilationContext.emitMemoryGrow(
    pages: OperandSource,
    memory: MemoryIndex,
    destinationSlot: Int,
) {
    if (pages.sourceKind == OperandSourceKind.I32Immediate) {
        emit(CirMemoryInstruction.MemoryGrowI(pages.i32Immediate, destinationSlot, memory))
    } else {
        emit(CirMemoryInstruction.MemoryGrowS(pages.sourceSlot, destinationSlot, memory))
    }
}

internal fun FunctionCompilationContext.emitMemoryInit(
    bytes: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    memory: MemoryIndex,
    data: DataIndex,
) = emitMemoryTernary(
    bytes,
    sourceOffset,
    destinationOffset,
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitIii(a, b, c, memory, data)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitIis(a, b, c, memory, data)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitIsi(a, b, c, memory, data)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitIss(a, b, c, memory, data)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitSii(a, b, c, memory, data)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitSis(a, b, c, memory, data)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitSsi(a, b, c, memory, data)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryInitSss(a, b, c, memory, data)) },
)

internal fun FunctionCompilationContext.emitMemoryCopy(
    bytes: OperandSource,
    sourceOffset: OperandSource,
    destinationOffset: OperandSource,
    sourceMemory: MemoryIndex,
    destinationMemory: MemoryIndex,
) = emitMemoryTernary(
    bytes,
    sourceOffset,
    destinationOffset,
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopyIii(a, b, c, sourceMemory, destinationMemory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopyIis(a, b, c, sourceMemory, destinationMemory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopyIsi(a, b, c, sourceMemory, destinationMemory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopyIss(a, b, c, sourceMemory, destinationMemory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopySii(a, b, c, sourceMemory, destinationMemory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopySis(a, b, c, sourceMemory, destinationMemory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopySsi(a, b, c, sourceMemory, destinationMemory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryCopySss(a, b, c, sourceMemory, destinationMemory)) },
)

internal fun FunctionCompilationContext.emitMemoryFill(
    bytes: OperandSource,
    value: OperandSource,
    offset: OperandSource,
    memory: MemoryIndex,
) = emitMemoryTernary(
    bytes,
    value,
    offset,
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillIii(a, b, c, memory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillIis(a, b, c, memory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillIsi(a, b, c, memory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillIss(a, b, c, memory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillSii(a, b, c, memory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillSis(a, b, c, memory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillSsi(a, b, c, memory)) },
    { a, b, c -> emit(CirMemoryInstruction.MemoryFillSss(a, b, c, memory)) },
)

internal fun FunctionCompilationContext.emitDataDrop(
    data: DataIndex,
) {
    emit(CirMemoryInstruction.DataDrop(data))
}

private inline fun FunctionCompilationContext.emitMemoryTernary(
    first: OperandSource,
    second: OperandSource,
    third: OperandSource,
    iii: (Int, Int, Int) -> Unit,
    iis: (Int, Int, Int) -> Unit,
    isi: (Int, Int, Int) -> Unit,
    iss: (Int, Int, Int) -> Unit,
    sii: (Int, Int, Int) -> Unit,
    sis: (Int, Int, Int) -> Unit,
    ssi: (Int, Int, Int) -> Unit,
    sss: (Int, Int, Int) -> Unit,
) {
    val ai = first.sourceKind == OperandSourceKind.I32Immediate
    val bi = second.sourceKind == OperandSourceKind.I32Immediate
    val ci = third.sourceKind == OperandSourceKind.I32Immediate
    val a = if (ai) first.i32Immediate else first.sourceSlot
    val b = if (bi) second.i32Immediate else second.sourceSlot
    val c = if (ci) third.i32Immediate else third.sourceSlot
    when {
        ai && bi && ci -> iii(a, b, c)
        ai && bi -> iis(a, b, c)
        ai && ci -> isi(a, b, c)
        ai -> iss(a, b, c)
        bi && ci -> sii(a, b, c)
        bi -> sis(a, b, c)
        ci -> ssi(a, b, c)
        else -> sss(a, b, c)
    }
}

private inline fun FunctionCompilationContext.i32Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    ii: (Int, Int) -> Unit,
    `is`: (Int, Int) -> Unit,
    si: (Int, Int) -> Unit,
    ss: (Int, Int) -> Unit,
) {
    val immediate = value.sourceKind == OperandSourceKind.I32Immediate
    val valueBits = if (immediate) value.i32Immediate else value.sourceSlot
    when {
        immediate && addressImmediate -> ii(valueBits, address)
        immediate -> `is`(valueBits, address)
        addressImmediate -> si(valueBits, address)
        else -> ss(valueBits, address)
    }
}

private inline fun FunctionCompilationContext.i64Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    ii: (Long, Int) -> Unit,
    `is`: (Long, Int) -> Unit,
    si: (Int, Int) -> Unit,
    ss: (Int, Int) -> Unit,
) {
    val immediate = value.sourceKind == OperandSourceKind.I64Immediate
    val immediateValue = value.i64Immediate
    val valueSlot = value.sourceSlot
    when {
        immediate && addressImmediate -> ii(immediateValue, address)
        immediate -> `is`(immediateValue, address)
        addressImmediate -> si(valueSlot, address)
        else -> ss(valueSlot, address)
    }
}

private fun FunctionCompilationContext.f32Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryIndex,
    memArg: CirMemArg,
) {
    val immediate = value.sourceKind == OperandSourceKind.F32Immediate
    when {
        immediate && addressImmediate -> emit(CirMemoryInstruction.F32StoreIi(value.f32Immediate, address, memory, memArg))
        immediate -> emit(CirMemoryInstruction.F32StoreIs(value.f32Immediate, address, memory, memArg))
        addressImmediate -> emit(CirMemoryInstruction.F32StoreSi(value.sourceSlot, address, memory, memArg))
        else -> emit(CirMemoryInstruction.F32StoreSs(value.sourceSlot, address, memory, memArg))
    }
}

private fun FunctionCompilationContext.f64Store(
    value: OperandSource,
    addressImmediate: Boolean,
    address: Int,
    memory: MemoryIndex,
    memArg: CirMemArg,
) {
    val immediate = value.sourceKind == OperandSourceKind.F64Immediate
    when {
        immediate && addressImmediate -> emit(CirMemoryInstruction.F64StoreIi(value.f64Immediate, address, memory, memArg))
        immediate -> emit(CirMemoryInstruction.F64StoreIs(value.f64Immediate, address, memory, memArg))
        addressImmediate -> emit(CirMemoryInstruction.F64StoreSi(value.sourceSlot, address, memory, memArg))
        else -> emit(CirMemoryInstruction.F64StoreSs(value.sourceSlot, address, memory, memArg))
    }
}

private fun AstMemArg.toCir() = CirMemArg(offset.toInt())

internal fun precomputedEffectiveAddress(
    address: Int,
    offset: Int,
): Int? {
    return if (address >= 0 && offset >= 0 && address <= Int.MAX_VALUE - offset) address + offset else null
}
