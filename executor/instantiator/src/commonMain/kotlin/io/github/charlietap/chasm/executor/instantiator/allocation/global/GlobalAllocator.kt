package io.github.charlietap.chasm.executor.instantiator.allocation.global

import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

internal typealias GlobalAllocator = (Store, GlobalType, ExecutionValue) -> Address.Global
