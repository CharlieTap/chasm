package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.module.ModuleValidator

fun WasmModuleValidatorImpl(
    module: Module,
): Result<Module, ModuleValidatorError> =
    WasmModuleValidatorImpl(
        module = module,
        moduleValidator = ::ModuleValidator,
    )

internal fun WasmModuleValidatorImpl(
    module: Module,
    moduleValidator: Validator<Module>,
): Result<Module, ModuleValidatorError> = binding {

    val context = ValidationContext(module)

    module.apply {
        moduleValidator(context, module).bind()
    }
}
