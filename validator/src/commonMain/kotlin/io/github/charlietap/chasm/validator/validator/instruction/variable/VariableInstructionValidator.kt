package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun VariableInstructionValidator(
    context: ValidationContext,
    instruction: VariableInstruction,
): Result<Unit, ModuleValidatorError> =
    VariableInstructionValidator(
        context = context,
        instruction = instruction,
        globalGetValidator = ::GlobalGetInstructionValidator,
        globalSetValidator = ::GlobalSetInstructionValidator,
        localGetValidator = ::LocalGetInstructionValidator,
        localSetValidator = ::LocalSetInstructionValidator,
        localTeeValidator = ::LocalTeeInstructionValidator,
    )

internal inline fun VariableInstructionValidator(
    context: ValidationContext,
    instruction: VariableInstruction,
    crossinline globalGetValidator: Validator<VariableInstruction.GlobalGet>,
    crossinline globalSetValidator: Validator<VariableInstruction.GlobalSet>,
    crossinline localGetValidator: Validator<VariableInstruction.LocalGet>,
    crossinline localSetValidator: Validator<VariableInstruction.LocalSet>,
    crossinline localTeeValidator: Validator<VariableInstruction.LocalTee>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is VariableInstruction.GlobalGet -> globalGetValidator(context, instruction)
        is VariableInstruction.GlobalSet -> globalSetValidator(context, instruction)
        is VariableInstruction.LocalGet -> localGetValidator(context, instruction)
        is VariableInstruction.LocalSet -> localSetValidator(context, instruction)
        is VariableInstruction.LocalTee -> localTeeValidator(context, instruction)
    }
}
