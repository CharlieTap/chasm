package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.runtime.value.component.ComponentValue

typealias ComponentHostFunction = ComponentHostFunctionContext.(List<ComponentValue>) -> List<ComponentValue>
typealias ComponentResourceDestructor = ComponentHostFunctionContext.(Any?) -> Unit

data class ComponentHostFunctionContext(
    val store: Store,
)
