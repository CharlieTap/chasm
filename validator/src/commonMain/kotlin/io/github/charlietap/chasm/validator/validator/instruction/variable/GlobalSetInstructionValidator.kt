package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidationException
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.getOrThrowValidation
import io.github.charlietap.chasm.validator.ext.globalType
import io.github.charlietap.chasm.validator.ext.popOrThrow

internal fun GlobalSetInstructionValidator(
    context: ModuleValidationContext,
    instruction: VariableInstruction.GlobalSet,
): Result<Unit, ModuleValidatorError> {
    val globalType = context.globalType(instruction.globalIdx).getOrThrowValidation()
    if (globalType.mutability == Mutability.Const) {
        throw ModuleValidationException(InstructionValidatorError.MutationOfAConstGlobal)
    }

    context.popOrThrow(globalType.valueType)
    return Ok(Unit)
}
