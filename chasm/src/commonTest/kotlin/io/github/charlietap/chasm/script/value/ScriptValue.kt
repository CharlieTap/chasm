package io.github.charlietap.chasm.script.value

import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

sealed interface ScriptValue {

    data class Core(val value: ExecutionValue) : ScriptValue

    data class ComponentModel(val value: ComponentValue) : ScriptValue
}
