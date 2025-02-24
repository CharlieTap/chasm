package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.matching.DefinedTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal typealias FunctionImportMatcher = (InstantiationContext, Import.Descriptor.Function, ExternalValue.Function) -> Result<Boolean, ModuleTrapError>

internal fun FunctionImportMatcher(
    context: InstantiationContext,
    descriptor: Import.Descriptor.Function,
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
    descriptor: Import.Descriptor.Function,
    import: ExternalValue.Function,
    crossinline definedTypeMatcher: TypeMatcher<DefinedType>,
): Result<Boolean, ModuleTrapError> = binding {

    val store = context.store
    val actualFunction = store.function(import.address)
    val actualFunctionType = actualFunction.type
    val requiredFunctionType = descriptor.type

    definedTypeMatcher(actualFunctionType, requiredFunctionType, context)
}
