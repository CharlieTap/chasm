package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.function.FunctionValidator

fun WasmModuleValidatorImpl(
    module: Module,
): Result<Module, ModuleValidatorError> =
    WasmModuleValidatorImpl(
        module = module,
        functionValidator = ::FunctionValidator,
    )

internal fun WasmModuleValidatorImpl(
    module: Module,
    functionValidator: Validator<Function>,
): Result<Module, ModuleValidatorError> = binding {

    val context = ValidationContext(module)

    module.apply {
        functions.forEach { function ->
            functionValidator(context, function).bind()
        }
    }
}
