package io.github.charlietap.chasm.validator.validator.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.popReference
import io.github.charlietap.chasm.validator.ext.pushI32

internal fun RefIsNullInstructionValidator(
    context: ValidationContext,
    instruction: ReferenceInstruction.RefIsNull,
): Result<Unit, ModuleValidatorError> = binding {
    context.popReference().bind()
    context.pushI32()
}
