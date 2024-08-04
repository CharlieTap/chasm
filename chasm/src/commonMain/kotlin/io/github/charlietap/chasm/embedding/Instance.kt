package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.get
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.embedding.import.Import
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiatorImpl
import io.github.charlietap.chasm.executor.instantiator.import.ImportMatcher
import io.github.charlietap.chasm.executor.instantiator.import.ImportMatcherImpl
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

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
    val mappedImports = imports.map { import -> Triple(import.moduleName, import.entityName, import.value) }
    val orderedImports = importMatcher(store, module, mappedImports).get()
        ?: return Error(ChasmError.ExecutionError(InstantiationError.MissingImport))
    return instantiator(store, module, orderedImports)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)
}
