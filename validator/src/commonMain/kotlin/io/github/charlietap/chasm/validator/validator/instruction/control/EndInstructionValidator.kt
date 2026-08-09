package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.validator.context.LabelKind
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.peek
import io.github.charlietap.chasm.validator.ext.pop

internal fun EndInstructionValidator(
    context: ModuleValidationContext,
    instruction: ControlInstruction.End,
): Result<Unit, ModuleValidatorError> = binding {
    repeat(instruction.count) {
        val label = context.labels.peek().bind()

        if (label.kind == LabelKind.Function) {
            Err(TypeValidatorError.TypeMismatch).bind<Unit>()
        }

        if (label.kind == LabelKind.IfThen) {
            TransitionElse(context, label).bind()
        }

        FinishLabel(context, label, pushOutputs = true).bind()
        context.labels.pop().bind()
    }
}
