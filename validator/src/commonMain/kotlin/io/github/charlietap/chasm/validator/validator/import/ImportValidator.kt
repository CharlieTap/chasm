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
        globalImportValidator = ::GlobalImportValidator,
        memoryImportValidator = ::MemoryImportValidator,
        tableImportValidator = ::TableImportValidator,
        tagImportValidator = ::TagImportValidator,
    )

internal inline fun ImportValidator(
    context: ValidationContext,
    import: Import,
    crossinline functionImportValidator: Validator<Import.Descriptor.Function>,
    crossinline globalImportValidator: Validator<Import.Descriptor.Global>,
    crossinline memoryImportValidator: Validator<Import.Descriptor.Memory>,
    crossinline tableImportValidator: Validator<Import.Descriptor.Table>,
    crossinline tagImportValidator: Validator<Import.Descriptor.Tag>,
): Result<Unit, ModuleValidatorError> = binding {
    when (val descriptor = import.descriptor) {
        is Import.Descriptor.Function -> functionImportValidator(context, descriptor).bind()
        is Import.Descriptor.Global -> globalImportValidator(context, descriptor).bind()
        is Import.Descriptor.Memory -> memoryImportValidator(context, descriptor).bind()
        is Import.Descriptor.Table -> tableImportValidator(context, descriptor).bind()
        is Import.Descriptor.Tag -> tagImportValidator(context, descriptor).bind()
    }
}
