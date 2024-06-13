package io.github.charlietap.chasm.validator.validator.instruction.numeric

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.type.InstructionType
import io.github.charlietap.chasm.ast.type.NumberType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.InstructionTypeValidator

internal fun I32ConstInstructionValidator(
    context: ValidationContext,
    instruction: NumericInstruction.I32Const,
): Result<Unit, ModuleValidatorError> =
    I32ConstInstructionValidator(
        context = context,
        instruction = instruction,
        instructionTypeValidator = ::InstructionTypeValidator,
    )

internal fun I32ConstInstructionValidator(
    context: ValidationContext,
    instruction: NumericInstruction.I32Const,
    instructionTypeValidator: Validator<InstructionType>,
): Result<Unit, ModuleValidatorError> = binding {
    val instructionType = InstructionType(
        inputs = ResultType(emptyList()),
        outputs = ResultType(
            listOf(
                ValueType.Number(NumberType.I32),
            ),
        ),
    )
    instructionTypeValidator(context, instructionType).bind()
}
