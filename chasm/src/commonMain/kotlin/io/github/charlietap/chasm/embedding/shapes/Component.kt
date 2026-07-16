package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.executor.instantiator.component.PreparedComponent
import io.github.charlietap.chasm.ast.component.Component as InternalComponent

class Component internal constructor(
    internal val config: ComponentConfig,
    internal val component: InternalComponent,
    internal val analysisCache: ComponentAnalysisCache = ComponentAnalysisCache(),
    internal val preparationCache: ComponentPreparationCache<PreparedComponent> = ComponentPreparationCache(),
)
