package io.github.charlietap.chasm.validator.validator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.TagTypeValidator

internal fun TagImportValidator(
    context: ValidationContext,
    descriptor: Import.Descriptor.Tag,
): Result<Unit, ModuleValidatorError> =
    TagImportValidator(
        context = context,
        descriptor = descriptor,
        typeValidator = ::TagTypeValidator,
    )

internal inline fun TagImportValidator(
    context: ValidationContext,
    descriptor: Import.Descriptor.Tag,
    crossinline typeValidator: Validator<TagType>,
): Result<Unit, ModuleValidatorError> = binding {
    typeValidator(context, descriptor.type).bind()
}
