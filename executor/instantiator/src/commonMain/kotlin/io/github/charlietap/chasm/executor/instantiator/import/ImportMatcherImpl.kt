package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

fun ImportMatcherImpl(
    store: Store,
    module: Module,
    imports: List<Triple<String, String, ExternalValue>>,
): Result<List<ExternalValue>, ModuleTrapError> =
    ImportMatcherImpl(
        store = store,
        module = module,
        imports = imports,
        descriptorMatcher = ::ImportDescriptorMatcherImpl,
    )

internal fun ImportMatcherImpl(
    store: Store,
    module: Module,
    imports: List<Triple<String, String, ExternalValue>>,
    descriptorMatcher: ImportDescriptorMatcher,
): Result<List<ExternalValue>, ModuleTrapError> = binding {
    module.imports.map { moduleImport ->
        imports.firstOrNull { (moduleName, entityName, externalValue) ->
            moduleImport.moduleName.name == moduleName &&
                moduleImport.entityName.name == entityName &&
                descriptorMatcher(store, module, moduleImport.descriptor, externalValue).bind()
        }?.third ?: Err(InstantiationError.MissingImport).bind<Nothing>()
    }
}
