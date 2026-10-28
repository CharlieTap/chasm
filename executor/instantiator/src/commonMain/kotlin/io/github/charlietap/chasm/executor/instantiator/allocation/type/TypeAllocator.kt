package io.github.charlietap.chasm.executor.instantiator.allocation.type

import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

internal typealias TypeAllocator = (ModuleDeclarations, Store) -> RuntimeTypeMap

internal fun TypeAllocator(
    module: ModuleDeclarations,
    store: Store,
): RuntimeTypeMap = store.heap.registerRuntimeTypes(module.definedTypes)
