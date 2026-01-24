package io.github.charlietap.chasm.validator.validator.instruction.vector

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popF32
import io.github.charlietap.chasm.validator.ext.popF64
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popI64
import io.github.charlietap.chasm.validator.ext.popMemoryAddress
import io.github.charlietap.chasm.validator.ext.popV128
import io.github.charlietap.chasm.validator.ext.pushF32
import io.github.charlietap.chasm.validator.ext.pushF64
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.pushI64
import io.github.charlietap.chasm.validator.ext.pushV128
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator
import io.github.charlietap.chasm.validator.validator.instruction.MemArgValidator

internal fun VectorInstructionValidator(
    context: ValidationContext,
    instruction: VectorInstruction,
): Result<Unit, ModuleValidatorError> =
    VectorInstructionValidator(
        context = context,
        instruction = instruction,
        memArgValidator = ::MemArgValidator,
        memoryIndexValidator = ::MemoryIndexValidator,
        laneIndexValidator = ::LaneIndexValidator,
    )

internal inline fun VectorInstructionValidator(
    context: ValidationContext,
    instruction: VectorInstruction,
    crossinline memArgValidator: Validator<MemArg>,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
    crossinline laneIndexValidator: LaneIndexValidator,
): Result<Unit, ModuleValidatorError> = binding {
    when (instruction) {
        is VectorInstruction.V128Load -> {
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Load8x8S,
        is VectorInstruction.V128Load8x8U,
        is VectorInstruction.V128Load16x4S,
        is VectorInstruction.V128Load16x4U,
        is VectorInstruction.V128Load32x2S,
        is VectorInstruction.V128Load32x2U,
        -> {
            val (memoryIndex, memArg) = when (instruction) {
                is VectorInstruction.V128Load8x8S -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load8x8U -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load16x4S -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load16x4U -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load32x2S -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load32x2U -> instruction.memoryIndex to instruction.memArg
            }
            memArgValidator(context, memArg).bind()
            memoryIndexValidator(context, memoryIndex).bind()
            context.popMemoryAddress(memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Load8Splat,
        is VectorInstruction.V128Load16Splat,
        is VectorInstruction.V128Load32Splat,
        is VectorInstruction.V128Load64Splat,
        -> {
            val (memoryIndex, memArg) = when (instruction) {
                is VectorInstruction.V128Load8Splat -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load16Splat -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load32Splat -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load64Splat -> instruction.memoryIndex to instruction.memArg
            }
            memArgValidator(context, memArg).bind()
            memoryIndexValidator(context, memoryIndex).bind()
            context.popMemoryAddress(memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Load32Zero,
        is VectorInstruction.V128Load64Zero,
        -> {
            val (memoryIndex, memArg) = when (instruction) {
                is VectorInstruction.V128Load32Zero -> instruction.memoryIndex to instruction.memArg
                is VectorInstruction.V128Load64Zero -> instruction.memoryIndex to instruction.memArg
            }
            memArgValidator(context, memArg).bind()
            memoryIndexValidator(context, memoryIndex).bind()
            context.popMemoryAddress(memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Store -> {
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
        }
        is VectorInstruction.V128Load8Lane -> {
            laneIndexValidator(instruction.laneIdx, 16).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Load16Lane -> {
            laneIndexValidator(instruction.laneIdx, 8).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Load32Lane -> {
            laneIndexValidator(instruction.laneIdx, 4).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Load64Lane -> {
            laneIndexValidator(instruction.laneIdx, 2).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
            context.pushV128()
        }
        is VectorInstruction.V128Store8Lane -> {
            laneIndexValidator(instruction.laneIdx, 16).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
        }
        is VectorInstruction.V128Store16Lane -> {
            laneIndexValidator(instruction.laneIdx, 8).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
        }
        is VectorInstruction.V128Store32Lane -> {
            laneIndexValidator(instruction.laneIdx, 4).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
        }
        is VectorInstruction.V128Store64Lane -> {
            laneIndexValidator(instruction.laneIdx, 2).bind()
            memArgValidator(context, instruction.memArg).bind()
            memoryIndexValidator(context, instruction.memoryIndex).bind()
            context.popV128().bind()
            context.popMemoryAddress(instruction.memoryIndex).bind()
        }
        is VectorInstruction.V128Const -> {
            context.pushV128()
        }
        is VectorInstruction.I8x16Shuffle -> {
            instruction.laneIndices.forEach { laneIdx ->
                laneIndexValidator(laneIdx, 32).bind()
            }
            context.popV128().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.I8x16Swizzle,
        VectorInstruction.I8x16RelaxedSwizzle,
        -> {
            context.popV128().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.I8x16Splat,
        VectorInstruction.I16x8Splat,
        VectorInstruction.I32x4Splat,
        -> {
            context.popI32().bind()
            context.pushV128()
        }
        VectorInstruction.I64x2Splat -> {
            context.popI64().bind()
            context.pushV128()
        }
        VectorInstruction.F32x4Splat -> {
            context.popF32().bind()
            context.pushV128()
        }
        VectorInstruction.F64x2Splat -> {
            context.popF64().bind()
            context.pushV128()
        }
        is VectorInstruction.I8x16ExtractLaneS -> {
            laneIndexValidator(instruction.laneIdx, 16).bind()
            context.popV128().bind()
            context.pushI32()
        }
        is VectorInstruction.I8x16ExtractLaneU -> {
            laneIndexValidator(instruction.laneIdx, 16).bind()
            context.popV128().bind()
            context.pushI32()
        }
        is VectorInstruction.I16x8ExtractLaneS -> {
            laneIndexValidator(instruction.laneIdx, 8).bind()
            context.popV128().bind()
            context.pushI32()
        }
        is VectorInstruction.I16x8ExtractLaneU -> {
            laneIndexValidator(instruction.laneIdx, 8).bind()
            context.popV128().bind()
            context.pushI32()
        }
        is VectorInstruction.I32x4ExtractLane -> {
            laneIndexValidator(instruction.laneIdx, 4).bind()
            context.popV128().bind()
            context.pushI32()
        }
        is VectorInstruction.I64x2ExtractLane -> {
            laneIndexValidator(instruction.laneIdx, 2).bind()
            context.popV128().bind()
            context.pushI64()
        }
        is VectorInstruction.F32x4ExtractLane -> {
            laneIndexValidator(instruction.laneIdx, 4).bind()
            context.popV128().bind()
            context.pushF32()
        }
        is VectorInstruction.F64x2ExtractLane -> {
            laneIndexValidator(instruction.laneIdx, 2).bind()
            context.popV128().bind()
            context.pushF64()
        }
        is VectorInstruction.I8x16ReplaceLane -> {
            laneIndexValidator(instruction.laneIdx, 16).bind()
            context.popI32().bind()
            context.popV128().bind()
            context.pushV128()
        }
        is VectorInstruction.I16x8ReplaceLane -> {
            laneIndexValidator(instruction.laneIdx, 8).bind()
            context.popI32().bind()
            context.popV128().bind()
            context.pushV128()
        }
        is VectorInstruction.I32x4ReplaceLane -> {
            laneIndexValidator(instruction.laneIdx, 4).bind()
            context.popI32().bind()
            context.popV128().bind()
            context.pushV128()
        }
        is VectorInstruction.I64x2ReplaceLane -> {
            laneIndexValidator(instruction.laneIdx, 2).bind()
            context.popI64().bind()
            context.popV128().bind()
            context.pushV128()
        }
        is VectorInstruction.F32x4ReplaceLane -> {
            laneIndexValidator(instruction.laneIdx, 4).bind()
            context.popF32().bind()
            context.popV128().bind()
            context.pushV128()
        }
        is VectorInstruction.F64x2ReplaceLane -> {
            laneIndexValidator(instruction.laneIdx, 2).bind()
            context.popF64().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.I8x16Eq, VectorInstruction.I8x16Ne,
        VectorInstruction.I8x16LtS, VectorInstruction.I8x16LtU,
        VectorInstruction.I8x16GtS, VectorInstruction.I8x16GtU,
        VectorInstruction.I8x16LeS, VectorInstruction.I8x16LeU,
        VectorInstruction.I8x16GeS, VectorInstruction.I8x16GeU,
        VectorInstruction.I16x8Eq, VectorInstruction.I16x8Ne,
        VectorInstruction.I16x8LtS, VectorInstruction.I16x8LtU,
        VectorInstruction.I16x8GtS, VectorInstruction.I16x8GtU,
        VectorInstruction.I16x8LeS, VectorInstruction.I16x8LeU,
        VectorInstruction.I16x8GeS, VectorInstruction.I16x8GeU,
        VectorInstruction.I32x4Eq, VectorInstruction.I32x4Ne,
        VectorInstruction.I32x4LtS, VectorInstruction.I32x4LtU,
        VectorInstruction.I32x4GtS, VectorInstruction.I32x4GtU,
        VectorInstruction.I32x4LeS, VectorInstruction.I32x4LeU,
        VectorInstruction.I32x4GeS, VectorInstruction.I32x4GeU,
        VectorInstruction.I64x2Eq, VectorInstruction.I64x2Ne,
        VectorInstruction.I64x2LtS, VectorInstruction.I64x2GtS,
        VectorInstruction.I64x2LeS, VectorInstruction.I64x2GeS,
        VectorInstruction.F32x4Eq, VectorInstruction.F32x4Ne,
        VectorInstruction.F32x4Lt, VectorInstruction.F32x4Gt,
        VectorInstruction.F32x4Le, VectorInstruction.F32x4Ge,
        VectorInstruction.F64x2Eq, VectorInstruction.F64x2Ne,
        VectorInstruction.F64x2Lt, VectorInstruction.F64x2Gt,
        VectorInstruction.F64x2Le, VectorInstruction.F64x2Ge,
        -> {
            context.popV128().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.V128Not -> {
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.V128And, VectorInstruction.V128AndNot,
        VectorInstruction.V128Or, VectorInstruction.V128Xor,
        -> {
            context.popV128().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.V128Bitselect -> {
            context.popV128().bind()
            context.popV128().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.V128AnyTrue,
        VectorInstruction.I8x16AllTrue, VectorInstruction.I8x16Bitmask,
        VectorInstruction.I16x8AllTrue, VectorInstruction.I16x8Bitmask,
        VectorInstruction.I32x4AllTrue, VectorInstruction.I32x4Bitmask,
        VectorInstruction.I64x2AllTrue, VectorInstruction.I64x2Bitmask,
        -> {
            context.popV128().bind()
            context.pushI32()
        }
        VectorInstruction.I8x16Abs, VectorInstruction.I8x16Neg, VectorInstruction.I8x16Popcnt,
        VectorInstruction.I16x8Abs, VectorInstruction.I16x8Neg,
        VectorInstruction.I32x4Abs, VectorInstruction.I32x4Neg,
        VectorInstruction.I64x2Abs, VectorInstruction.I64x2Neg,
        VectorInstruction.F32x4Abs, VectorInstruction.F32x4Neg, VectorInstruction.F32x4Sqrt,
        VectorInstruction.F32x4Ceil, VectorInstruction.F32x4Floor,
        VectorInstruction.F32x4Trunc, VectorInstruction.F32x4Nearest,
        VectorInstruction.F64x2Abs, VectorInstruction.F64x2Neg, VectorInstruction.F64x2Sqrt,
        VectorInstruction.F64x2Ceil, VectorInstruction.F64x2Floor,
        VectorInstruction.F64x2Trunc, VectorInstruction.F64x2Nearest,
        VectorInstruction.I16x8ExtendLowI8x16S, VectorInstruction.I16x8ExtendHighI8x16S,
        VectorInstruction.I16x8ExtendLowI8x16U, VectorInstruction.I16x8ExtendHighI8x16U,
        VectorInstruction.I32x4ExtendLowI16x8S, VectorInstruction.I32x4ExtendHighI16x8S,
        VectorInstruction.I32x4ExtendLowI16x8U, VectorInstruction.I32x4ExtendHighI16x8U,
        VectorInstruction.I64x2ExtendLowI32x4S, VectorInstruction.I64x2ExtendHighI32x4S,
        VectorInstruction.I64x2ExtendLowI32x4U, VectorInstruction.I64x2ExtendHighI32x4U,
        VectorInstruction.I16x8ExtaddPairwiseI8x16S, VectorInstruction.I16x8ExtaddPairwiseI8x16U,
        VectorInstruction.I32x4ExtaddPairwiseI16x8S, VectorInstruction.I32x4ExtaddPairwiseI16x8U,
        VectorInstruction.I32x4TruncSatF32x4S, VectorInstruction.I32x4TruncSatF32x4U,
        VectorInstruction.F32x4ConvertI32x4S, VectorInstruction.F32x4ConvertI32x4U,
        VectorInstruction.I32x4TruncSatF64x2SZero, VectorInstruction.I32x4TruncSatF64x2UZero,
        VectorInstruction.F64x2ConvertLowI32x4S, VectorInstruction.F64x2ConvertLowI32x4U,
        VectorInstruction.F32x4DemoteF64x2Zero, VectorInstruction.F64x2PromoteLowF32x4,
        VectorInstruction.I32x4RelaxedTruncF32x4S, VectorInstruction.I32x4RelaxedTruncF32x4U,
        VectorInstruction.I32x4RelaxedTruncF64x2SZero, VectorInstruction.I32x4RelaxedTruncF64x2UZero,
        -> {
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.I8x16NarrowI16x8S, VectorInstruction.I8x16NarrowI16x8U,
        VectorInstruction.I16x8NarrowI32x4S, VectorInstruction.I16x8NarrowI32x4U,
        VectorInstruction.I8x16Add, VectorInstruction.I8x16AddSatS, VectorInstruction.I8x16AddSatU,
        VectorInstruction.I8x16Sub, VectorInstruction.I8x16SubSatS, VectorInstruction.I8x16SubSatU,
        VectorInstruction.I8x16MinS, VectorInstruction.I8x16MinU,
        VectorInstruction.I8x16MaxS, VectorInstruction.I8x16MaxU,
        VectorInstruction.I8x16AvgrU,
        VectorInstruction.I16x8Add, VectorInstruction.I16x8AddSatS, VectorInstruction.I16x8AddSatU,
        VectorInstruction.I16x8Sub, VectorInstruction.I16x8SubSatS, VectorInstruction.I16x8SubSatU,
        VectorInstruction.I16x8Mul,
        VectorInstruction.I16x8MinS, VectorInstruction.I16x8MinU,
        VectorInstruction.I16x8MaxS, VectorInstruction.I16x8MaxU,
        VectorInstruction.I16x8AvgrU, VectorInstruction.I16x8Q15mulrSatS,
        VectorInstruction.I16x8ExtmulLowI8x16S, VectorInstruction.I16x8ExtmulHighI8x16S,
        VectorInstruction.I16x8ExtmulLowI8x16U, VectorInstruction.I16x8ExtmulHighI8x16U,
        VectorInstruction.I32x4Add, VectorInstruction.I32x4Sub, VectorInstruction.I32x4Mul,
        VectorInstruction.I32x4MinS, VectorInstruction.I32x4MinU,
        VectorInstruction.I32x4MaxS, VectorInstruction.I32x4MaxU,
        VectorInstruction.I32x4DotI16x8S,
        VectorInstruction.I32x4ExtmulLowI16x8S, VectorInstruction.I32x4ExtmulHighI16x8S,
        VectorInstruction.I32x4ExtmulLowI16x8U, VectorInstruction.I32x4ExtmulHighI16x8U,
        VectorInstruction.I64x2Add, VectorInstruction.I64x2Sub, VectorInstruction.I64x2Mul,
        VectorInstruction.I64x2ExtmulLowI32x4S, VectorInstruction.I64x2ExtmulHighI32x4S,
        VectorInstruction.I64x2ExtmulLowI32x4U, VectorInstruction.I64x2ExtmulHighI32x4U,
        VectorInstruction.F32x4Add, VectorInstruction.F32x4Sub,
        VectorInstruction.F32x4Mul, VectorInstruction.F32x4Div,
        VectorInstruction.F32x4Min, VectorInstruction.F32x4Max,
        VectorInstruction.F32x4PMin, VectorInstruction.F32x4PMax,
        VectorInstruction.F64x2Add, VectorInstruction.F64x2Sub,
        VectorInstruction.F64x2Mul, VectorInstruction.F64x2Div,
        VectorInstruction.F64x2Min, VectorInstruction.F64x2Max,
        VectorInstruction.F64x2PMin, VectorInstruction.F64x2PMax,
        VectorInstruction.I16x8RelaxedQ15mulrS,
        VectorInstruction.I16x8RelaxedDotI8x16I7x16S,
        VectorInstruction.F32x4RelaxedMin, VectorInstruction.F32x4RelaxedMax,
        VectorInstruction.F64x2RelaxedMin, VectorInstruction.F64x2RelaxedMax,
        -> {
            context.popV128().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.I8x16Shl, VectorInstruction.I8x16ShrS, VectorInstruction.I8x16ShrU,
        VectorInstruction.I16x8Shl, VectorInstruction.I16x8ShrS, VectorInstruction.I16x8ShrU,
        VectorInstruction.I32x4Shl, VectorInstruction.I32x4ShrS, VectorInstruction.I32x4ShrU,
        VectorInstruction.I64x2Shl, VectorInstruction.I64x2ShrS, VectorInstruction.I64x2ShrU,
        -> {
            context.popI32().bind()
            context.popV128().bind()
            context.pushV128()
        }
        VectorInstruction.I8x16RelaxedLaneselect,
        VectorInstruction.I16x8RelaxedLaneselect,
        VectorInstruction.I32x4RelaxedLaneselect,
        VectorInstruction.I64x2RelaxedLaneselect,
        VectorInstruction.F32x4RelaxedMadd, VectorInstruction.F32x4RelaxedNmadd,
        VectorInstruction.F64x2RelaxedMadd, VectorInstruction.F64x2RelaxedNmadd,
        VectorInstruction.I32x4RelaxedDotI8x16I7x16AddS,
        -> {
            context.popV128().bind()
            context.popV128().bind()
            context.popV128().bind()
            context.pushV128()
        }
    }
}
