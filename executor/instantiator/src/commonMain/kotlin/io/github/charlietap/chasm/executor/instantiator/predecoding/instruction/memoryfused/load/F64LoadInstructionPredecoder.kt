package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.memoryAddress
import io.github.charlietap.chasm.executor.instantiator.predecoding.LoadFactory
import io.github.charlietap.chasm.executor.instantiator.predecoding.StoreFactory
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.MemArgPredecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.F64LoadDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction.F64Load
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction

internal fun F64LoadInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction.F64Load,
): Result<DispatchableInstruction, ModuleTrapError> =
    F64LoadInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::F64LoadDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
    )

internal inline fun F64LoadInstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction.F64Load,
    crossinline dispatcher: Dispatcher<F64Load>,
    crossinline memArgPredecoder: MemArgPredecoder,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)
    val address = loadFactory(context, instruction.addressOperand)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(F64Load(address, destination, memory, memArg))
}
