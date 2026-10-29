package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.F32Load
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.F32Store
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.F64Load
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.F64Store
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Load
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Load16S
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Load16U
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Load8S
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Load8U
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Store
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Store16
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I32Store8
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Load
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Load16S
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Load16U
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Load32S
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Load32U
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Load8S
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Load8U
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Store
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Store16
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Store32
import io.github.charlietap.chasm.compiler.backend.analysis.form.MemoryOperation.I64Store8
import io.github.charlietap.chasm.type.NumberType

class MemoryAccess(
    val operation: MemoryOperation,
    val address: Operand,
    val value: Operand?,
    val destination: Int,
    val memory: MemoryIndex,
    val offset: Int,
) {
    val isLoad: Boolean get() = value == null
}

enum class MemoryOperation(
    val type: NumberType,
    val width: Int,
    val signed: Boolean = false,
) {
    I32Load(NumberType.I32, 4),
    I64Load(NumberType.I64, 8),
    F32Load(NumberType.F32, 4),
    F64Load(NumberType.F64, 8),
    I32Load8S(NumberType.I32, 1, signed = true),
    I32Load8U(NumberType.I32, 1),
    I32Load16S(NumberType.I32, 2, signed = true),
    I32Load16U(NumberType.I32, 2),
    I64Load8S(NumberType.I64, 1, signed = true),
    I64Load8U(NumberType.I64, 1),
    I64Load16S(NumberType.I64, 2, signed = true),
    I64Load16U(NumberType.I64, 2),
    I64Load32S(NumberType.I64, 4, signed = true),
    I64Load32U(NumberType.I64, 4),
    I32Store(NumberType.I32, 4),
    I64Store(NumberType.I64, 8),
    F32Store(NumberType.F32, 4),
    F64Store(NumberType.F64, 8),
    I32Store8(NumberType.I32, 1),
    I32Store16(NumberType.I32, 2),
    I64Store8(NumberType.I64, 1),
    I64Store16(NumberType.I64, 2),
    I64Store32(NumberType.I64, 4),
    ;

    val helperName: String = name.replaceFirstChar { it.lowercase() }
}

fun MemoryInstruction.access(): MemoryAccess? = when (val i = this) {
    is MemoryInstruction.I32LoadI -> load(I32Load, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32LoadS -> load(I32Load, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64LoadI -> load(I64Load, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64LoadS -> load(I64Load, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.F32LoadI -> load(F32Load, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.F32LoadS -> load(F32Load, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.F64LoadI -> load(F64Load, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.F64LoadS -> load(F64Load, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load8SI -> load(I32Load8S, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load8SS -> load(I32Load8S, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load8UI -> load(I32Load8U, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load8US -> load(I32Load8U, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load16SI -> load(I32Load16S, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load16SS -> load(I32Load16S, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load16UI -> load(I32Load16U, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I32Load16US -> load(I32Load16U, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load8SI -> load(I64Load8S, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load8SS -> load(I64Load8S, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load8UI -> load(I64Load8U, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load8US -> load(I64Load8U, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load16SI -> load(I64Load16S, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load16SS -> load(I64Load16S, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load16UI -> load(I64Load16U, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load16US -> load(I64Load16U, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load32SI -> load(I64Load32S, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load32SS -> load(I64Load32S, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load32UI -> load(I64Load32U, immediate(i.address), i.destinationSlot, i.memory, i.memArg.offset)
    is MemoryInstruction.I64Load32US -> load(I64Load32U, slot(i.addressSlot), i.destinationSlot, i.memory, i.memArg.offset)

    is MemoryInstruction.I32StoreIi -> store(I32Store, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I32StoreIs -> store(I32Store, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I32StoreSi -> store(I32Store, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I32StoreSs -> store(I32Store, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64StoreIi -> store(I64Store, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64StoreIs -> store(I64Store, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64StoreSi -> store(I64Store, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64StoreSs -> store(I64Store, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.F32StoreIi -> store(F32Store, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.F32StoreIs -> store(F32Store, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.F32StoreSi -> store(F32Store, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.F32StoreSs -> store(F32Store, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.F64StoreIi -> store(F64Store, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.F64StoreIs -> store(F64Store, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.F64StoreSi -> store(F64Store, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.F64StoreSs -> store(F64Store, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store8Ii -> store(I32Store8, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store8Is -> store(I32Store8, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store8Si -> store(I32Store8, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store8Ss -> store(I32Store8, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store16Ii -> store(I32Store16, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store16Is -> store(I32Store16, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store16Si -> store(I32Store16, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I32Store16Ss -> store(I32Store16, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store8Ii -> store(I64Store8, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store8Is -> store(I64Store8, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store8Si -> store(I64Store8, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store8Ss -> store(I64Store8, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store16Ii -> store(I64Store16, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store16Is -> store(I64Store16, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store16Si -> store(I64Store16, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store16Ss -> store(I64Store16, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store32Ii -> store(I64Store32, immediate(i.address), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store32Is -> store(I64Store32, slot(i.addressSlot), immediate(i.value), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store32Si -> store(I64Store32, immediate(i.address), slot(i.valueSlot), i.memory, i.memArg.offset)
    is MemoryInstruction.I64Store32Ss -> store(I64Store32, slot(i.addressSlot), slot(i.valueSlot), i.memory, i.memArg.offset)
    else -> null
}

private fun load(
    operation: MemoryOperation,
    address: Operand,
    destination: Int,
    memory: MemoryIndex,
    offset: Int,
) =
    MemoryAccess(operation, address, null, destination, memory, offset)

private fun store(
    operation: MemoryOperation,
    address: Operand,
    value: Operand,
    memory: MemoryIndex,
    offset: Int,
) =
    MemoryAccess(operation, address, value, NO_DESTINATION, memory, offset)

private const val NO_DESTINATION = -1
