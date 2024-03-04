package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

typealias ModuleInstantiator = (Store, Module, List<ExternalValue>) -> Result<ModuleInstance, ModuleRuntimeError>
