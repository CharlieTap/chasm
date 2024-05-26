package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias MemoryInitializer = (Store, ModuleInstance, Module) -> Result<Unit, ModuleTrapError>
