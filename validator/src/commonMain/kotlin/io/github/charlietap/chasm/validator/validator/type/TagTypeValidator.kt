package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TagValidatorError

internal fun TagTypeValidator(
    context: ValidationContext,
    type: TagType,
): Result<Unit, ModuleValidatorError> = binding {
    if (type.type.results.types.isNotEmpty()) {
        Err(TagValidatorError.InvalidTagType).bind()
    }
}
