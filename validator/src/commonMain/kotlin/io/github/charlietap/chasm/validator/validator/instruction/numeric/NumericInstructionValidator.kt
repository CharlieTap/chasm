package io.github.charlietap.chasm.validator.validator.instruction.numeric

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun NumericInstructionValidator(
    context: ValidationContext,
    instruction: NumericInstruction,
): Result<Unit, ModuleValidatorError> = Ok(Unit)
