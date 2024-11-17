package io.github.charlietap.chasm.validator.validator.instruction.numeric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popF32
import io.github.charlietap.chasm.validator.ext.popF64
import io.github.charlietap.chasm.validator.ext.popI32
import io.github.charlietap.chasm.validator.ext.popI64
import io.github.charlietap.chasm.validator.ext.pushF32
import io.github.charlietap.chasm.validator.ext.pushF64
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.pushI64

internal fun NumericInstructionValidator(
    context: ValidationContext,
    instruction: NumericInstruction,
): Result<Unit, ModuleValidatorError> = binding {
    when (instruction) {
        NumericInstruction.F32Abs, NumericInstruction.F32Neg,
        NumericInstruction.F32Floor, NumericInstruction.F32Ceil,
        NumericInstruction.F32Nearest, NumericInstruction.F32Sqrt,
        NumericInstruction.F32Trunc,
        -> {
            context.popF32().bind()
            context.pushF32()
        }
        NumericInstruction.F32Add, NumericInstruction.F32Sub,
        NumericInstruction.F32Mul, NumericInstruction.F32Div,
        NumericInstruction.F32Min, NumericInstruction.F32Max,
        NumericInstruction.F32Copysign,
        -> {
            context.popF32().bind()
            context.popF32().bind()
            context.pushF32()
        }
        NumericInstruction.F32Eq, NumericInstruction.F32Ne,
        NumericInstruction.F32Lt, NumericInstruction.F32Le,
        NumericInstruction.F32Gt, NumericInstruction.F32Ge,
        -> {
            context.popF32().bind()
            context.popF32().bind()
            context.pushI32()
        }
        is NumericInstruction.F32Const -> {
            context.pushF32()
        }
        NumericInstruction.F32ConvertI32S, NumericInstruction.F32ConvertI32U -> {
            context.popI32().bind()
            context.pushF32()
        }
        NumericInstruction.F32ConvertI64S, NumericInstruction.F32ConvertI64U -> {
            context.popI64().bind()
            context.pushF32()
        }
        NumericInstruction.F32DemoteF64 -> {
            context.popF64().bind()
            context.pushF32()
        }
        NumericInstruction.F32ReinterpretI32 -> {
            context.popI32().bind()
            context.pushF32()
        }
        NumericInstruction.F64Abs, NumericInstruction.F64Neg,
        NumericInstruction.F64Floor, NumericInstruction.F64Ceil,
        NumericInstruction.F64Nearest, NumericInstruction.F64Sqrt,
        NumericInstruction.F64Trunc,
        -> {
            context.popF64().bind()
            context.pushF64()
        }
        NumericInstruction.F64Add, NumericInstruction.F64Sub,
        NumericInstruction.F64Mul, NumericInstruction.F64Div,
        NumericInstruction.F64Min, NumericInstruction.F64Max,
        NumericInstruction.F64Copysign,
        -> {
            context.popF64().bind()
            context.popF64().bind()
            context.pushF64()
        }
        NumericInstruction.F64Eq, NumericInstruction.F64Ne,
        NumericInstruction.F64Lt, NumericInstruction.F64Le,
        NumericInstruction.F64Gt, NumericInstruction.F64Ge,
        -> {
            context.popF64().bind()
            context.popF64().bind()
            context.pushI32()
        }
        is NumericInstruction.F64Const -> {
            context.pushF64()
        }
        NumericInstruction.F64ConvertI32S, NumericInstruction.F64ConvertI32U -> {
            context.popI32().bind()
            context.pushF64()
        }
        NumericInstruction.F64ConvertI64S, NumericInstruction.F64ConvertI64U -> {
            context.popI64().bind()
            context.pushF64()
        }
        NumericInstruction.F64PromoteF32 -> {
            context.popF32().bind()
            context.pushF64()
        }
        NumericInstruction.F64ReinterpretI64 -> {
            context.popI64().bind()
            context.pushF64()
        }
        NumericInstruction.I32Add, NumericInstruction.I32Sub,
        NumericInstruction.I32Mul, NumericInstruction.I32DivS,
        NumericInstruction.I32DivU, NumericInstruction.I32RemS,
        NumericInstruction.I32RemU, NumericInstruction.I32And,
        NumericInstruction.I32Or, NumericInstruction.I32Xor,
        -> {
            context.popI32().bind()
            context.popI32().bind()
            context.pushI32()
        }
        NumericInstruction.I32Clz, NumericInstruction.I32Ctz,
        NumericInstruction.I32Popcnt,
        -> {
            context.popI32().bind()
            context.pushI32()
        }
        NumericInstruction.I32Eq, NumericInstruction.I32Ne,
        NumericInstruction.I32LtS, NumericInstruction.I32LtU,
        NumericInstruction.I32LeS, NumericInstruction.I32LeU,
        NumericInstruction.I32GtS, NumericInstruction.I32GtU,
        NumericInstruction.I32GeS, NumericInstruction.I32GeU,
        -> {
            context.popI32().bind()
            context.popI32().bind()
            context.pushI32()
        }
        NumericInstruction.I32Eqz -> {
            context.popI32().bind()
            context.pushI32()
        }
        is NumericInstruction.I32Const -> {
            context.pushI32()
        }
        NumericInstruction.I32Extend8S, NumericInstruction.I32Extend16S -> {
            context.popI32().bind()
            context.pushI32()
        }
        NumericInstruction.I32ReinterpretF32 -> {
            context.popF32().bind()
            context.pushI32()
        }
        NumericInstruction.I32Rotl, NumericInstruction.I32Rotr,
        NumericInstruction.I32Shl, NumericInstruction.I32ShrS,
        NumericInstruction.I32ShrU,
        -> {
            context.popI32().bind()
            context.popI32().bind()
            context.pushI32()
        }
        NumericInstruction.I32TruncF32S, NumericInstruction.I32TruncF32U -> {
            context.popF32().bind()
            context.pushI32()
        }
        NumericInstruction.I32TruncF64S, NumericInstruction.I32TruncF64U -> {
            context.popF64().bind()
            context.pushI32()
        }
        NumericInstruction.I32TruncSatF32S, NumericInstruction.I32TruncSatF32U -> {
            context.popF32().bind()
            context.pushI32()
        }
        NumericInstruction.I32TruncSatF64S, NumericInstruction.I32TruncSatF64U -> {
            context.popF64().bind()
            context.pushI32()
        }
        NumericInstruction.I32WrapI64 -> {
            context.popI64().bind()
            context.pushI32()
        }
        NumericInstruction.I64Add, NumericInstruction.I64Sub,
        NumericInstruction.I64Mul, NumericInstruction.I64DivS,
        NumericInstruction.I64DivU, NumericInstruction.I64RemS,
        NumericInstruction.I64RemU, NumericInstruction.I64And,
        NumericInstruction.I64Or, NumericInstruction.I64Xor,
        -> {
            context.popI64().bind()
            context.popI64().bind()
            context.pushI64()
        }
        NumericInstruction.I64Clz, NumericInstruction.I64Ctz,
        NumericInstruction.I64Popcnt,
        -> {
            context.popI64().bind()
            context.pushI64()
        }
        NumericInstruction.I64Eq, NumericInstruction.I64Ne,
        NumericInstruction.I64LtS, NumericInstruction.I64LtU,
        NumericInstruction.I64LeS, NumericInstruction.I64LeU,
        NumericInstruction.I64GtS, NumericInstruction.I64GtU,
        NumericInstruction.I64GeS, NumericInstruction.I64GeU,
        -> {
            context.popI64().bind()
            context.popI64().bind()
            context.pushI32()
        }
        NumericInstruction.I64Eqz -> {
            context.popI64().bind()
            context.pushI32()
        }
        is NumericInstruction.I64Const -> {
            context.pushI64()
        }
        NumericInstruction.I64Extend8S,
        NumericInstruction.I64Extend16S,
        NumericInstruction.I64Extend32S,
        -> {
            context.popI64().bind()
            context.pushI64()
        }
        NumericInstruction.I64ExtendI32S,
        NumericInstruction.I64ExtendI32U,
        -> {
            context.popI32().bind()
            context.pushI64()
        }
        NumericInstruction.I64ReinterpretF64 -> {
            context.popF64().bind()
            context.pushI64()
        }
        NumericInstruction.I64Rotl, NumericInstruction.I64Rotr,
        NumericInstruction.I64Shl, NumericInstruction.I64ShrS,
        NumericInstruction.I64ShrU,
        -> {
            context.popI64().bind()
            context.popI64().bind()
            context.pushI64()
        }
        NumericInstruction.I64TruncF32S, NumericInstruction.I64TruncF32U -> {
            context.popF32().bind()
            context.pushI64()
        }
        NumericInstruction.I64TruncF64S, NumericInstruction.I64TruncF64U -> {
            context.popF64().bind()
            context.pushI64()
        }
        NumericInstruction.I64TruncSatF32S, NumericInstruction.I64TruncSatF32U -> {
            context.popF32().bind()
            context.pushI64()
        }
        NumericInstruction.I64TruncSatF64S, NumericInstruction.I64TruncSatF64U -> {
            context.popF64().bind()
            context.pushI64()
        }
    }
}
