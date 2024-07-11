package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun UnreachableInstructionValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Unreachable,
): Result<Unit, ModuleValidatorError> = binding {
    context.unreachable().bind()
}
