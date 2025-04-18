@file:JvmName("ModuleAllocatorKt")

package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.export.ExportAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.global.GlobalAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.memory.MemoryAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.table.TableAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.tag.TagAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.asPredecodingContext
import io.github.charlietap.chasm.executor.instantiator.ext.functionAddress
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.predecoder.ExpressionPredecoder
import io.github.charlietap.chasm.predecoder.FunctionPredecoder
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.runtime.Arity
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.addDataAddress
import io.github.charlietap.chasm.runtime.ext.addElementAddress
import io.github.charlietap.chasm.runtime.ext.addExport
import io.github.charlietap.chasm.runtime.ext.addGlobalAddress
import io.github.charlietap.chasm.runtime.ext.addMemoryAddress
import io.github.charlietap.chasm.runtime.ext.addTableAddress
import io.github.charlietap.chasm.runtime.ext.addTagAddress
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import kotlin.jvm.JvmName
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression
import io.github.charlietap.chasm.runtime.function.Function as RuntimeFunction

internal typealias ModuleAllocator = (InstantiationContext, ModuleInstance, LongArray) -> Result<ModuleInstance, ModuleTrapError>

internal fun ModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    tableInitValues: LongArray,
): Result<ModuleInstance, ModuleTrapError> =
    ModuleAllocator(
        context = context,
        instance = instance,
        tableInitValues = tableInitValues,
        evaluator = ::ExpressionEvaluator,
        tableAllocator = ::TableAllocator,
        memoryAllocator = ::MemoryAllocator,
        tagAllocator = ::TagAllocator,
        globalAllocator = ::GlobalAllocator,
        elementAllocator = ::ElementAllocator,
        dataAllocator = ::DataAllocator,
        expressionPredecoder = ::ExpressionPredecoder,
        functionPredecoder = ::FunctionPredecoder,
        exportAllocator = ::ExportAllocator,
    )

internal inline fun ModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    tableInitValues: LongArray,
    crossinline evaluator: ExpressionEvaluator,
    crossinline tableAllocator: TableAllocator,
    crossinline memoryAllocator: MemoryAllocator,
    crossinline tagAllocator: TagAllocator,
    crossinline globalAllocator: GlobalAllocator,
    crossinline elementAllocator: ElementAllocator,
    crossinline dataAllocator: DataAllocator,
    crossinline expressionPredecoder: Predecoder<Expression, RuntimeExpression>,
    crossinline functionPredecoder: Predecoder<Function, RuntimeFunction>,
    crossinline exportAllocator: ExportAllocator,
): Result<ModuleInstance, ModuleTrapError> = binding {

    val (config, store, module) = context

    module.tables.forEachIndexed { idx, table ->
        val address = tableAllocator(store, table.type, tableInitValues[idx])
        instance.addTableAddress(address)
    }

    module.memories.forEach { memory ->
        val address = memoryAllocator(store, memory.type)
        instance.addMemoryAddress(address)
    }

    module.tags.forEach { tag ->
        val address = tagAllocator(store, tag.type)
        instance.addTagAddress(address)
    }

    module.globals.forEachIndexed { idx, global ->
        val initExpression = expressionPredecoder(context.asPredecodingContext(), global.initExpression).bind()
        val value = evaluator(config, store, instance, initExpression, Arity.Return(1))
            .flatMap { initialValue ->
                initialValue.toResultOr { InvocationError.MissingStackValue }
            }.bind()
        val address = globalAllocator(store, global.type, value)
        instance.addGlobalAddress(address)
    }

    module.elementSegments.forEachIndexed { idx, elementSegment ->
        val elementSegmentReferences = module.elementSegments.map { segment ->
            LongArray(segment.initExpressions.size) { initExpressionIndex ->
                val initExpression = expressionPredecoder(context.asPredecodingContext(), segment.initExpressions[initExpressionIndex]).bind()
                evaluator(config, store, instance, initExpression, Arity.Return(1)).bind() ?: 0L
            }
        }
        val address = elementAllocator(store, elementSegment.type, elementSegmentReferences[idx])
        instance.addElementAddress(address)
    }

    module.dataSegments.forEach { dataSegment ->
        val address = dataAllocator(store, dataSegment.initData)
        instance.addDataAddress(address)
    }

    module.functions.forEach { function ->

        val predecoded = functionPredecoder(context.asPredecodingContext(), function).bind()
        val address = instance.functionAddress(function.idx).bind()
        val functionInstance = context.store.function(address) as FunctionInstance.WasmFunction

        functionInstance.function = predecoded
    }

    module.exports.forEach { export ->
        val externalValue = exportAllocator(context, export.descriptor).bind()
        instance.addExport(ExportInstance(export.name, externalValue))
    }

    instance
}
