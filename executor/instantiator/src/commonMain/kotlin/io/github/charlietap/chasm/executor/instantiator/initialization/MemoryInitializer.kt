package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.dataAddress
import io.github.charlietap.chasm.executor.instantiator.ext.memoryAddress
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instance.DataInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal typealias MemoryInitializer = (InstantiationContext, ModuleInstance) -> Result<Unit, ModuleTrapError>

internal fun MemoryInitializer(
    context: InstantiationContext,
    instance: ModuleInstance,
): Result<Unit, ModuleTrapError> =
    MemoryInitializer(
        context = context,
        instance = instance,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
        linearMemoryInitialiser = ::LinearMemoryInitialiser,
    )

internal inline fun MemoryInitializer(
    context: InstantiationContext,
    instance: ModuleInstance,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
    crossinline linearMemoryInitialiser: LinearMemoryInitialiser,
): Result<Unit, ModuleTrapError> = binding {

    val store = context.store
    context.module.dataSegments.forEachIndexed { segmentIndex, segment ->
        val mode = segment.mode as? DataSegmentDeclaration.Mode.Active ?: return@forEachIndexed
        val offset = constantExpressionEvaluator(store, instance, context.types, mode.offset).bind().toInt()

        val memoryAddress = instance.memoryAddress(mode.memoryIndex.toInt()).bind()
        val memoryInstance = store.memory(memoryAddress)
        val dataAddress = instance.dataAddress(segmentIndex).bind()
        val dataInstance = store.data(dataAddress)

        val size = segment.payload.size
        try {
            linearMemoryInitialiser(dataInstance.bytes, memoryInstance.data, 0, offset, size, dataInstance.bytes.size, memoryInstance.size)
        } catch (e: InvocationException) {
            Err(e.error).bind<Unit>()
        }

        dataInstance.bytes = DataInstance.EMPTY
    }
}
