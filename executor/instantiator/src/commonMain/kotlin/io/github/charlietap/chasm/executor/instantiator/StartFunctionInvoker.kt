package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

typealias StartFunctionInvoker = (RuntimeConfig, Store, ModuleInstance, Address.Function) -> Result<Unit, ModuleTrapError>
