package io.github.charlietap.chasm.validator.resolver

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.type.InstructionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun NumericInstructionTypeResolverImpl(
    context: ValidationContext,
    instruction: NumericInstruction,
): Result<InstructionType, ModuleValidatorError> = binding {
    when (instruction) {
        NumericInstruction.F32Abs -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Add -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Ceil -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        is NumericInstruction.F32Const -> InstructionType(
            ResultType(emptyList()),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32ConvertI32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32ConvertI32U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32ConvertI64S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32ConvertI64U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Copysign -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32DemoteF64 -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Div -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Eq -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F32Floor -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Ge -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F32Gt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F32Le -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F32Lt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F32Max -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Min -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Mul -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Ne -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F32Nearest -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Neg -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32ReinterpretI32 -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Sqrt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Sub -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32), ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F32Trunc -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F32))),
        )
        NumericInstruction.F64Abs -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Add -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Ceil -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        is NumericInstruction.F64Const -> InstructionType(
            ResultType(emptyList()),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64ConvertI32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64ConvertI32U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64ConvertI64S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64ConvertI64U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Copysign -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Div -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Eq -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F64Floor -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Ge -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F64Gt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F64Le -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F64Lt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F64Max -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Min -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Mul -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Ne -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.F64Nearest -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Neg -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64PromoteF32 -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64ReinterpretI64 -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Sqrt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Sub -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64), ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        NumericInstruction.F64Trunc -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.F64))),
        )
        is NumericInstruction.I32Const -> InstructionType(
            ResultType(emptyList()),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Add -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32And -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Clz -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Ctz -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32DivS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32DivU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Eq -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Eqz -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Extend16S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Extend8S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32GeS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32GeU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32GtS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32GtU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32LeS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32LeU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32LtS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32LtU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Mul -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Ne -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Or -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Popcnt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32ReinterpretF32 -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32RemS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32RemU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Rotl -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Rotr -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Shl -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32ShrS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32ShrU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Sub -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncF32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncF32U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncF64S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncF64U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncSatF32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncSatF32U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncSatF64S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32TruncSatF64U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32WrapI64 -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I32Xor -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32), ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64Add -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64And -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Clz -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        is NumericInstruction.I64Const -> InstructionType(
            ResultType(emptyList()),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Ctz -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64DivS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64DivU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Eq -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64Eqz -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64Extend16S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Extend32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Extend8S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64ExtendI32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64ExtendI32U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I32))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64GeS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64GeU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64GtS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64GtU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64LeS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64LeU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64LtS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64LtU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64Mul -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Ne -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I32))),
        )
        NumericInstruction.I64Or -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Popcnt -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64ReinterpretF64 -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64RemS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64RemU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Rotl -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Rotr -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Shl -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64ShrS -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64ShrU -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Sub -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncF32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncF32U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncF64S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncF64U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncSatF32S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncSatF32U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F32))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncSatF64S -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64TruncSatF64U -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.F64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
        NumericInstruction.I64Xor -> InstructionType(
            ResultType(listOf(ValueType.Number(NumberType.I64), ValueType.Number(NumberType.I64))),
            ResultType(listOf(ValueType.Number(NumberType.I64))),
        )
    }
}
