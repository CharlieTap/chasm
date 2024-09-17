package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.tag
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias TagImportMatcher = (Store, ModuleImport.Descriptor.Tag, ExternalValue.Tag) -> Result<Boolean, ModuleTrapError>

internal fun TagImportMatcher(
    store: Store,
    descriptor: ModuleImport.Descriptor.Tag,
    import: ExternalValue.Tag,
): Result<Boolean, ModuleTrapError> = binding {

    val tag = store.tag(import.address).bind()

    descriptor.type == tag.type
}
