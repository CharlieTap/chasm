package io.github.charlietap.chasm.validator.validator.export

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ExportValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun MemoryExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Memory,
): Result<Unit, ModuleValidatorError> = binding {
    if (descriptor.memoryIndex.idx.toInt() !in context.memories.indices) {
        Err(ExportValidatorError.UnknownMemory).bind<Unit>()
    }
}
