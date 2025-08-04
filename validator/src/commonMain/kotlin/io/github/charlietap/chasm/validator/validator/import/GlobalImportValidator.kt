package io.github.charlietap.chasm.validator.validator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.GlobalTypeValidator

internal fun GlobalImportValidator(
    context: ValidationContext,
    descriptor: Import.Descriptor.Global,
): Result<Unit, ModuleValidatorError> =
    GlobalImportValidator(
        context = context,
        descriptor = descriptor,
        typeValidator = ::GlobalTypeValidator,
    )

internal inline fun GlobalImportValidator(
    context: ValidationContext,
    descriptor: Import.Descriptor.Global,
    crossinline typeValidator: Validator<GlobalType>,
): Result<Unit, ModuleValidatorError> = binding {
    typeValidator(context, descriptor.type).bind()
}
