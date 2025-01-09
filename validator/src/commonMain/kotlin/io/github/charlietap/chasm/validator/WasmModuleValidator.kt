package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.module.ModuleValidator

typealias WasmModuleValidator = (ModuleConfig, Module) -> Result<Module, ModuleValidatorError>

fun WasmModuleValidator(
    config: ModuleConfig,
    module: Module,
): Result<Module, ModuleValidatorError> =
    WasmModuleValidator(
        config = config,
        module = module,
        moduleValidator = ::ModuleValidator,
    )

internal inline fun WasmModuleValidator(
    config: ModuleConfig,
    module: Module,
    crossinline moduleValidator: Validator<Module>,
): Result<Module, ModuleValidatorError> = binding {

    val context = ValidationContext(config, module)

    module.apply {
        moduleValidator(context, module).bind()
    }
}
