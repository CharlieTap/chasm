package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal typealias Validator<T> = (ValidationContext, T) -> Result<Unit, ModuleValidatorError>
