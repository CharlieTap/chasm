package io.github.charlietap.chasm.executor.instantiator.allocation.data

import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias DataAllocator = (Store, UByteArray) -> Address.Data
