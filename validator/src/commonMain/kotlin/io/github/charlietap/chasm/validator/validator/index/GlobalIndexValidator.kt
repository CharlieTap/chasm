package io.github.charlietap.chasm.validator.validator.index

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal inline fun GlobalIndexValidator(
    context: ModuleValidationContext,
    index: Index.GlobalIndex,
): Result<Unit, ModuleValidatorError> {
    val globalIndex = index.idx.toInt()
    return if (globalIndex >= 0 && globalIndex < context.visibleGlobalCount) {
        Ok(Unit)
    } else {
        Err(InstructionValidatorError.UnknownGlobal)
    }
}
