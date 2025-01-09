package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias FunctionImportMatcher = (InstantiationContext, Import.Descriptor.Function, ExternalValue.Function) -> Result<Boolean, ModuleTrapError>

internal fun FunctionImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Function,
    import: ExternalValue.Function,
): Result<Boolean, ModuleTrapError> =
    FunctionImportMatcher(
        context = context,
        descriptor = descriptor,
        import = import,
        definedTypeMatcher = ::DefinedTypeMatcher,
    )

internal inline fun FunctionImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Function,
    import: ExternalValue.Function,
    crossinline definedTypeMatcher: TypeMatcher<DefinedType>,
): Result<Boolean, ModuleTrapError> = binding {

    val store = context.store
    val actualFunction = store.function(import.address).bind()
    val actualFunctionType = actualFunction.type
    val requiredFunctionType = descriptor.type

    definedTypeMatcher(actualFunctionType, requiredFunctionType, context)
}
