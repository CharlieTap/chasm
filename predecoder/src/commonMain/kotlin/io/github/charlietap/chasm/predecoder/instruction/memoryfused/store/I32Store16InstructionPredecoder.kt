package io.github.charlietap.chasm.predecoder.instruction.memoryfused.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Store16Dispatcher
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction.I32Store16

internal fun I32Store16InstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedMemoryInstruction.I32Store16,
): Result<DispatchableInstruction, ModuleTrapError> =
    I32Store16InstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::I32Store16Dispatcher,
        memArgPredecoder = ::MemArgPredecoder,
        loadFactory = ::LoadFactory,
    )

internal inline fun I32Store16InstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedMemoryInstruction.I32Store16,
    crossinline dispatcher: Dispatcher<I32Store16>,
    crossinline memArgPredecoder: MemArgPredecoder,
    crossinline loadFactory: LoadFactory,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(instruction.memoryIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val memory = context.store.memory(memoryAddress)
    val memArg = memArgPredecoder(instruction.memArg)
    val address = loadFactory(context, instruction.addressOperand)
    val value = loadFactory(context, instruction.valueOperand)

    dispatcher(I32Store16(value, address, memory, memArg))
}
