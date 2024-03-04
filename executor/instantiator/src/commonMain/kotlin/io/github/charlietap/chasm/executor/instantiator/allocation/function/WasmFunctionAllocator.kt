package io.github.charlietap.chasm.executor.instantiator.allocation.function

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias WasmFunctionAllocator = (Store, ModuleInstance, Function) -> Result<Address.Function, InstantiationError.FailedToResolveFunctionType>
