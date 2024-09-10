package io.github.charlietap.chasm.validator.validator.tag

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.type.TagType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.TagTypeValidator

internal fun TagValidator(
    context: ValidationContext,
    tag: Tag,
): Result<Unit, ModuleValidatorError> =
    TagValidator(
        context = context,
        tag = tag,
        typeValidator = ::TagTypeValidator,
    )

internal fun TagValidator(
    context: ValidationContext,
    tag: Tag,
    typeValidator: Validator<TagType>,
): Result<Unit, ModuleValidatorError> = binding {
    typeValidator(context, tag.type).bind()
}
