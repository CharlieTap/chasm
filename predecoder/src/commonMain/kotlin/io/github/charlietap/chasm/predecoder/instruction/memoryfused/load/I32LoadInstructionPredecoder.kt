package io.github.charlietap.chasm.predecoder.instruction.memoryfused.load

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32LoadDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.StoreFactory
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction.I32Load

internal fun I32LoadInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedMemoryInstruction.I32Load,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32LoadInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I32LoadDispatcher,
        memArgPredecoder = ::MemArgPredecoder,
        loadFactory = ::LoadFactory,
        storeFactory = ::StoreFactory,
    )

internal inline fun I32LoadInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedMemoryInstruction.I32Load,
    crossinline dispatcher: Dispatcher<I32Load>,
    crossinline memArgPredecoder: MemArgPredecoder,
    crossinline loadFactory: LoadFactory,
    crossinline storeFactory: StoreFactory,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)
    val address = loadFactory(context, instruction.addressOperand)
    val destination = storeFactory(context, instruction.destination)

    dispatcher(I32Load(address, destination, memory, memArg))
}
