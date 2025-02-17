package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.Import
import io.github.charlietap.chasm.ir.module.Import as ModuleImport

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

    val requiredImports = context.module.imports
    val missingImports = mutableListOf<ModuleImport>()

    val matched = requiredImports.mapNotNull { requiredImport ->
        val match = imports.firstOrNull { (moduleName, entityName, externalValue) ->
            requiredImport.moduleName.name == moduleName &&
                requiredImport.entityName.name == entityName &&
                descriptorMatcher(context, requiredImport.descriptor, externalValue).bind()
        }
        if (match == null) {
            missingImports.add(requiredImport)
        }
        match?.externalValue
    }

    if (missingImports.isNotEmpty()) {
        Err(InstantiationError.MissingImports(missingImports)).bind()
    }

    matched
}
