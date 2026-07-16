package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.ComponentAnalysisCache
import io.github.charlietap.chasm.fixture.ast.component.component
import io.github.charlietap.chasm.fixture.config.componentConfig
import io.github.charlietap.chasm.type.component.ComponentScopeTypes
import io.github.charlietap.chasm.type.component.ComponentTypes
import io.github.charlietap.chasm.ast.component.Component as InternalComponent

fun publicComponent(
    config: ComponentConfig = componentConfig(),
    component: InternalComponent = component(),
    types: ComponentTypes? = null,
) = Component(
    config = config,
    component = component,
    analysisCache = ComponentAnalysisCache(types),
)

fun componentTypes(
    root: ComponentScopeTypes = ComponentScopeTypes(),
) = ComponentTypes(root)
