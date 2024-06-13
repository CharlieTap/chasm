package io.github.charlietap.chasm.validator.validator.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.MemoryScope
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.MemoryTypeValidator

internal fun MemoryValidator(
    context: ValidationContext,
    memory: Memory,
): Result<Unit, ModuleValidatorError> =
    MemoryValidator(
        context = context,
        memory = memory,
        scope = ::MemoryScope,
        typeValidator = ::MemoryTypeValidator,
    )

internal fun MemoryValidator(
    context: ValidationContext,
    memory: Memory,
    scope: Scope<Memory>,
    typeValidator: Validator<MemoryType>,
): Result<Unit, ModuleValidatorError> = binding {
    val scopedContext = scope(context, memory).bind()
    typeValidator(scopedContext, memory.type).bind()
}
