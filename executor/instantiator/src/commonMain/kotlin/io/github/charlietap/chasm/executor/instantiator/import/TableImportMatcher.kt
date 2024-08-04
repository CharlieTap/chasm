package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias TableImportMatcher = (Store, Import.Descriptor.Table, ExternalValue.Table) -> Result<Boolean, ModuleTrapError>
