package io.github.charlietap.chasm.executor.instantiator.component.linking

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId

data class NamedComponentLinkInput(
    val name: String,
    val value: ComponentLinkInput,
)

sealed interface ComponentLinkInput {

    data class CoreModule(
        val module: CompiledModule,
    ) : ComponentLinkInput

    data class Instance(
        val imports: List<NamedComponentLinkInput>,
    ) : ComponentLinkInput

    data class Function(
        val function: RuntimeComponentHostFunction,
        val type: ComponentFunctionType? = null,
        val resourceTypes: Map<ComponentResourceTypeId, RuntimeResourceTypeAddress> = emptyMap(),
    ) : ComponentLinkInput

    data class ResourceType(
        val address: RuntimeResourceTypeAddress,
    ) : ComponentLinkInput
}
