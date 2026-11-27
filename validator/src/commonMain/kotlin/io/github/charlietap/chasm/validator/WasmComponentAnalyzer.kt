package io.github.charlietap.chasm.validator

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.type.component.ComponentTypes
import io.github.charlietap.chasm.validator.context.component.ComponentScopeTypesFactory
import io.github.charlietap.chasm.validator.context.component.ComponentValidationContext
import io.github.charlietap.chasm.validator.error.ComponentValidatorError
import io.github.charlietap.chasm.validator.validator.component.ComponentValidator

@InternalChasmApi
typealias WasmComponentAnalyzer =
    (ComponentConfig, Component) -> Result<ComponentTypes, ComponentValidatorError>

@InternalChasmApi
fun WasmComponentAnalyzer(
    config: ComponentConfig,
    component: Component,
): Result<ComponentTypes, ComponentValidatorError> = WasmComponentAnalyzer(
    config = config,
    component = component,
    componentValidator = ::ComponentValidator,
)

internal inline fun WasmComponentAnalyzer(
    config: ComponentConfig,
    component: Component,
    crossinline componentValidator: ComponentValidator<Component>,
): Result<ComponentTypes, ComponentValidatorError> {
    val context = ComponentValidationContext(config)
    val result = componentValidator(context, component).map {
        ComponentTypes(ComponentScopeTypesFactory(context.frame))
    }
    context.reset()
    return result
}
