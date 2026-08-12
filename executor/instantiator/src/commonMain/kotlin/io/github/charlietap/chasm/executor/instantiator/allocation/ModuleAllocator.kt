@file:JvmName("ModuleAllocatorKt")

package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.ModuleCompiler
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.export.ExportAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.addDataAddress
import io.github.charlietap.chasm.runtime.ext.addElementAddress
import io.github.charlietap.chasm.runtime.ext.addExport
import io.github.charlietap.chasm.runtime.ext.addGlobalAddress
import io.github.charlietap.chasm.runtime.ext.addMemoryAddress
import io.github.charlietap.chasm.runtime.ext.addTableAddress
import io.github.charlietap.chasm.runtime.ext.addTagAddress
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import kotlin.jvm.JvmName

internal typealias ModuleAllocator = (
    InstantiationContext,
    ModuleInstance,
    LongArray,
    CompilerDiagnostics?,
) -> Result<ModuleInstance, ModuleTrapError>

internal typealias ModuleCompiler = (
    RuntimeConfig,
    Store,
    Module,
    ModuleInstance,
    RuntimeTypeMap,
    ModuleTypeResolver,
    CompilerDiagnostics?,
) -> Result<Unit, ModuleTrapError>

internal fun ModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    tableInitValues: LongArray,
    compilerDiagnostics: CompilerDiagnostics?,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleAllocator(
        context = context,
        instance = instance,
        tableInitValues = tableInitValues,
        compilerDiagnostics = compilerDiagnostics,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
        tableAllocator = ::TableAllocator,
        memoryAllocator = ::MemoryAllocator,
        tagAllocator = ::TagAllocator,
        globalAllocator = ::GlobalAllocator,
        elementAllocator = ::ElementAllocator,
        dataAllocator = ::DataAllocator,
        moduleCompiler = ::ModuleCompiler,
        exportAllocator = ::ExportAllocator,
    )

internal inline fun ModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    tableInitValues: LongArray,
    compilerDiagnostics: CompilerDiagnostics? = null,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
    crossinline tableAllocator: TableAllocator,
    crossinline memoryAllocator: MemoryAllocator,
    crossinline tagAllocator: TagAllocator,
    crossinline globalAllocator: GlobalAllocator,
    crossinline elementAllocator: ElementAllocator,
    crossinline dataAllocator: DataAllocator,
    crossinline moduleCompiler: ModuleCompiler,
    crossinline exportAllocator: ExportAllocator,
): Result<ModuleInstance, ModuleTrapError> = binding {

    val store = context.store
    val module = context.module

    module.tables.forEachIndexed { idx, table ->
        val address = tableAllocator(store, context.types.resolve(table.type), tableInitValues[idx])
        instance.addTableAddress(address)
    }

    module.memories.forEach { memory ->
        val address = memoryAllocator(store, memory.type)
        instance.addMemoryAddress(address)
    }

    module.tags.forEach { tag ->
        val type = context.types.resolve(tag.type)
        val rtt = context.runtimeTypes[type.typeIndex]
        val address = tagAllocator(store, rtt, type)
        instance.addTagAddress(address)
    }

    module.globals.forEach { global ->
        val value = constantExpressionEvaluator(store, instance, context.types, global.initExpression).bind()
        val address = globalAllocator(store, context.types.resolve(global.type), value)
        instance.addGlobalAddress(address)
    }

    module.elementSegments.forEach { elementSegment ->
        val references = LongArray(elementSegment.initExpressions.size) { initExpressionIndex ->
            constantExpressionEvaluator(
                store,
                instance,
                context.types,
                elementSegment.initExpressions[initExpressionIndex],
            ).bind()
        }
        val address = elementAllocator(
            store,
            context.types.resolve(elementSegment.type),
            references,
        )
        instance.addElementAddress(address)
    }

    module.dataSegments.forEach { dataSegment ->
        val address = dataAllocator(store, dataSegment.initData)
        instance.addDataAddress(address)
    }

    moduleCompiler(
        context.config,
        store,
        module,
        instance,
        context.runtimeTypes,
        context.types,
        compilerDiagnostics,
    ).bind()

    module.exports.forEach { export ->
        val externalValue = exportAllocator(context, export.descriptor).bind()
        instance.addExport(ExportInstance(export.name, externalValue))
    }

    instance
}
