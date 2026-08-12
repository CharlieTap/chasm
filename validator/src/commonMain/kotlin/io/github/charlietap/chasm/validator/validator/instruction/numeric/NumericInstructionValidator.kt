package io.github.charlietap.chasm.validator.validator.instruction.numeric

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.NumericOpcode
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popF32OrThrow
import io.github.charlietap.chasm.validator.ext.popF64OrThrow
import io.github.charlietap.chasm.validator.ext.popI32OrThrow
import io.github.charlietap.chasm.validator.ext.popI64OrThrow
import io.github.charlietap.chasm.validator.ext.pushF32
import io.github.charlietap.chasm.validator.ext.pushF64
import io.github.charlietap.chasm.validator.ext.pushI32
import io.github.charlietap.chasm.validator.ext.pushI64

internal fun NumericInstructionValidator(
    context: ModuleValidationContext,
    instruction: NumericInstruction,
): Result<Unit, ModuleValidatorError> {
    when ((instruction as? NumericInstruction.Operator)?.opcode) {
        NumericOpcode.F32Abs, NumericOpcode.F32Neg,
        NumericOpcode.F32Floor, NumericOpcode.F32Ceil,
        NumericOpcode.F32Nearest, NumericOpcode.F32Sqrt,
        NumericOpcode.F32Trunc,
        -> {
            context.popF32OrThrow()
            context.pushF32()
        }
        NumericOpcode.F32Add, NumericOpcode.F32Sub,
        NumericOpcode.F32Mul, NumericOpcode.F32Div,
        NumericOpcode.F32Min, NumericOpcode.F32Max,
        NumericOpcode.F32Copysign,
        -> {
            context.popF32OrThrow()
            context.popF32OrThrow()
            context.pushF32()
        }
        NumericOpcode.F32Eq, NumericOpcode.F32Ne,
        NumericOpcode.F32Lt, NumericOpcode.F32Le,
        NumericOpcode.F32Gt, NumericOpcode.F32Ge,
        -> {
            context.popF32OrThrow()
            context.popF32OrThrow()
            context.pushI32()
        }
        NumericOpcode.F32ConvertI32S, NumericOpcode.F32ConvertI32U -> {
            context.popI32OrThrow()
            context.pushF32()
        }
        NumericOpcode.F32ConvertI64S, NumericOpcode.F32ConvertI64U -> {
            context.popI64OrThrow()
            context.pushF32()
        }
        NumericOpcode.F32DemoteF64 -> {
            context.popF64OrThrow()
            context.pushF32()
        }
        NumericOpcode.F32ReinterpretI32 -> {
            context.popI32OrThrow()
            context.pushF32()
        }
        NumericOpcode.F64Abs, NumericOpcode.F64Neg,
        NumericOpcode.F64Floor, NumericOpcode.F64Ceil,
        NumericOpcode.F64Nearest, NumericOpcode.F64Sqrt,
        NumericOpcode.F64Trunc,
        -> {
            context.popF64OrThrow()
            context.pushF64()
        }
        NumericOpcode.F64Add, NumericOpcode.F64Sub,
        NumericOpcode.F64Mul, NumericOpcode.F64Div,
        NumericOpcode.F64Min, NumericOpcode.F64Max,
        NumericOpcode.F64Copysign,
        -> {
            context.popF64OrThrow()
            context.popF64OrThrow()
            context.pushF64()
        }
        NumericOpcode.F64Eq, NumericOpcode.F64Ne,
        NumericOpcode.F64Lt, NumericOpcode.F64Le,
        NumericOpcode.F64Gt, NumericOpcode.F64Ge,
        -> {
            context.popF64OrThrow()
            context.popF64OrThrow()
            context.pushI32()
        }
        NumericOpcode.F64ConvertI32S, NumericOpcode.F64ConvertI32U -> {
            context.popI32OrThrow()
            context.pushF64()
        }
        NumericOpcode.F64ConvertI64S, NumericOpcode.F64ConvertI64U -> {
            context.popI64OrThrow()
            context.pushF64()
        }
        NumericOpcode.F64PromoteF32 -> {
            context.popF32OrThrow()
            context.pushF64()
        }
        NumericOpcode.F64ReinterpretI64 -> {
            context.popI64OrThrow()
            context.pushF64()
        }
        NumericOpcode.I32Add, NumericOpcode.I32Sub,
        NumericOpcode.I32Mul, NumericOpcode.I32DivS,
        NumericOpcode.I32DivU, NumericOpcode.I32RemS,
        NumericOpcode.I32RemU, NumericOpcode.I32And,
        NumericOpcode.I32Or, NumericOpcode.I32Xor,
        -> {
            context.popI32OrThrow()
            context.popI32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32Clz, NumericOpcode.I32Ctz,
        NumericOpcode.I32Popcnt,
        -> {
            context.popI32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32Eq, NumericOpcode.I32Ne,
        NumericOpcode.I32LtS, NumericOpcode.I32LtU,
        NumericOpcode.I32LeS, NumericOpcode.I32LeU,
        NumericOpcode.I32GtS, NumericOpcode.I32GtU,
        NumericOpcode.I32GeS, NumericOpcode.I32GeU,
        -> {
            context.popI32OrThrow()
            context.popI32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32Eqz -> {
            context.popI32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32Extend8S, NumericOpcode.I32Extend16S -> {
            context.popI32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32ReinterpretF32 -> {
            context.popF32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32Rotl, NumericOpcode.I32Rotr,
        NumericOpcode.I32Shl, NumericOpcode.I32ShrS,
        NumericOpcode.I32ShrU,
        -> {
            context.popI32OrThrow()
            context.popI32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32TruncF32S, NumericOpcode.I32TruncF32U -> {
            context.popF32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32TruncF64S, NumericOpcode.I32TruncF64U -> {
            context.popF64OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32TruncSatF32S, NumericOpcode.I32TruncSatF32U -> {
            context.popF32OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32TruncSatF64S, NumericOpcode.I32TruncSatF64U -> {
            context.popF64OrThrow()
            context.pushI32()
        }
        NumericOpcode.I32WrapI64 -> {
            context.popI64OrThrow()
            context.pushI32()
        }
        NumericOpcode.I64Add, NumericOpcode.I64Sub,
        NumericOpcode.I64Mul, NumericOpcode.I64DivS,
        NumericOpcode.I64DivU, NumericOpcode.I64RemS,
        NumericOpcode.I64RemU, NumericOpcode.I64And,
        NumericOpcode.I64Or, NumericOpcode.I64Xor,
        -> {
            context.popI64OrThrow()
            context.popI64OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64Clz, NumericOpcode.I64Ctz,
        NumericOpcode.I64Popcnt,
        -> {
            context.popI64OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64Eq, NumericOpcode.I64Ne,
        NumericOpcode.I64LtS, NumericOpcode.I64LtU,
        NumericOpcode.I64LeS, NumericOpcode.I64LeU,
        NumericOpcode.I64GtS, NumericOpcode.I64GtU,
        NumericOpcode.I64GeS, NumericOpcode.I64GeU,
        -> {
            context.popI64OrThrow()
            context.popI64OrThrow()
            context.pushI32()
        }
        NumericOpcode.I64Eqz -> {
            context.popI64OrThrow()
            context.pushI32()
        }
        NumericOpcode.I64Extend8S,
        NumericOpcode.I64Extend16S,
        NumericOpcode.I64Extend32S,
        -> {
            context.popI64OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64ExtendI32S,
        NumericOpcode.I64ExtendI32U,
        -> {
            context.popI32OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64ReinterpretF64 -> {
            context.popF64OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64Rotl, NumericOpcode.I64Rotr,
        NumericOpcode.I64Shl, NumericOpcode.I64ShrS,
        NumericOpcode.I64ShrU,
        -> {
            context.popI64OrThrow()
            context.popI64OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64TruncF32S, NumericOpcode.I64TruncF32U -> {
            context.popF32OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64TruncF64S, NumericOpcode.I64TruncF64U -> {
            context.popF64OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64TruncSatF32S, NumericOpcode.I64TruncSatF32U -> {
            context.popF32OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64TruncSatF64S, NumericOpcode.I64TruncSatF64U -> {
            context.popF64OrThrow()
            context.pushI64()
        }
        NumericOpcode.I64Add128, NumericOpcode.I64Sub128 -> {
            context.popI64OrThrow()
            context.popI64OrThrow()
            context.popI64OrThrow()
            context.popI64OrThrow()
            context.pushI64()
            context.pushI64()
        }
        NumericOpcode.I64MulWideS, NumericOpcode.I64MulWideU -> {
            context.popI64OrThrow()
            context.popI64OrThrow()
            context.pushI64()
            context.pushI64()
        }
        null -> when (instruction) {
            is NumericInstruction.F32Const -> context.pushF32()
            is NumericInstruction.F64Const -> context.pushF64()
            is NumericInstruction.I32Const -> context.pushI32()
            is NumericInstruction.I64Const -> context.pushI64()
            is NumericInstruction.Operator -> error("Unexpected numeric operator")
        }
    }
    return Ok(Unit)
}
