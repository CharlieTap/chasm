package io.github.charlietap.chasm.validator.validator.index

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.InstructionValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal inline fun FunctionIndexValidator(
    context: ValidationContext,
    index: Index.FunctionIndex,
): Result<Unit, ModuleValidatorError> = binding {
    if (index.idx.toInt() !in context.functions.indices) {
        Err(InstructionValidatorError.UnknownFunction).bind<Unit>()
    }
}
