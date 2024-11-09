package io.github.charlietap.chasm.validator.validator.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.DataSegmentValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.ExpressionValidator

internal fun DataSegmentModeValidator(
    context: ValidationContext,
    mode: DataSegment.Mode,
): Result<Unit, ModuleValidatorError> =
    DataSegmentModeValidator(
        context = context,
        mode = mode,
        expressionValidator = ::ExpressionValidator,
    )

internal inline fun DataSegmentModeValidator(
    context: ValidationContext,
    mode: DataSegment.Mode,
    crossinline expressionValidator: Validator<Expression>,
): Result<Unit, ModuleValidatorError> = binding {
    when (mode) {
        is DataSegment.Mode.Active -> {
            if (mode.memoryIndex.idx.toInt() !in context.memories.indices) {
                Err(DataSegmentValidatorError.UnknownMemory).bind<Unit>()
            }
            expressionValidator(context, mode.offset).bind()
        }
        DataSegment.Mode.Passive -> Unit
    }
}
