@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._binary
import io.github.charlietap.chasm.embedding.shapes._config
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.embedding.shapes._validated
import io.github.charlietap.chasm.validator.WasmComponentAnalyzer
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

fun validate(module: Module): ChasmResult<Module, ModuleError.Invalid> {
    return validate(module, ::WasmModuleValidator)
}

fun validate(component: Component): ChasmResult<Component, ModuleError.Invalid> {
    return validate(component, ::WasmComponentAnalyzer)
}

internal fun validate(
    component: Component,
    analyzer: WasmComponentAnalyzer,
): ChasmResult<Component, ModuleError.Invalid> {

    component.types?.let { return Success(component) }

    return analyzer(component.config, component.component).fold(
        success = { types ->
            Success(Component(config = component.config, component = component.component, types = types))
        },
        failure = { error ->
            Error(ModuleError.Invalid(error.toString()))
        },
    )
}

internal fun validate(
    module: Module,
    validator: WasmModuleValidator,
): ChasmResult<Module, ModuleError.Invalid> {

    if (module._validated()) {
        return Success(module)
    }

    return validate(module, validator(module._config(), module._module()))
}

internal fun validate(
    module: Module,
    result: Result<io.github.charlietap.chasm.ast.module.Module, ModuleValidatorError>,
): ChasmResult<Module, ModuleError.Invalid> {
    return result.fold(
        success = { internal ->
            Success(_module(config = module._config(), module = internal, binary = module._binary(), validated = true))
        },
        failure = { error ->
            Error(ModuleError.Invalid(error.toString()))
        },
    )
}
