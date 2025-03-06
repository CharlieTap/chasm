package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnCallIndirectDispatcher
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.tableAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction.ReturnCallIndirect

internal fun ReturnCallIndirectInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnCallIndirect,
): Result<DispatchableInstruction, ModuleTrapError> =
    ReturnCallIndirectInstructionPredecoder(
        context = context,
        instruction = instruction,
        dispatcher = ::ReturnCallIndirectDispatcher,
    )

internal inline fun ReturnCallIndirectInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.ReturnCallIndirect,
    crossinline dispatcher: Dispatcher<ReturnCallIndirect>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val address = context.instance.tableAddress(instruction.tableIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    val table = context.store.table(address)

    dispatcher(
        ReturnCallIndirect(
            type = context.types[instruction.typeIndex.idx],
            table = table,
        ),
    )
} 
