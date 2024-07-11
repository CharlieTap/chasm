package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.unreachable

internal fun ReturnValidator(
    context: ValidationContext,
    instruction: ControlInstruction.Return,
): Result<Unit, ModuleValidatorError> = binding {
    val expected = context.result?.types

    if (expected == null) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    context.popValues(expected ?: emptyList()).bind()
    context.unreachable().bind()
}
