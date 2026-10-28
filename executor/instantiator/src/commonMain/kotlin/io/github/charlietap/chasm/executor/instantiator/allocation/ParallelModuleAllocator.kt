package io.github.charlietap.chasm.executor.instantiator.allocation

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.asErr
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.executor.instantiator.ParallelModuleCodeInstaller
import io.github.charlietap.chasm.executor.instantiator.allocation.data.DataAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.element.ElementAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.export.ExportAllocator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.resource.allocation.GlobalAllocator
import io.github.charlietap.chasm.runtime.resource.allocation.MemoryAllocator
import io.github.charlietap.chasm.runtime.resource.allocation.TableAllocator
import io.github.charlietap.chasm.runtime.resource.allocation.TagAllocator

internal suspend fun ParallelModuleAllocator(
    context: InstantiationContext,
    instance: ModuleInstance,
    tableInitValues: LongArray,
    taskExecutor: ParallelTaskExecutor,
    codeInstaller: ParallelModuleCodeInstaller,
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

    val installation = codeInstaller(
        context.store,
        instance,
        context.runtimeTypes,
        context.types,
        taskExecutor,
    )
    if (installation.isErr) return installation.asErr()

    return AllocateModuleExports(context, instance, ::ExportAllocator).map { instance }
}
