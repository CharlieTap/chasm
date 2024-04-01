package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.classification.ExternalValueClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.ExternalValueClassifierImpl
import io.github.charlietap.chasm.executor.instantiator.validation.ImportValidator
import io.github.charlietap.chasm.executor.instantiator.validation.ImportValidatorImpl
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.executor.runtime.ext.addGlobalAddress
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
        typeAllocator = ::TypeAllocatorImpl,
        classifier = ::ExternalValueClassifierImpl,
        importValidator = ::ImportValidatorImpl,
    )

internal fun PartialModuleAllocatorImpl(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
    wasmFunctionAllocator: WasmFunctionAllocator,
    typeAllocator: TypeAllocator,
    classifier: ExternalValueClassifier,
    importValidator: ImportValidator,
): Result<ModuleInstance, InstantiationError> = binding {

    val instance = ModuleInstance(typeAllocator(module.types.map(Type::recursiveType)))

    val classifiedImports = imports.map { import ->
        classifier(store, import).mapError {
            InstantiationError.ClassificationError
        }.bind()
    }

    // it's an invariant that external values are ordered and matched
    // before the ModuleInstantiator is called
    module.imports.forEachIndexed { idx, import ->
        val classified = classifiedImports[idx]
        importValidator(instance, import, classified).bind()
    }

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
