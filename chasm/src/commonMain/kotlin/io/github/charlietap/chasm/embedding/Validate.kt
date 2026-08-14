package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError.ValidationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.validator.WasmComponentAnalyzer
import io.github.charlietap.chasm.validator.WasmModuleValidator
import io.github.charlietap.chasm.validator.error.ComponentValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

fun validate(module: Module): ChasmResult<Module, ValidationError> {
    return validate(module, ::WasmModuleValidator)
}

fun validate(component: Component): ChasmResult<Component, ValidationError> {
    return validate(component, ::WasmComponentAnalyzer)
}

internal fun validate(
    component: Component,
    analyzer: WasmComponentAnalyzer,
): ChasmResult<Component, ValidationError> {
    component.types?.let { return Success(component) }

    return analyzer(component.config, component.component)
        .mapError(ComponentValidatorError::toString)
        .mapError(::ValidationError)
        .map { types ->
            Component(
                config = component.config,
                component = component.component,
                types = types,
            )
        }.fold(::Success, ::Error)
}

internal fun validate(
    module: Module,
    validator: WasmModuleValidator,
): ChasmResult<Module, ValidationError> {
    return validate(module, validator(module.config, module.module))
}

internal fun validate(
    module: Module,
    result: Result<io.github.charlietap.chasm.ast.module.Module, ModuleValidatorError>,
): ChasmResult<Module, ValidationError> {
    return result
        .mapError(ModuleValidatorError::toString)
        .mapError(::ValidationError)
        .map { internal ->
            Module(
                config = module.config,
                module = internal,
            )
        }.fold(::Success, ::Error)
}
