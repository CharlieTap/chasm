package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias PartialModuleAllocator = (Store, Module, List<ExternalValue>) -> Result<ModuleInstance, InstantiationError>
