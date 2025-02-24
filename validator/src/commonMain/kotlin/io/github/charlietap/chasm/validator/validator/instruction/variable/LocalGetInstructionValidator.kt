package io.github.charlietap.chasm.validator.validator.instruction.variable

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.type.InitializationStatus
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.localType
import io.github.charlietap.chasm.validator.ext.push

internal fun LocalGetInstructionValidator(
    context: ValidationContext,
    instruction: VariableInstruction.LocalGet,
): Result<Unit, ModuleValidatorError> = binding {
    val localType = context.localType(instruction.localIdx).bind()
    if (localType.status == InitializationStatus.UNSET) {
        Err(InstructionValidatorError.UninitialisedLocal).bind()
    }

    context.push(localType.type)
}
