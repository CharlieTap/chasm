package io.github.charlietap.chasm.executor.instantiator.context

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

data class InstantiationContext(
    val config: RuntimeConfig,
    val store: Store,
    val module: ModuleDeclarations,
    val runtimeTypes: RuntimeTypeMap,
    val types: ModuleTypeResolver = ModuleTypeResolver(module),
    var instance: ModuleInstance? = null,
)
