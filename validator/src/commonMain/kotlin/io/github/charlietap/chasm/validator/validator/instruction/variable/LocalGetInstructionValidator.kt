package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.type.InitializationStatus
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidationException
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.ext.localType
import io.github.charlietap.chasm.validator.ext.push

internal fun LocalGetInstructionValidator(
    context: ModuleValidationContext,
    instruction: VariableInstruction.LocalGet,
): Result<Unit, ModuleValidatorError> {
    val localType = context.localType(instruction.localIdx).getOrThrowValidation()
    if (localType.status == InitializationStatus.UNSET) {
        throw ModuleValidationException(InstructionValidatorError.UninitialisedLocal)
    }

    context.push(localType.type)
    return Ok(Unit)
}
