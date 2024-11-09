package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.module.ModuleValidator

typealias WasmModuleValidator = (Module) -> Result<Module, ModuleValidatorError>

fun WasmModuleValidator(
    module: Module,
): Result<Module, ModuleValidatorError> =
    WasmModuleValidator(
        module = module,
        moduleValidator = ::ModuleValidator,
    )

internal inline fun WasmModuleValidator(
    module: Module,
    crossinline moduleValidator: Validator<Module>,
): Result<Module, ModuleValidatorError> = binding {

    val context = ValidationContext(module)

    module.apply {
        moduleValidator(context, module).bind()
    }
}
