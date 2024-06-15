package io.github.charlietap.chasm.validator.validator.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ImportValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun FunctionImportValidator(
    context: ValidationContext,
    descriptor: Import.Descriptor.Function,
): Result<Unit, ModuleValidatorError> = binding {
    if (descriptor.typeIndex.idx.toInt() !in context.module.types.indices) {
        Err(ImportValidatorError.UnknownType).bind<Unit>()
    }
}
