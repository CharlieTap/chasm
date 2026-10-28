package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

typealias ParallelModuleCodeInstaller = suspend (
    Store,
    ModuleInstance,
    RuntimeTypeMap,
    ModuleTypeResolver,
    ParallelTaskExecutor,
) -> Result<Unit, ModuleTrapError>
