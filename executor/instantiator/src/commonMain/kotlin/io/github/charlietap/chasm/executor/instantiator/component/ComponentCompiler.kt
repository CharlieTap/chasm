package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.type.component.ComponentTypes

typealias ComponentCompiler = (
    RuntimeConfig,
    Component,
    ComponentTypes,
) -> Result<PreparedComponent, ComponentPreparationError>

fun ComponentCompiler(
    config: RuntimeConfig,
    component: Component,
    types: ComponentTypes,
): Result<PreparedComponent, ComponentPreparationError> = ComponentCompiler(
    config = config,
    component = component,
    types = types,
    planner = ::ComponentPlanner,
)

internal inline fun ComponentCompiler(
    config: RuntimeConfig,
    component: Component,
    types: ComponentTypes,
    crossinline planner: ComponentPlanner,
): Result<PreparedComponent, ComponentPreparationError> = planner(config, component, types)
