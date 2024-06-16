package io.github.charlietap.chasm.validator.validator.element

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ElementSegmentValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ElementSegmentModeValidator(
    context: ValidationContext,
    mode: ElementSegment.Mode,
): Result<Unit, ModuleValidatorError> = binding {
    when (mode) {
        is ElementSegment.Mode.Active -> {
            if (mode.tableIndex.idx.toInt() !in context.validTableIndices) {
                Err(ElementSegmentValidatorError.UnknownTable).bind<Unit>()
            }
        }
        ElementSegment.Mode.Declarative -> Unit
        ElementSegment.Mode.Passive -> Unit
    }
}
