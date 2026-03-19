package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.tag
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.ir.module.Import as ModuleImport

internal typealias TagImportMatcher = (InstantiationContext, ModuleImport.Descriptor.Tag, ExternalValue.Tag) -> Result<Boolean, ModuleTrapError>

internal inline fun TagImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Tag,
    import: ExternalValue.Tag,
): Result<Boolean, ModuleTrapError> = binding {
    val store = context.store
    val tag = store.tag(import.address)
    val descriptorRtt = context.runtimeTypes[descriptor.type.typeIndex]

    when {
        tag.rtt === descriptorRtt -> true
        else -> {
            tag.rtt.superTypes.any { superType ->
                superType === descriptorRtt
            }
        }
    }
}
