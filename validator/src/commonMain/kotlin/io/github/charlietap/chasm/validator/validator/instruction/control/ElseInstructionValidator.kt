package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.peek

internal fun ElseInstructionValidator(
    context: ModuleValidationContext,
    @Suppress("UNUSED_PARAMETER") instruction: ControlInstruction.Else,
): Result<Unit, ModuleValidatorError> = binding {
    val label = context.labels.peek().bind()
    TransitionElse(context, label).bind()
}
