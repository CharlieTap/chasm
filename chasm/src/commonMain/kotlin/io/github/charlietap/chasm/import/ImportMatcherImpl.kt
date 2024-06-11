package io.github.charlietap.chasm.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun ImportMatcherImpl(
    store: Store,
    module: Module,
    imports: List<Import>,
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
    imports: List<Import>,
    descriptorMatcher: ImportDescriptorMatcher,
): Result<List<ExternalValue>, ModuleTrapError> = binding {
    module.imports.map { moduleImport ->
        imports.firstOrNull { import ->
            moduleImport.moduleName.name == import.moduleName &&
                moduleImport.entityName.name == import.entityName &&
                descriptorMatcher(store, module, moduleImport.descriptor, import.value).bind()
        }?.value ?: Err(InstantiationError.MissingImport).bind()
    }
}
