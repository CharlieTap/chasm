package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.ext.initializeLocal
import io.github.charlietap.chasm.validator.ext.popOrThrow
import io.github.charlietap.chasm.validator.ext.push

internal fun LocalTeeInstructionValidator(
    context: ModuleValidationContext,
    instruction: VariableInstruction.LocalTee,
): Result<Unit, ModuleValidatorError> {

    val localType = context.initializeLocal(instruction.localIdx).getOrThrowValidation()

    context.popOrThrow(localType.type)
    context.push(localType.type)
    return Ok(Unit)
}
