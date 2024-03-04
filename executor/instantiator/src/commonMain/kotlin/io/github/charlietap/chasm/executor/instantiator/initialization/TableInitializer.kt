package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias TableInitializer = (Store, ModuleInstance, Module) -> Result<Unit, InvocationError>
