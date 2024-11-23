package io.github.charlietap.chasm.validator.validator.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ExportValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.index.MemoryIndexValidator

internal fun MemoryExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Memory,
): Result<Unit, ModuleValidatorError> =
    MemoryExportValidator(
        context = context,
        descriptor = descriptor,
        memoryIndexValidator = ::MemoryIndexValidator,
    )

internal inline fun MemoryExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Memory,
    crossinline memoryIndexValidator: Validator<Index.MemoryIndex>,
): Result<Unit, ModuleValidatorError> = binding {
    memoryIndexValidator(context, descriptor.memoryIndex).mapError {
        ExportValidatorError.UnknownMemory
    }.bind()
}
