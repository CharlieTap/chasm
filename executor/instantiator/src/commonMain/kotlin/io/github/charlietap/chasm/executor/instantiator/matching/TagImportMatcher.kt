package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.tag
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.matching.TagTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.ir.module.Import as ModuleImport

internal typealias TagImportMatcher = (InstantiationContext, ModuleImport.Descriptor.Tag, ExternalValue.Tag) -> Result<Boolean, ModuleTrapError>

internal fun TagImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Tag,
    import: ExternalValue.Tag,
): Result<Boolean, ModuleTrapError> =
    TagImportMatcher(
        context = context,
        descriptor = descriptor,
        import = import,
        tagTypeMatcher = ::TagTypeMatcher,
    )

internal inline fun TagImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Tag,
    import: ExternalValue.Tag,
    crossinline tagTypeMatcher: TypeMatcher<TagType>,
): Result<Boolean, ModuleTrapError> = binding {
    val store = context.store
    val tag = store.tag(import.address)

    tagTypeMatcher(tag.type, descriptor.type, context)
}
