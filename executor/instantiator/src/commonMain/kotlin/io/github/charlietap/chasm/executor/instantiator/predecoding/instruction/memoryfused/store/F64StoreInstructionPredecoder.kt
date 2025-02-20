package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.memoryAddress
import io.github.charlietap.chasm.executor.instantiator.predecoding.LoadFactory
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.MemArgPredecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.F64StoreDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction.F64Store
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction

internal fun F64StoreInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction.F64Store,
): Result<DispatchableInstruction, ModuleTrapError> =
    F64StoreInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::F64StoreDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
        loadFactory = ::LoadFactory,
    )

internal inline fun F64StoreInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction.F64Store,
    crossinline dispatcher: Dispatcher<F64Store>,
    crossinline memArgPredecoder: MemArgPredecoder,
    crossinline loadFactory: LoadFactory,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)
    val address = loadFactory(context, instruction.addressOperand)
    val value = loadFactory(context, instruction.valueOperand)

    dispatcher(F64Store(value, address, memory, memArg))
}
