package io.github.charlietap.chasm.executor.instantiator.allocation.type

import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.factory.RTTFactory

internal typealias TypeAllocator = (Module, Store) -> List<RTT>

internal fun TypeAllocator(
    module: Module,
    store: Store,
): List<RTT> = TypeAllocator(
    module = module,
    store = store,
    rttFactory = ::RTTFactory,
)

internal inline fun TypeAllocator(
    module: Module,
    store: Store,
    rttFactory: RTTFactory,
): List<RTT> {
    val cache = store.rttCache
    return module.definedTypes.map { type ->
        cache.getOrPut(type) {
            rttFactory(type, cache)
        }
    }
}
