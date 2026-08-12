package io.github.charlietap.chasm.validator.validator.instruction.vector

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VectorOpcode
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popF32OrThrow
import io.github.charlietap.chasm.validator.ext.popF64OrThrow
import io.github.charlietap.chasm.validator.ext.popI32OrThrow
import io.github.charlietap.chasm.validator.ext.popI64OrThrow
import io.github.charlietap.chasm.validator.ext.popV128OrThrow
import io.github.charlietap.chasm.validator.ext.pushF32
import io.github.charlietap.chasm.validator.ext.pushF64
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.pushI64
import io.github.charlietap.chasm.validator.ext.pushV128

internal fun VectorOperatorValidator(
    context: ModuleValidationContext,
    opcode: VectorOpcode,
): Result<Unit, ModuleValidatorError> {
    when (opcode) {
        VectorOpcode.I8x16Swizzle,
        VectorOpcode.I8x16RelaxedSwizzle,
        -> {
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.I8x16Splat,
        VectorOpcode.I16x8Splat,
        VectorOpcode.I32x4Splat,
        -> {
            context.popI32OrThrow()
            context.pushV128()
        }
        VectorOpcode.I64x2Splat -> {
            context.popI64OrThrow()
            context.pushV128()
        }
        VectorOpcode.F32x4Splat -> {
            context.popF32OrThrow()
            context.pushV128()
        }
        VectorOpcode.F64x2Splat -> {
            context.popF64OrThrow()
            context.pushV128()
        }
        VectorOpcode.I8x16Eq, VectorOpcode.I8x16Ne,
        VectorOpcode.I8x16LtS, VectorOpcode.I8x16LtU,
        VectorOpcode.I8x16GtS, VectorOpcode.I8x16GtU,
        VectorOpcode.I8x16LeS, VectorOpcode.I8x16LeU,
        VectorOpcode.I8x16GeS, VectorOpcode.I8x16GeU,
        VectorOpcode.I16x8Eq, VectorOpcode.I16x8Ne,
        VectorOpcode.I16x8LtS, VectorOpcode.I16x8LtU,
        VectorOpcode.I16x8GtS, VectorOpcode.I16x8GtU,
        VectorOpcode.I16x8LeS, VectorOpcode.I16x8LeU,
        VectorOpcode.I16x8GeS, VectorOpcode.I16x8GeU,
        VectorOpcode.I32x4Eq, VectorOpcode.I32x4Ne,
        VectorOpcode.I32x4LtS, VectorOpcode.I32x4LtU,
        VectorOpcode.I32x4GtS, VectorOpcode.I32x4GtU,
        VectorOpcode.I32x4LeS, VectorOpcode.I32x4LeU,
        VectorOpcode.I32x4GeS, VectorOpcode.I32x4GeU,
        VectorOpcode.I64x2Eq, VectorOpcode.I64x2Ne,
        VectorOpcode.I64x2LtS, VectorOpcode.I64x2GtS,
        VectorOpcode.I64x2LeS, VectorOpcode.I64x2GeS,
        VectorOpcode.F32x4Eq, VectorOpcode.F32x4Ne,
        VectorOpcode.F32x4Lt, VectorOpcode.F32x4Gt,
        VectorOpcode.F32x4Le, VectorOpcode.F32x4Ge,
        VectorOpcode.F64x2Eq, VectorOpcode.F64x2Ne,
        VectorOpcode.F64x2Lt, VectorOpcode.F64x2Gt,
        VectorOpcode.F64x2Le, VectorOpcode.F64x2Ge,
        -> {
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.V128Not -> {
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.V128And, VectorOpcode.V128AndNot,
        VectorOpcode.V128Or, VectorOpcode.V128Xor,
        -> {
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.V128Bitselect -> {
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.V128AnyTrue,
        VectorOpcode.I8x16AllTrue, VectorOpcode.I8x16Bitmask,
        VectorOpcode.I16x8AllTrue, VectorOpcode.I16x8Bitmask,
        VectorOpcode.I32x4AllTrue, VectorOpcode.I32x4Bitmask,
        VectorOpcode.I64x2AllTrue, VectorOpcode.I64x2Bitmask,
        -> {
            context.popV128OrThrow()
            context.pushI32()
        }
        VectorOpcode.I8x16Abs, VectorOpcode.I8x16Neg, VectorOpcode.I8x16Popcnt,
        VectorOpcode.I16x8Abs, VectorOpcode.I16x8Neg,
        VectorOpcode.I32x4Abs, VectorOpcode.I32x4Neg,
        VectorOpcode.I64x2Abs, VectorOpcode.I64x2Neg,
        VectorOpcode.F32x4Abs, VectorOpcode.F32x4Neg, VectorOpcode.F32x4Sqrt,
        VectorOpcode.F32x4Ceil, VectorOpcode.F32x4Floor,
        VectorOpcode.F32x4Trunc, VectorOpcode.F32x4Nearest,
        VectorOpcode.F64x2Abs, VectorOpcode.F64x2Neg, VectorOpcode.F64x2Sqrt,
        VectorOpcode.F64x2Ceil, VectorOpcode.F64x2Floor,
        VectorOpcode.F64x2Trunc, VectorOpcode.F64x2Nearest,
        VectorOpcode.I16x8ExtendLowI8x16S, VectorOpcode.I16x8ExtendHighI8x16S,
        VectorOpcode.I16x8ExtendLowI8x16U, VectorOpcode.I16x8ExtendHighI8x16U,
        VectorOpcode.I32x4ExtendLowI16x8S, VectorOpcode.I32x4ExtendHighI16x8S,
        VectorOpcode.I32x4ExtendLowI16x8U, VectorOpcode.I32x4ExtendHighI16x8U,
        VectorOpcode.I64x2ExtendLowI32x4S, VectorOpcode.I64x2ExtendHighI32x4S,
        VectorOpcode.I64x2ExtendLowI32x4U, VectorOpcode.I64x2ExtendHighI32x4U,
        VectorOpcode.I16x8ExtaddPairwiseI8x16S, VectorOpcode.I16x8ExtaddPairwiseI8x16U,
        VectorOpcode.I32x4ExtaddPairwiseI16x8S, VectorOpcode.I32x4ExtaddPairwiseI16x8U,
        VectorOpcode.I32x4TruncSatF32x4S, VectorOpcode.I32x4TruncSatF32x4U,
        VectorOpcode.F32x4ConvertI32x4S, VectorOpcode.F32x4ConvertI32x4U,
        VectorOpcode.I32x4TruncSatF64x2SZero, VectorOpcode.I32x4TruncSatF64x2UZero,
        VectorOpcode.F64x2ConvertLowI32x4S, VectorOpcode.F64x2ConvertLowI32x4U,
        VectorOpcode.F32x4DemoteF64x2Zero, VectorOpcode.F64x2PromoteLowF32x4,
        VectorOpcode.I32x4RelaxedTruncF32x4S, VectorOpcode.I32x4RelaxedTruncF32x4U,
        VectorOpcode.I32x4RelaxedTruncF64x2SZero, VectorOpcode.I32x4RelaxedTruncF64x2UZero,
        -> {
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.I8x16NarrowI16x8S, VectorOpcode.I8x16NarrowI16x8U,
        VectorOpcode.I16x8NarrowI32x4S, VectorOpcode.I16x8NarrowI32x4U,
        VectorOpcode.I8x16Add, VectorOpcode.I8x16AddSatS, VectorOpcode.I8x16AddSatU,
        VectorOpcode.I8x16Sub, VectorOpcode.I8x16SubSatS, VectorOpcode.I8x16SubSatU,
        VectorOpcode.I8x16MinS, VectorOpcode.I8x16MinU,
        VectorOpcode.I8x16MaxS, VectorOpcode.I8x16MaxU,
        VectorOpcode.I8x16AvgrU,
        VectorOpcode.I16x8Add, VectorOpcode.I16x8AddSatS, VectorOpcode.I16x8AddSatU,
        VectorOpcode.I16x8Sub, VectorOpcode.I16x8SubSatS, VectorOpcode.I16x8SubSatU,
        VectorOpcode.I16x8Mul,
        VectorOpcode.I16x8MinS, VectorOpcode.I16x8MinU,
        VectorOpcode.I16x8MaxS, VectorOpcode.I16x8MaxU,
        VectorOpcode.I16x8AvgrU, VectorOpcode.I16x8Q15mulrSatS,
        VectorOpcode.I16x8ExtmulLowI8x16S, VectorOpcode.I16x8ExtmulHighI8x16S,
        VectorOpcode.I16x8ExtmulLowI8x16U, VectorOpcode.I16x8ExtmulHighI8x16U,
        VectorOpcode.I32x4Add, VectorOpcode.I32x4Sub, VectorOpcode.I32x4Mul,
        VectorOpcode.I32x4MinS, VectorOpcode.I32x4MinU,
        VectorOpcode.I32x4MaxS, VectorOpcode.I32x4MaxU,
        VectorOpcode.I32x4DotI16x8S,
        VectorOpcode.I32x4ExtmulLowI16x8S, VectorOpcode.I32x4ExtmulHighI16x8S,
        VectorOpcode.I32x4ExtmulLowI16x8U, VectorOpcode.I32x4ExtmulHighI16x8U,
        VectorOpcode.I64x2Add, VectorOpcode.I64x2Sub, VectorOpcode.I64x2Mul,
        VectorOpcode.I64x2ExtmulLowI32x4S, VectorOpcode.I64x2ExtmulHighI32x4S,
        VectorOpcode.I64x2ExtmulLowI32x4U, VectorOpcode.I64x2ExtmulHighI32x4U,
        VectorOpcode.F32x4Add, VectorOpcode.F32x4Sub,
        VectorOpcode.F32x4Mul, VectorOpcode.F32x4Div,
        VectorOpcode.F32x4Min, VectorOpcode.F32x4Max,
        VectorOpcode.F32x4PMin, VectorOpcode.F32x4PMax,
        VectorOpcode.F64x2Add, VectorOpcode.F64x2Sub,
        VectorOpcode.F64x2Mul, VectorOpcode.F64x2Div,
        VectorOpcode.F64x2Min, VectorOpcode.F64x2Max,
        VectorOpcode.F64x2PMin, VectorOpcode.F64x2PMax,
        VectorOpcode.I16x8RelaxedQ15mulrS,
        VectorOpcode.I16x8RelaxedDotI8x16I7x16S,
        VectorOpcode.F32x4RelaxedMin, VectorOpcode.F32x4RelaxedMax,
        VectorOpcode.F64x2RelaxedMin, VectorOpcode.F64x2RelaxedMax,
        -> {
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.I8x16Shl, VectorOpcode.I8x16ShrS, VectorOpcode.I8x16ShrU,
        VectorOpcode.I16x8Shl, VectorOpcode.I16x8ShrS, VectorOpcode.I16x8ShrU,
        VectorOpcode.I32x4Shl, VectorOpcode.I32x4ShrS, VectorOpcode.I32x4ShrU,
        VectorOpcode.I64x2Shl, VectorOpcode.I64x2ShrS, VectorOpcode.I64x2ShrU,
        -> {
            context.popI32OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
        VectorOpcode.I8x16RelaxedLaneselect,
        VectorOpcode.I16x8RelaxedLaneselect,
        VectorOpcode.I32x4RelaxedLaneselect,
        VectorOpcode.I64x2RelaxedLaneselect,
        VectorOpcode.F32x4RelaxedMadd, VectorOpcode.F32x4RelaxedNmadd,
        VectorOpcode.F64x2RelaxedMadd, VectorOpcode.F64x2RelaxedNmadd,
        VectorOpcode.I32x4RelaxedDotI8x16I7x16AddS,
        -> {
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.popV128OrThrow()
            context.pushV128()
        }
    }
    return Ok(Unit)
}
