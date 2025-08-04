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
        memoryImportValidator = ::MemoryImportValidator,
    )

internal inline fun ImportValidator(
    context: ValidationContext,
    import: Import,
    crossinline functionImportValidator: Validator<Import.Descriptor.Function>,
    crossinline memoryImportValidator: Validator<Import.Descriptor.Memory>,
): Result<Unit, ModuleValidatorError> = binding {
    when (val descriptor = import.descriptor) {
        is Import.Descriptor.Function -> functionImportValidator(context, descriptor).bind()
        is Import.Descriptor.Global -> Unit
        is Import.Descriptor.Memory -> memoryImportValidator(context, descriptor).bind()
        is Import.Descriptor.Table -> Unit
        is Import.Descriptor.Tag -> Unit
    }
}
