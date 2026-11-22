package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.validator.WasmComponentAnalyzer

suspend fun validate(component: Component): ChasmResult<Component, ModuleError.Invalid> {
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
