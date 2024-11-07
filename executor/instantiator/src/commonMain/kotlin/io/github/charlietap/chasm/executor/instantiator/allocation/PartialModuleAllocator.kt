package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.matching.ImportMatcher
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.executor.runtime.ext.addGlobalAddress
import io.github.charlietap.chasm.executor.runtime.ext.addMemoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.addTableAddress
import io.github.charlietap.chasm.executor.runtime.ext.addTagAddress
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.Import
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory

internal typealias PartialModuleAllocator = (InstantiationContext, List<Import>) -> Result<ModuleInstance, InstantiationError>

internal fun PartialModuleAllocator(
    context: InstantiationContext,
    imports: List<Import>,
): Result<ModuleInstance, InstantiationError> =
    PartialModuleAllocator(
        context = context,
        imports = imports,
        wasmFunctionAllocator = ::WasmFunctionAllocator,
        typeAllocator = ::DefinedTypeFactory,
        importMatcher = ::ImportMatcher,
    )

internal fun PartialModuleAllocator(
    context: InstantiationContext,
    imports: List<Import>,
    wasmFunctionAllocator: WasmFunctionAllocator,
    typeAllocator: DefinedTypeFactory,
    importMatcher: ImportMatcher,
): Result<ModuleInstance, InstantiationError> = binding {

    val (store, module) = context

    val instance = ModuleInstance(typeAllocator(module.types.map(Type::recursiveType)))

    val matchedImports = importMatcher(context, imports).mapError {
        InstantiationError.MissingImport
    }.bind()

    matchedImports.forEach { import ->
        when (import) {
            is ExternalValue.Function -> instance.addFunctionAddress(import.address)
            is ExternalValue.Table -> instance.addTableAddress(import.address)
            is ExternalValue.Memory -> instance.addMemoryAddress(import.address)
            is ExternalValue.Global -> instance.addGlobalAddress(import.address)
            is ExternalValue.Tag -> instance.addTagAddress(import.address)
        }
    }

    module.functions.forEach { function ->
        val address = wasmFunctionAllocator(store, instance, function).bind()
        instance.addFunctionAddress(address)
    }

    instance
}
