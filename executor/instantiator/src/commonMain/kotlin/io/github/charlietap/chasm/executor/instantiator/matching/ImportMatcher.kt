package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.Import

typealias ImportMatcher = (InstantiationContext, List<Import>) -> Result<List<ExternalValue>, ModuleTrapError>

fun ImportMatcher(
    context: InstantiationContext,
    imports: List<Import>,
): Result<List<ExternalValue>, ModuleTrapError> =
    ImportMatcher(
        context = context,
        imports = imports,
        descriptorMatcher = ::ImportDescriptorMatcher,
    )

internal inline fun ImportMatcher(
    context: InstantiationContext,
    imports: List<Import>,
    crossinline descriptorMatcher: ImportDescriptorMatcher,
): Result<List<ExternalValue>, ModuleTrapError> = binding {
    val module = context.module
    module.imports.map { moduleImport ->
        imports
            .firstOrNull { (moduleName, entityName, externalValue) ->
                moduleImport.moduleName.name == moduleName &&
                    moduleImport.entityName.name == entityName &&
                    descriptorMatcher(context, moduleImport.descriptor, externalValue).bind()
            }?.externalValue ?: Err(InstantiationError.MissingImport).bind<Nothing>()
    }
}
