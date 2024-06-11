package io.github.charlietap.chasm.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.type.ext.definedType
import io.github.charlietap.chasm.executor.type.ext.functionType
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal fun FunctionImportMatcherImpl(
    store: Store,
    module: Module,
    descriptor: ModuleImport.Descriptor.Function,
    import: ExternalValue.Function,
): Result<Boolean, ModuleTrapError> = binding {
    val actualFunction = store.function(import.address).bind()
    val actualFunctionType = actualFunction.functionType()

    val requiredImportType = module.types[descriptor.typeIndex.idx.toInt()]
    val requiredFunctionType = requiredImportType.recursiveType.definedType().functionType()

    requiredFunctionType == actualFunctionType
}
