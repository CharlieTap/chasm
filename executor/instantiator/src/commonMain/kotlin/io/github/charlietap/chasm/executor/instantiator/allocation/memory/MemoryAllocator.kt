package io.github.charlietap.chasm.executor.instantiator.allocation.memory

import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

typealias MemoryAllocator = (Store, MemoryType) -> Address.Memory
