package io.github.charlietap.chasm.validator.validator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ImportValidator(
    context: ValidationContext,
    import: Import,
): Result<Unit, ModuleValidatorError> =
    ImportValidator(
        context = context,
        import = import,
        functionImportValidator = ::FunctionImportValidator,
    )

internal fun ImportValidator(
    context: ValidationContext,
    import: Import,
    functionImportValidator: Validator<Import.Descriptor.Function>,
): Result<Unit, ModuleValidatorError> = binding {
    when (val descriptor = import.descriptor) {
        is Import.Descriptor.Function -> functionImportValidator(context, descriptor).bind()
        is Import.Descriptor.Global -> Unit
        is Import.Descriptor.Memory -> Unit
        is Import.Descriptor.Table -> Unit
    }
}
