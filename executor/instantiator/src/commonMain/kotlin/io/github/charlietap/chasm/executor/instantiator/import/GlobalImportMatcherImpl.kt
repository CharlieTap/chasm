package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal fun GlobalImportMatcherImpl(
    store: Store,
    descriptor: ModuleImport.Descriptor.Global,
    import: ExternalValue.Global,
): Result<Boolean, ModuleTrapError> = binding {
    val actualGlobal = store.global(import.address).bind()
    val actualGlobalType = actualGlobal.type

    val requiredGlobalType = descriptor.type

    requiredGlobalType == actualGlobalType
}
