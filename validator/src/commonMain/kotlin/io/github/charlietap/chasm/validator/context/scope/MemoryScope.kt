package io.github.charlietap.chasm.validator.context.scope

import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.validator.context.TypeContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext

internal fun MemoryScope(
    context: ValidationContext,
    memory: Memory,
): ValidationContext = context.copy(
    typeContext = TypeContextImpl(
        limitsMaximum = 65536u,
    ),
)
