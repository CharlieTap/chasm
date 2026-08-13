package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.asErr
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.compiler.ParallelModuleCompiler
import io.github.charlietap.chasm.compiler.ParallelTaskExecutor
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
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal suspend fun ParallelModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    tableInitValues: LongArray,
    taskExecutor: ParallelTaskExecutor,
): Result<ModuleInstance, ModuleTrapError> {
    val allocation = AllocateModuleContents(
        context = context,
        instance = instance,
        tableInitValues = tableInitValues,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
        tableAllocator = ::TableAllocator,
        memoryAllocator = ::MemoryAllocator,
        tagAllocator = ::TagAllocator,
        globalAllocator = ::GlobalAllocator,
        elementAllocator = ::ElementAllocator,
        dataAllocator = ::DataAllocator,
    )
    if (allocation.isErr) return allocation.asErr()

    val compilation = ParallelModuleCompiler(
        config = context.config,
        store = context.store,
        module = context.module,
        instance = instance,
        runtimeTypes = context.runtimeTypes,
        types = context.types,
        taskExecutor = taskExecutor,
    )
    if (compilation.isErr) return compilation.asErr()

    return AllocateModuleExports(context, instance, ::ExportAllocator).map { instance }
}
