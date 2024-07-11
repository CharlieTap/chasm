@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.validator.ext

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.stack.Stack
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TypeValidatorError

internal inline fun Stack<Label>.peek(): Result<Label, ModuleValidatorError> = peekOrNull().toResultOr {
    TypeValidatorError.TypeMismatch
}

internal inline fun Stack<Label>.peek(
    index: Index.LabelIndex,
): Result<Label, ModuleValidatorError> = peekNthOrNull(index.idx.toInt()).toResultOr {
    TypeValidatorError.TypeMismatch
}

internal inline fun Stack<Label>.pop(): Result<Label, ModuleValidatorError> = popOrNull().toResultOr {
    TypeValidatorError.TypeMismatch
}
