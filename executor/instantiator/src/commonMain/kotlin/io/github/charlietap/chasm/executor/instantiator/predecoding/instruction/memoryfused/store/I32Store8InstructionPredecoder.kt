package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.memoryfused.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.memoryAddress
import io.github.charlietap.chasm.executor.instantiator.predecoding.LoadFactory
import io.github.charlietap.chasm.executor.instantiator.predecoding.instruction.MemArgPredecoder
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Store8Dispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instruction.FusedMemoryInstruction.I32Store8
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction

internal fun I32Store8InstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction.I32Store8,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32Store8InstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I32Store8Dispatcher,
        memArgPredecoder = ::MemArgPredecoder,
        loadFactory = ::LoadFactory,
    )

internal inline fun I32Store8InstructionPredecoder(
    context: InstantiationContext,
    instruction: FusedMemoryInstruction.I32Store8,
    crossinline dispatcher: Dispatcher<I32Store8>,
    crossinline memArgPredecoder: MemArgPredecoder,
    crossinline loadFactory: LoadFactory,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance?.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)
    val address = loadFactory(context, instruction.addressOperand)
    val value = loadFactory(context, instruction.valueOperand)

    dispatcher(I32Store8(value, address, memory, memArg))
}
