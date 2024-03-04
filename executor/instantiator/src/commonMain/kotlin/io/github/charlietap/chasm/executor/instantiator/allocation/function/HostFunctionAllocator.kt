package io.github.charlietap.chasm.executor.instantiator.allocation.function

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias HostFunctionAllocator = (Store, FunctionType, Function<*>) -> Address.Function
