package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocatorImpl
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun PartialModuleAllocatorImpl(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
): Result<ModuleInstance, InstantiationError> =
    PartialModuleAllocatorImpl(
        store = store,
        module = module,
        imports = imports,
        wasmFunctionAllocator = ::WasmFunctionAllocatorImpl,
    )

internal fun PartialModuleAllocatorImpl(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
    wasmFunctionAllocator: WasmFunctionAllocator,
): Result<ModuleInstance, InstantiationError> = binding {

    val instance = ModuleInstance(module.types.map(Type::functionType))

    imports.forEach { import ->
        when (import) {
            is ExternalValue.Function -> instance.addFunctionAddress(import.address)
            is ExternalValue.Table -> Unit
            is ExternalValue.Memory -> Unit
            is ExternalValue.Global -> instance.addGlobalAddress(import.address)
        }
    }

    module.functions.forEach { function ->
        val address = wasmFunctionAllocator(store, instance, function).bind()
        instance.addFunctionAddress(address)
    }

    instance
}
