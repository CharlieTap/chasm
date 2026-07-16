package io.github.charlietap.chasm.script

import io.github.charlietap.chasm.embedding.shapes.ComponentInstance
import io.github.charlietap.chasm.embedding.shapes.Instance

sealed interface ScriptInstance {

    data class Core(val instance: Instance) : ScriptInstance

    data class ComponentModel(val instance: ComponentInstance) : ScriptInstance
}
