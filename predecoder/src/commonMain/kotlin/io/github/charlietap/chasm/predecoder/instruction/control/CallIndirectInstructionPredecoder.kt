package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.CallIndirectDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.CallIndirect

internal fun CallIndirectInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.CallIndirect,
): Result<DispatchableInstruction, ModuleTrapError> =
    CallIndirectInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::CallIndirectDispatcher,
    )

internal inline fun CallIndirectInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.CallIndirect,
    crossinline dispatcher: Dispatcher<CallIndirect>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(address)

    dispatcher(
        CallIndirect(
            type = context.types[instruction.typeIndex.idx],
            table = table,
        ),
    )
} 
