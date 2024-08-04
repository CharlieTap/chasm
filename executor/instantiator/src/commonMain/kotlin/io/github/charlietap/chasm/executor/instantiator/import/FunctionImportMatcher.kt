package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias FunctionImportMatcher = (Store, Module, Import.Descriptor.Function, ExternalValue.Function) -> Result<Boolean, ModuleTrapError>
