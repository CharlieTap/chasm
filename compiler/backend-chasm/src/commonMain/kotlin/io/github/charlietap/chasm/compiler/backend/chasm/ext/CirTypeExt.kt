package io.github.charlietap.chasm.compiler.backend.chasm.ext

import io.github.charlietap.chasm.cir.AbstractHeapType
import io.github.charlietap.chasm.cir.Frame
import io.github.charlietap.chasm.cir.FusedOperand
import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.cir.OperandTransfer
import io.github.charlietap.chasm.cir.PackedType
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.cir.TailCallOperandTransfer
import io.github.charlietap.chasm.cir.ValueType
import io.github.charlietap.chasm.runtime.ext.default
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.runtime.instruction.FusedOperand as RuntimeFusedOperand
import io.github.charlietap.chasm.runtime.instruction.NumericCondition as RuntimeNumericCondition
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer as RuntimeOperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TailCallOperandTransfer as RuntimeTailCallOperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource as RuntimeTransferSource
import io.github.charlietap.chasm.type.AbstractHeapType as RuntimeAbstractHeapType
import io.github.charlietap.chasm.type.HeapType as RuntimeHeapType
import io.github.charlietap.chasm.type.PackedType as RuntimePackedType
import io.github.charlietap.chasm.type.ReferenceType as RuntimeReferenceType
import io.github.charlietap.chasm.type.ValueType as RuntimeValueType

internal fun PackedType.toRuntime(): RuntimePackedType = when (this) {
    PackedType.I8 -> RuntimePackedType.I8
    PackedType.I16 -> RuntimePackedType.I16
}

internal fun ReferenceType.toRuntime(): RuntimeReferenceType = if (nullable) {
    RuntimeReferenceType.RefNull(heapType.toRuntime())
} else {
    RuntimeReferenceType.Ref(heapType.toRuntime())
}

internal fun HeapType.toRuntime(): RuntimeHeapType = when (this) {
    is HeapType.Concrete -> ConcreteHeapType.TypeIndex(type.index)
    is HeapType.Abstract -> when (type) {
        AbstractHeapType.Func -> RuntimeAbstractHeapType.Func
        AbstractHeapType.NoFunc -> RuntimeAbstractHeapType.NoFunc
        AbstractHeapType.Extern -> RuntimeAbstractHeapType.Extern
        AbstractHeapType.NoExtern -> RuntimeAbstractHeapType.NoExtern
        AbstractHeapType.Exception -> RuntimeAbstractHeapType.Exception
        AbstractHeapType.NoException -> RuntimeAbstractHeapType.NoException
        AbstractHeapType.Any -> RuntimeAbstractHeapType.Any
        AbstractHeapType.Eq -> RuntimeAbstractHeapType.Eq
        AbstractHeapType.Struct -> RuntimeAbstractHeapType.Struct
        AbstractHeapType.Array -> RuntimeAbstractHeapType.Array
        AbstractHeapType.I31 -> RuntimeAbstractHeapType.I31
        AbstractHeapType.None -> RuntimeAbstractHeapType.None
    }
}

internal fun OperandTransfer.toRuntime(): RuntimeOperandTransfer =
    RuntimeOperandTransfer(runtimeSources(), destinationSlotBase)

internal fun TailCallOperandTransfer.toRuntime(): RuntimeTailCallOperandTransfer {
    val wasmSources = wasm.runtimeSources()
    val hostSources = if (host.values === wasm.values && host.immediates === wasm.immediates) wasmSources else host.runtimeSources()
    return RuntimeTailCallOperandTransfer(
        RuntimeOperandTransfer(wasmSources, wasm.destinationSlotBase),
        RuntimeOperandTransfer(hostSources, host.destinationSlotBase),
    )
}

private fun OperandTransfer.runtimeSources(): Array<RuntimeTransferSource> {
    val values = values
    val immediates = immediates
    return Array(values.size) { index ->
        if (immediates[index]) RuntimeTransferSource.Immediate(values[index]) else RuntimeTransferSource.Slot(values[index].toInt())
    }
}

