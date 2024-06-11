package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiatorImpl
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.import.Import
import io.github.charlietap.chasm.import.ImportMatcher
import io.github.charlietap.chasm.import.ImportMatcherImpl

fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
): ChasmResult<ModuleInstance, ChasmError.ExecutionError> {
    return instance(
        store = store,
        module = module,
        imports = imports,
        importMatcher = ::ImportMatcherImpl,
        instantiator = ::ModuleInstantiatorImpl,
    )
}

internal fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    importMatcher: ImportMatcher,
    instantiator: ModuleInstantiator,
): ChasmResult<ModuleInstance, ChasmError.ExecutionError> {
    val orderedImports = importMatcher(store, module, imports).component1()
        ?: return Error(ChasmError.ExecutionError(InstantiationError.MissingImport))
    return instantiator(store, module, orderedImports)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
