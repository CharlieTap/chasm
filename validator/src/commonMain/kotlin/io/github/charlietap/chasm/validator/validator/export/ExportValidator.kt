package io.github.charlietap.chasm.validator.validator.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ExportValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ExportValidator(
    context: ValidationContext,
    export: Export,
): Result<Unit, ModuleValidatorError> =
    ExportValidator(
        context = context,
        export = export,
        functionExportValidator = ::FunctionExportValidator,
        globalExportValidator = ::GlobalExportValidator,
        memoryExportValidator = ::MemoryExportValidator,
        tableExportValidator = ::TableExportValidator,
        tagExportValidator = ::TagExportValidator,
    )

internal inline fun ExportValidator(
    context: ValidationContext,
    export: Export,
    crossinline functionExportValidator: Validator<Export.Descriptor.Function>,
    crossinline globalExportValidator: Validator<Export.Descriptor.Global>,
    crossinline memoryExportValidator: Validator<Export.Descriptor.Memory>,
    crossinline tableExportValidator: Validator<Export.Descriptor.Table>,
    crossinline tagExportValidator: Validator<Export.Descriptor.Tag>,
): Result<Unit, ModuleValidatorError> = binding {
    when (val descriptor = export.descriptor) {
        is Export.Descriptor.Function -> functionExportValidator(context, descriptor).bind()
        is Export.Descriptor.Global -> globalExportValidator(context, descriptor).bind()
        is Export.Descriptor.Memory -> memoryExportValidator(context, descriptor).bind()
        is Export.Descriptor.Table -> tableExportValidator(context, descriptor).bind()
        is Export.Descriptor.Tag -> tagExportValidator(context, descriptor).bind()
    }

    if (!context.exportNames.add(export.name.name)) {
        Err(ExportValidatorError.DuplicateExportNames).bind<Unit>()
    }
}
