package io.github.charlietap.chasm.import

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal fun ImportMatcherImpl(
    module: Module,
    imports: List<Import>,
): Result<List<ExternalValue>, InstantiationError> {
    return module.imports.map { moduleImport ->
        imports.firstOrNull { import ->
            moduleImport.moduleName.name == import.moduleName &&
                moduleImport.entityName.name == import.entityName &&
                descriptorMatcher(moduleImport.descriptor, import.value)
        }?.value ?: return Err(InstantiationError.MissingImport)
    }.let(::Ok)
}

private fun descriptorMatcher(
    descriptor: ModuleImport.Descriptor,
    externalValue: ExternalValue,
): Boolean {
    return when (descriptor) {
        is ModuleImport.Descriptor.Function -> externalValue is ExternalValue.Function
        is ModuleImport.Descriptor.Table -> externalValue is ExternalValue.Table
        is ModuleImport.Descriptor.Memory -> externalValue is ExternalValue.Memory
        is ModuleImport.Descriptor.Global -> externalValue is ExternalValue.Global
    }
}
