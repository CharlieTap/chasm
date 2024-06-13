package io.github.charlietap.chasm.validator.validator.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.pushRefNull

internal fun RefNullInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefNull,
): Result<Unit, ModuleValidatorError> = binding {
    context.pushRefNull(instruction.type)
}
