package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.FusedNumericInstruction
import io.github.charlietap.chasm.ir.instruction.FusedParametricInstruction
import io.github.charlietap.chasm.ir.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.FusedTableInstruction
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.instruction.MemoryInstruction
import io.github.charlietap.chasm.ir.instruction.NumericInstruction
import io.github.charlietap.chasm.ir.instruction.ParametricInstruction
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ir.instruction.TableInstruction
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.ir.instruction.VectorInstruction
import io.github.charlietap.chasm.optimiser.passes.PassContextt
import io.github.charlietap.chasm.type.ext.asFunctionType
import io.github.charlietap.chasm.type.ext.asStructType

internal typealias StackDifferential = (PassContextt, Instruction) -> Int

internal fun StackDifferential(
    context: PassContextt,
    instruction: Instruction,
): Int {
    return when (instruction) {
        is AdminInstruction -> 0
        is AggregateInstruction.ArrayCopy -> -5
        is AggregateInstruction.ArrayFill -> -4
        is AggregateInstruction.ArrayGet,
        is AggregateInstruction.ArrayGetSigned,
        is AggregateInstruction.ArrayGetUnsigned,
        -> -1
        is AggregateInstruction.ArrayInitData,
        is AggregateInstruction.ArrayInitElement,
        -> -4
        is AggregateInstruction.ArrayNew,
        is AggregateInstruction.ArrayNewData,
        is AggregateInstruction.ArrayNewElement,
        -> -1
        is AggregateInstruction.ArrayNewFixed -> {
            1 - instruction.size.toInt()
        }
        is AggregateInstruction.ArraySet -> -3
        is AggregateInstruction.StructNew -> {
            val definedType = context.module.definedTypes[instruction.typeIndex.idx]
            val structType = definedType.asSubType.compositeType.asStructType()
            1 - structType.fields.size
        }
        is AggregateInstruction.StructNewDefault -> 1
        is AggregateInstruction.StructSet -> -2
        is AggregateInstruction -> 0
        is AtomicMemoryInstruction -> 0
        is ControlInstruction.Call -> {
            val definedType = context.functionTypes[instruction.functionIndex.idx]
            val functionType = definedType.asFunctionType()
            functionType.results.types.size - functionType.params.types.size
        }
        is ControlInstruction.CallIndirect -> {
            val definedType = context.module.definedTypes[instruction.typeIndex.idx]
            val functionType = definedType.asFunctionType()
            functionType.results.types.size - 1 - functionType.params.types.size
        }
        is ControlInstruction.CallRef -> {
            val definedType = context.module.definedTypes[instruction.typeIndex.idx]
            val functionType = definedType.asFunctionType()
            functionType.results.types.size - 1 - functionType.params.types.size
        }
        is ControlInstruction -> 0
        is MemoryInstruction.MemorySize -> 1
        is MemoryInstruction.Store -> -2
        is MemoryInstruction.MemoryInit -> -3
        is MemoryInstruction.MemoryCopy -> -3
        is MemoryInstruction.MemoryFill -> -3
        is MemoryInstruction -> 0
        is NumericInstruction.I32Const,
        is NumericInstruction.I64Const,
        is NumericInstruction.F32Const,
        is NumericInstruction.F64Const,
        -> 1
        is NumericInstruction.I32Add,
        is NumericInstruction.I64Add,
        is NumericInstruction.F32Add,
        is NumericInstruction.F64Add,
        is NumericInstruction.I32Sub,
        is NumericInstruction.I64Sub,
        is NumericInstruction.F32Sub,
        is NumericInstruction.F64Sub,
        is NumericInstruction.I32Mul,
        is NumericInstruction.I64Mul,
        is NumericInstruction.F32Mul,
        is NumericInstruction.F64Mul,
        is NumericInstruction.I32DivS,
        is NumericInstruction.I32DivU,
        is NumericInstruction.I64DivS,
        is NumericInstruction.I64DivU,
        is NumericInstruction.F32Div,
        is NumericInstruction.F64Div,
        is NumericInstruction.I32RemS,
        is NumericInstruction.I32RemU,
        is NumericInstruction.I64RemS,
        is NumericInstruction.I64RemU,
        is NumericInstruction.I32And,
        is NumericInstruction.I64And,
        is NumericInstruction.I32Or,
        is NumericInstruction.I64Or,
        is NumericInstruction.I32Xor,
        is NumericInstruction.I64Xor,
        is NumericInstruction.I32Shl,
        is NumericInstruction.I64Shl,
        is NumericInstruction.I32ShrS,
        is NumericInstruction.I32ShrU,
        is NumericInstruction.I64ShrS,
        is NumericInstruction.I64ShrU,
        is NumericInstruction.I32Rotl,
        is NumericInstruction.I32Rotr,
        is NumericInstruction.I64Rotl,
        is NumericInstruction.I64Rotr,
        is NumericInstruction.F32Min,
        is NumericInstruction.F32Max,
        is NumericInstruction.F64Min,
        is NumericInstruction.F64Max,
        is NumericInstruction.F32Copysign,
        is NumericInstruction.F64Copysign,
        is NumericInstruction.I32Eq,
        is NumericInstruction.I32Ne,
        is NumericInstruction.I32LtS,
        is NumericInstruction.I32LtU,
        is NumericInstruction.I32GtS,
        is NumericInstruction.I32GtU,
        is NumericInstruction.I32LeS,
        is NumericInstruction.I32LeU,
        is NumericInstruction.I32GeS,
        is NumericInstruction.I32GeU,
        is NumericInstruction.I64Eq,
        is NumericInstruction.I64Ne,
        is NumericInstruction.I64LtS,
        is NumericInstruction.I64LtU,
        is NumericInstruction.I64GtS,
        is NumericInstruction.I64GtU,
        is NumericInstruction.I64LeS,
        is NumericInstruction.I64LeU,
        is NumericInstruction.I64GeS,
        is NumericInstruction.I64GeU,
        is NumericInstruction.F32Eq,
        is NumericInstruction.F32Ne,
        is NumericInstruction.F32Lt,
        is NumericInstruction.F32Gt,
        is NumericInstruction.F32Le,
        is NumericInstruction.F32Ge,
        is NumericInstruction.F64Eq,
        is NumericInstruction.F64Ne,
        is NumericInstruction.F64Lt,
        is NumericInstruction.F64Gt,
        is NumericInstruction.F64Le,
        is NumericInstruction.F64Ge,
        -> -1
        is NumericInstruction -> 0
        is ParametricInstruction.Drop -> -1
        is ParametricInstruction.Select,
        is ParametricInstruction.SelectWithType,
        -> -2
        is ReferenceInstruction.RefNull,
        is ReferenceInstruction.RefFunc,
        -> 1
        is ReferenceInstruction.RefEq -> -1
        is ReferenceInstruction -> 0
        is TableInstruction.TableSize -> 1
        is TableInstruction.TableSet -> -2
        is TableInstruction.TableInit,
        is TableInstruction.TableCopy,
        is TableInstruction.TableFill,
        -> -3
        is TableInstruction.TableGrow -> -1
        is TableInstruction -> 0
        is VariableInstruction.LocalGet,
        is VariableInstruction.GlobalGet,
        -> 1
        is VariableInstruction.LocalSet,
        is VariableInstruction.GlobalSet,
        -> -1
        is VariableInstruction.LocalTee -> 0
        is VectorInstruction -> 0
        is FusedAggregateInstruction -> 0
        is FusedControlInstruction -> 0
        is FusedMemoryInstruction -> 0
        is FusedNumericInstruction -> 0
        is FusedParametricInstruction -> 0
        is FusedReferenceInstruction -> 0
        is FusedTableInstruction -> 0
        is FusedVariableInstruction -> 0
    }
}
