package io.github.charlietap.chasm.fixture.executor.instantiator.component.linking

import io.github.charlietap.chasm.executor.instantiator.CompiledModule
import io.github.charlietap.chasm.executor.instantiator.component.linking.ComponentLinkInput
import io.github.charlietap.chasm.executor.instantiator.component.linking.NamedComponentLinkInput
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.function.runtimeComponentHostFunction
import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.type.component.ComponentFunctionType
import io.github.charlietap.chasm.type.component.ComponentResourceTypeId

fun namedComponentLinkInput(
    name: String = "input",
    value: ComponentLinkInput = componentLinkInput(),
) = NamedComponentLinkInput(
    name = name,
    value = value,
)

fun componentLinkInput(): ComponentLinkInput = functionComponentLinkInput()

fun coreModuleComponentLinkInput(
    module: CompiledModule,
) = ComponentLinkInput.CoreModule(module)

fun instanceComponentLinkInput(
    imports: List<NamedComponentLinkInput> = emptyList(),
) = ComponentLinkInput.Instance(imports)

fun functionComponentLinkInput(
    function: RuntimeComponentHostFunction = runtimeComponentHostFunction(),
    type: ComponentFunctionType? = null,
    resourceTypes: Map<ComponentResourceTypeId, RuntimeResourceTypeAddress> = emptyMap(),
) = ComponentLinkInput.Function(
    function = function,
    type = type,
    resourceTypes = resourceTypes,
)

fun resourceTypeComponentLinkInput(
    address: RuntimeResourceTypeAddress = runtimeResourceTypeAddress(),
) = ComponentLinkInput.ResourceType(address)
