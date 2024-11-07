package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.fold
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.ImportableMapper
import io.github.charlietap.chasm.embedding.transform.Mapper
import io.github.charlietap.chasm.executor.instantiator.ModuleInstantiator
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.Import as RuntimeImport

fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
): ChasmResult<Instance, ChasmError.ExecutionError> {
    return instance(
        store = store,
        module = module,
        imports = imports,
        instantiator = ::ModuleInstantiator,
        importableMapper = ImportableMapper,
    )
}

internal fun instance(
    store: Store,
    module: Module,
    imports: List<Import>,
    instantiator: ModuleInstantiator,
    importableMapper: Mapper<Importable, ExternalValue>,
): ChasmResult<Instance, ChasmError.ExecutionError> {

    val mappedImports = imports.map { import ->
        RuntimeImport(
            import.moduleName,
            import.entityName,
            importableMapper.map(import.value),
        )
    }

    return instantiator(store.store, module.module, mappedImports)
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .map(::Instance)
        .fold(::Success, ::Error)
}
