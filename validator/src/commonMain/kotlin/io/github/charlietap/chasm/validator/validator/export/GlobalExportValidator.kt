package io.github.charlietap.chasm.validator.validator.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ExportValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun GlobalExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Global,
): Result<Unit, ModuleValidatorError> = binding {
    if (descriptor.globalIndex.idx.toInt() !in context.globals.indices) {
        Err(ExportValidatorError.UnknownGlobal).bind<Unit>()
    }
}
