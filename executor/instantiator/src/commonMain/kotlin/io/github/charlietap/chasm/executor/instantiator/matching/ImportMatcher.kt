package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import
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

    if (requiredImports.size == imports.size) {
        val ordered = ArrayList<ExternalValue>(imports.size)
        var index = 0
        while (index < imports.size) {
            val requiredImport = requiredImports[index]
            val import = imports[index]
            if (
                requiredImport.moduleName.name != import.moduleName ||
                requiredImport.entityName.name != import.entityName ||
                !descriptorMatcher(context, requiredImport.descriptor, import.externalValue).bind()
            ) {
                break
            }
            ordered += import.externalValue
            index += 1
        }
        if (index == imports.size) return@binding ordered
    }

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
