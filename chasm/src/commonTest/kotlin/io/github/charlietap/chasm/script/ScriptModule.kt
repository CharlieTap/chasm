package io.github.charlietap.chasm.script

import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.Module

sealed interface ScriptModule {

    data class Core(val module: Module) : ScriptModule

    data class ComponentModel(val component: Component) : ScriptModule
}
