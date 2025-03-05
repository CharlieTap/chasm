package io.github.charlietap.chasm.predecoder.instruction.control

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.functionAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.instruction

internal inline fun CallInstructionPredecoder(
    context: PredecodingContext,
    instruction: ControlInstruction.Call,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    // Functions are predecoded into control instructions on instance allocation
    val address = context.instance.functionAddress(instruction.functionIndex)?.bind()
        ?: Err(InstantiationError.PredecodingError).bind()
    context.store.instruction(address)
}
