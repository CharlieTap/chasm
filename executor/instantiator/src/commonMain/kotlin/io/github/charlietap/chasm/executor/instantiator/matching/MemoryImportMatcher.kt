package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.matching.MemoryTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias MemoryImportMatcher = (InstantiationContext, Import.Descriptor.Memory, ExternalValue.Memory) -> Result<Boolean, ModuleTrapError>

internal fun MemoryImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Memory,
    import: ExternalValue.Memory,
): Result<Boolean, ModuleTrapError> =
    MemoryImportMatcher(
        context = context,
        descriptor = descriptor,
        import = import,
        memoryTypeMatcher = ::MemoryTypeMatcher,
    )

internal inline fun MemoryImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Memory,
    import: ExternalValue.Memory,
    crossinline memoryTypeMatcher: TypeMatcher<MemoryType>,
): Result<Boolean, ModuleTrapError> = binding {

    val (store) = context
    val actualMemory = store.memory(import.address).bind()
    val actualMemoryType = actualMemory.type
    val requiredMemoryType = descriptor.type

    memoryTypeMatcher(actualMemoryType, requiredMemoryType, context)
}
