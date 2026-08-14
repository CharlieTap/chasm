package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal inline fun ModuleValidationScope(
    context: ModuleValidationContext,
    config: ModuleConfig,
    module: Module,
    crossinline validator: ModuleValidator<Module>,
): Result<Module, ModuleValidatorError> {
    context.reset(config, module)
    val result = validator(context, module).map { module }
    context.clearLocalState()
    return result
}
