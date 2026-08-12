package io.github.charlietap.chasm.executor.instantiator.allocation.type

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

internal typealias TypeAllocator = (Module, Store) -> RuntimeTypeMap

internal fun TypeAllocator(
    module: Module,
    store: Store,
): RuntimeTypeMap = store.runtimeTypes.register(module.definedTypes)
