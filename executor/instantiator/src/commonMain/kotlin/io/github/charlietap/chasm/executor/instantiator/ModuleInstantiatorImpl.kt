package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocatorImpl
import io.github.charlietap.chasm.executor.instantiator.classification.ExternalValueClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.ExternalValueClassifierImpl
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializerImpl
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializerImpl
import io.github.charlietap.chasm.executor.instantiator.validation.ImportValidator
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluatorImpl
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.FunctionInvokerImpl
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun ModuleInstantiatorImpl(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
): Result<ModuleInstance, ModuleRuntimeError> =
    ModuleInstantiatorImpl(
        store = store,
        module = module,
        imports = imports,
        classifier = ::ExternalValueClassifierImpl,
        pallocator = ::PartialModuleAllocatorImpl,
        allocator = ::ModuleAllocatorImpl,
        validator = ::ImportValidator,
        invoker = ::FunctionInvokerImpl,
        evaluator = ::ExpressionEvaluatorImpl,
        tableInitializer = ::TableInitializerImpl,
        memoryInitializer = ::MemoryInitializerImpl,
    )

internal fun ModuleInstantiatorImpl(
    store: Store,
    module: Module,
    imports: List<ExternalValue>,
    classifier: ExternalValueClassifier,
    pallocator: PartialModuleAllocator,
    allocator: ModuleAllocator,
    validator: ImportValidator,
    invoker: FunctionInvoker,
    evaluator: ExpressionEvaluator,
    tableInitializer: TableInitializer,
    memoryInitializer: MemoryInitializer,
): Result<ModuleInstance, ModuleRuntimeError> = binding {
    // todo module validation

    if (module.imports.size != imports.size) {
        Err(InstantiationError.MissingImport).bind<ModuleInstance>()
    }

    val classifiedImports = imports.map { import ->
        classifier(store, import).bind()
    }

    // it's an invariant that external values are ordered and matched
    // before the ModuleInstantiator is called
    module.imports.forEachIndexed { idx, import ->
        val classified = classifiedImports[idx]
        validator(module, import, classified).bind()
    }

    val partialInstance = pallocator(store, module, imports).bind()

    val globalInitValues = module.globals.mapNotNull { global ->
        evaluator(store, partialInstance, global.initExpression, Arity(1)).bind()
    }

    val elementSegmentReferences = module.elementSegments.map { segment ->
        segment.initExpressions.map { initExpression ->
            evaluator(store, partialInstance, initExpression, Arity(1)).bind() as ReferenceValue
        }
    }

    val instance = allocator(store, module, partialInstance, imports, globalInitValues, elementSegmentReferences).bind()

    tableInitializer(store, instance, module).bind()
    memoryInitializer(store, instance, module).bind()

    module.startFunction?.let { function ->
        val address = instance.functionAddresses[function.idx.idx.toInt()]
        invoker(store, address, emptyList())
    }

    instance
}
