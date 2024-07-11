package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal typealias Scope<T> = (ValidationContext, T) -> Result<ValidationContext, ModuleValidatorError>
