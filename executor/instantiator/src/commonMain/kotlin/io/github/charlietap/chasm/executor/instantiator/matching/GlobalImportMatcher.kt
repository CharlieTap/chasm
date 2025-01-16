package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.matching.GlobalTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias GlobalImportMatcher = (InstantiationContext, Import.Descriptor.Global, ExternalValue.Global) -> Result<Boolean, ModuleTrapError>

internal fun GlobalImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Global,
    import: ExternalValue.Global,
): Result<Boolean, ModuleTrapError> =
    GlobalImportMatcher(
        context = context,
        descriptor = descriptor,
        import = import,
        globalTypeMatcher = ::GlobalTypeMatcher,
    )

internal inline fun GlobalImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Global,
    import: ExternalValue.Global,
    crossinline globalTypeMatcher: TypeMatcher<GlobalType>,
): Result<Boolean, ModuleTrapError> = binding {

    val store = context.store
    val actualGlobal = store.global(import.address)
    val actualGlobalType = actualGlobal.type
    val requiredGlobalType = descriptor.type

    globalTypeMatcher(actualGlobalType, requiredGlobalType, context)
}
