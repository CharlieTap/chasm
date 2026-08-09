package io.github.charlietap.chasm.validator.validator.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.InitializationStatus
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.LabelKind
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError
import io.github.charlietap.chasm.validator.ext.popValues
import io.github.charlietap.chasm.validator.ext.pushValues

internal fun FinishLabel(
    context: ModuleValidationContext,
    label: Label,
    pushOutputs: Boolean,
): Result<Unit, ModuleValidatorError> = binding {
    context.popValues(label.outputs.types).bind()

    if (context.operands.depth() != label.operandsDepth) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    while (context.localChanges.size > label.localChangesDepth) {
        val localIndex = context.localChanges.removeLast()
        context.locals[localIndex].status = InitializationStatus.UNSET
    }

    if (pushOutputs) {
        context.pushValues(label.outputs.types)
    }
}

internal fun TransitionElse(
    context: ModuleValidationContext,
    label: Label,
): Result<Unit, ModuleValidatorError> = binding {
    if (label.kind != LabelKind.IfThen) {
        Err(TypeValidatorError.TypeMismatch).bind<Unit>()
    }

    FinishLabel(context, label, pushOutputs = false).bind()

    label.kind = LabelKind.IfElse
    label.unreachable = false
    context.pushValues(label.inputs.types)
}
