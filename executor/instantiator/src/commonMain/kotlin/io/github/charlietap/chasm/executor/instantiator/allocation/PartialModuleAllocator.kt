package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.executor.instantiator.allocation.function.WasmFunctionAllocator
import io.github.charlietap.chasm.executor.instantiator.classification.ExternalValueClassifier
import io.github.charlietap.chasm.executor.instantiator.validation.ImportValidator
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.ext.addFunctionAddress
import io.github.charlietap.chasm.executor.runtime.ext.addGlobalAddress
import io.github.charlietap.chasm.executor.runtime.ext.addMemoryAddress
import io.github.charlietap.chasm.executor.runtime.ext.addTableAddress
import io.github.charlietap.chasm.executor.runtime.ext.addTagAddress
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory

internal typealias PartialModuleAllocator = (Store, Module, List<ExternalValue>) -> Result<ModuleInstance, InstantiationError>

internal fun PartialModuleAllocator(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
): Result<ModuleInstance, InstantiationError> =
    PartialModuleAllocator(
        store = store,
        module = module,
        imports = imports,
        wasmFunctionAllocator = ::WasmFunctionAllocator,
        typeAllocator = ::DefinedTypeFactory,
        classifier = ::ExternalValueClassifier,
        importValidator = ::ImportValidator,
    )

internal fun PartialModuleAllocator(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
    wasmFunctionAllocator: WasmFunctionAllocator,
    typeAllocator: DefinedTypeFactory,
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
        importValidator(import, classified).bind()
    }

    imports.forEach { import ->
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
