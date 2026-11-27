package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.type.component.ComponentTypes
import io.github.charlietap.chasm.validator.error.ComponentValidatorError

internal typealias WasmComponentValidator =
    (ComponentConfig, Component) -> Result<Component, ComponentValidatorError>

internal fun WasmComponentValidator(
    config: ComponentConfig,
    component: Component,
): Result<Component, ComponentValidatorError> = WasmComponentValidator(
    config = config,
    component = component,
    componentAnalyzer = ::WasmComponentAnalyzer,
)

internal inline fun WasmComponentValidator(
    config: ComponentConfig,
    component: Component,
    crossinline componentAnalyzer: (
        ComponentConfig,
        Component,
    ) -> Result<ComponentTypes, ComponentValidatorError>,
): Result<Component, ComponentValidatorError> = componentAnalyzer(config, component).map { component }
