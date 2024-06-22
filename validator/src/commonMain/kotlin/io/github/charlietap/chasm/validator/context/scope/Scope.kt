package io.github.charlietap.chasm.validator.context.scope

import io.github.charlietap.chasm.validator.context.ValidationContext

internal typealias Scope<T> = (ValidationContext, T) -> ValidationContext
