package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.validator.context.TypeContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun MemoryScope(
    context: ValidationContext,
    memory: Memory,
): Result<ValidationContext, ModuleValidatorError> = context.copy(
    typeContext = TypeContextImpl(
        limitsMaximum = 65536u,
    ),
).let(::Ok)
