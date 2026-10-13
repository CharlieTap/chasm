package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun VariableInstructionValidator(
    context: ModuleValidationContext,
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
    context: ModuleValidationContext,
    instruction: VariableInstruction,
    crossinline globalGetValidator: ModuleValidator<VariableInstruction.GlobalGet>,
    crossinline globalSetValidator: ModuleValidator<VariableInstruction.GlobalSet>,
    crossinline localGetValidator: ModuleValidator<VariableInstruction.LocalGet>,
    crossinline localSetValidator: ModuleValidator<VariableInstruction.LocalSet>,
    crossinline localTeeValidator: ModuleValidator<VariableInstruction.LocalTee>,
): Result<Unit, ModuleValidatorError> {
    return when (instruction) {
        is VariableInstruction.LocalGet -> localGetValidator(context, instruction)
        is VariableInstruction.LocalSet -> localSetValidator(context, instruction)
        is VariableInstruction.LocalTee -> localTeeValidator(context, instruction)
        is VariableInstruction.GlobalGet -> globalGetValidator(context, instruction)
        is VariableInstruction.GlobalSet -> globalSetValidator(context, instruction)
    }
}
