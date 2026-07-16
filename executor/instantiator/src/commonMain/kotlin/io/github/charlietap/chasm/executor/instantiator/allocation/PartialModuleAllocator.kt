package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.matching.ImportMatcher
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.runtime.ext.addGlobalAddress
import io.github.charlietap.chasm.runtime.ext.addMemoryAddress
import io.github.charlietap.chasm.runtime.ext.addTableAddress
import io.github.charlietap.chasm.runtime.ext.addTagAddress
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.instanceLifetimes

internal typealias PartialModuleAllocator = (
    InstantiationContext,
    ModuleInstance,
    List<Import>,
    ModuleAllocationJournal,
) -> Result<ModuleInstance, ModuleTrapError>

internal fun PartialModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    imports: List<Import>,
    journal: ModuleAllocationJournal,
): Result<ModuleInstance, ModuleTrapError> =
    PartialModuleAllocator(
        context = context,
        instance = instance,
        imports = imports,
        journal = journal,
        importMatcher = ::ImportMatcher,
        wasmFunctionAllocator = ::WasmFunctionAllocator,
    )

internal inline fun PartialModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    imports: List<Import>,
    journal: ModuleAllocationJournal,
    crossinline importMatcher: ImportMatcher,
    crossinline wasmFunctionAllocator: WasmFunctionAllocator,
): Result<ModuleInstance, ModuleTrapError> = binding {

    val module = context.module
    val store = context.store

    val matchedImports = importMatcher(context, imports).bind()

    matchedImports.forEach { import ->
        when (import) {
            is ExternalValue.Function -> instance.addFunctionAddress(import.address)
            is ExternalValue.Table -> instance.addTableAddress(import.address)
            is ExternalValue.Memory -> instance.addMemoryAddress(import.address)
            is ExternalValue.Global -> instance.addGlobalAddress(import.address)
            is ExternalValue.Tag -> instance.addTagAddress(import.address)
        }
    }

    journal.markImports()
    store.instanceLifetimes().begin(instance, matchedImports).bind()

    module.functions.forEach { function ->
        wasmFunctionAllocator(module, instance, function, store).bind()
    }

    instance
}
