package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.matching.ImportMatcher
import io.github.charlietap.chasm.ir.module.Type
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.runtime.ext.addGlobalAddress
import io.github.charlietap.chasm.runtime.ext.addMemoryAddress
import io.github.charlietap.chasm.runtime.ext.addTableAddress
import io.github.charlietap.chasm.runtime.ext.addTagAddress
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.Import
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal typealias PartialModuleAllocator = (InstantiationContext, List<Import>) -> Result<ModuleInstance, ModuleTrapError>

internal fun PartialModuleAllocator(
    context: InstantiationContext,
    imports: List<Import>,
): Result<ModuleInstance, ModuleTrapError> =
    PartialModuleAllocator(
        context = context,
        imports = imports,
        wasmFunctionAllocator = ::WasmFunctionAllocator,
        typeAllocator = ::TypeAllocator,
        importMatcher = ::ImportMatcher,
    )

internal inline fun PartialModuleAllocator(
    context: InstantiationContext,
    imports: List<Import>,
    crossinline wasmFunctionAllocator: WasmFunctionAllocator,
    crossinline typeAllocator: TypeAllocator,
    crossinline importMatcher: ImportMatcher,
): Result<ModuleInstance, ModuleTrapError> = binding {

    val module = context.module
    val types = typeAllocator(context, module.types.map(Type::recursiveType))

    val instance = ModuleInstance(types)
    context.instance = instance

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

    module.functions.forEach { function ->
        wasmFunctionAllocator(context, instance, function).bind()
    }

    instance
}
