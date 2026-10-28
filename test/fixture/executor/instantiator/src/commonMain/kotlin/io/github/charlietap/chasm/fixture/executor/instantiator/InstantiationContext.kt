package io.github.charlietap.chasm.fixture.executor.instantiator

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

fun instantiationContext(
    store: Store = store(),
    module: ModuleDeclarations = moduleDeclarations(),
    config: RuntimeConfig = runtimeConfig(),
    runtimeTypes: RuntimeTypeMap = store.heap.registerRuntimeTypes(module.definedTypes),
    instance: ModuleInstance? = null,
): InstantiationContext = InstantiationContext(
    store = store,
    module = module,
    config = config,
    runtimeTypes = runtimeTypes,
    instance = instance,
)