internal fun NumericCondition.toRuntime(): RuntimeNumericCondition = when (this) {
    is NumericCondition.I32Eqz -> RuntimeNumericCondition.I32Eqz(operand.toRuntime())
    is NumericCondition.I32And -> RuntimeNumericCondition.I32And(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64Eqz -> RuntimeNumericCondition.I64Eqz(operand.toRuntime())
    is NumericCondition.I32Eq -> RuntimeNumericCondition.I32Eq(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32Ne -> RuntimeNumericCondition.I32Ne(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32LtS -> RuntimeNumericCondition.I32LtS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32LtU -> RuntimeNumericCondition.I32LtU(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32GtS -> RuntimeNumericCondition.I32GtS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32GtU -> RuntimeNumericCondition.I32GtU(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32LeS -> RuntimeNumericCondition.I32LeS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32LeU -> RuntimeNumericCondition.I32LeU(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32GeS -> RuntimeNumericCondition.I32GeS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I32GeU -> RuntimeNumericCondition.I32GeU(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64Eq -> RuntimeNumericCondition.I64Eq(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64Ne -> RuntimeNumericCondition.I64Ne(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64LtS -> RuntimeNumericCondition.I64LtS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64LtU -> RuntimeNumericCondition.I64LtU(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64GtS -> RuntimeNumericCondition.I64GtS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64GtU -> RuntimeNumericCondition.I64GtU(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64LeS -> RuntimeNumericCondition.I64LeS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64LeU -> RuntimeNumericCondition.I64LeU(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64GeS -> RuntimeNumericCondition.I64GeS(left.toRuntime(), right.toRuntime())
    is NumericCondition.I64GeU -> RuntimeNumericCondition.I64GeU(left.toRuntime(), right.toRuntime())
    is NumericCondition.F32Eq -> RuntimeNumericCondition.F32Eq(left.toRuntime(), right.toRuntime())
    is NumericCondition.F32Ne -> RuntimeNumericCondition.F32Ne(left.toRuntime(), right.toRuntime())
    is NumericCondition.F32Lt -> RuntimeNumericCondition.F32Lt(left.toRuntime(), right.toRuntime())
    is NumericCondition.F32Gt -> RuntimeNumericCondition.F32Gt(left.toRuntime(), right.toRuntime())
    is NumericCondition.F32Le -> RuntimeNumericCondition.F32Le(left.toRuntime(), right.toRuntime())
    is NumericCondition.F32Ge -> RuntimeNumericCondition.F32Ge(left.toRuntime(), right.toRuntime())
    is NumericCondition.F64Eq -> RuntimeNumericCondition.F64Eq(left.toRuntime(), right.toRuntime())
    is NumericCondition.F64Ne -> RuntimeNumericCondition.F64Ne(left.toRuntime(), right.toRuntime())
    is NumericCondition.F64Lt -> RuntimeNumericCondition.F64Lt(left.toRuntime(), right.toRuntime())
    is NumericCondition.F64Gt -> RuntimeNumericCondition.F64Gt(left.toRuntime(), right.toRuntime())
    is NumericCondition.F64Le -> RuntimeNumericCondition.F64Le(left.toRuntime(), right.toRuntime())
    is NumericCondition.F64Ge -> RuntimeNumericCondition.F64Ge(left.toRuntime(), right.toRuntime())
}

private fun FusedOperand.toRuntime(): RuntimeFusedOperand = when (this) {
    is FusedOperand.I32Const -> RuntimeFusedOperand.I32Const(const)
    is FusedOperand.I64Const -> RuntimeFusedOperand.I64Const(const)
    is FusedOperand.F32Const -> RuntimeFusedOperand.F32Const(const)
    is FusedOperand.F64Const -> RuntimeFusedOperand.F64Const(const)
    is FusedOperand.FrameSlot -> RuntimeFusedOperand.FrameSlot(offset)
}

internal fun Frame.localInitialValues(): LongArray {
    if (initialisedLocalCount == 0) return NO_INITIAL_VALUES
    return LongArray(initialisedLocalCount) { index ->
        when (val type = locals[index]) {
            is ValueType.Reference -> RuntimeValueType.Reference(type.type.toRuntime()).default()
            else -> 0L
        }
    }
}

private val NO_INITIAL_VALUES = LongArray(0)
