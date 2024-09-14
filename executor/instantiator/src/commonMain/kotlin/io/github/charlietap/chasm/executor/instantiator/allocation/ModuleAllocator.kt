package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

internal typealias ModuleAllocator = (Store, Module, ModuleInstance, List<ExecutionValue>, List<ReferenceValue>, List<List<ReferenceValue>>) -> Result<ModuleInstance, InstantiationError>
