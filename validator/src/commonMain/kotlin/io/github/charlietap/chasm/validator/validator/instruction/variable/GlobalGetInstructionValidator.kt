package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.ext.globalType
import io.github.charlietap.chasm.validator.ext.push

internal fun GlobalGetInstructionValidator(
    context: ModuleValidationContext,
    instruction: VariableInstruction.GlobalGet,
): Result<Unit, ModuleValidatorError> {
    val globalType = context.globalType(instruction.globalIdx).getOrThrowValidation()
    context.push(globalType.valueType)
    return Ok(Unit)
}
