package io.github.charlietap.chasm.predecoder.instruction.variable

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.LocalGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.LocalSetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.LocalTeeDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.LocalGet
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.LocalSet
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.LocalTee
import io.github.charlietap.chasm.ir.instruction.VariableInstruction
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.predecoder.PredecodingContext

internal fun VariableInstructionPredecoder(
    context: PredecodingContext,
    instruction: VariableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    VariableInstructionPredecoder(
        context = context,
        instruction = instruction,
        localGetDispatcher = ::LocalGetDispatcher,
        localSetDispatcher = ::LocalSetDispatcher,
        localTeeDispatcher = ::LocalTeeDispatcher,
        globalGetPredecoder = ::GlobalGetInstructionPredecoder,
        globalSetPredecoder = ::GlobalSetInstructionPredecoder,
    )

internal inline fun VariableInstructionPredecoder(
    context: PredecodingContext,
    instruction: VariableInstruction,
    crossinline localGetDispatcher: Dispatcher<LocalGet>,
    crossinline localSetDispatcher: Dispatcher<LocalSet>,
    crossinline localTeeDispatcher: Dispatcher<LocalTee>,
    crossinline globalGetPredecoder: Predecoder<VariableInstruction.GlobalGet, DispatchableInstruction>,
    crossinline globalSetPredecoder: Predecoder<VariableInstruction.GlobalSet, DispatchableInstruction>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is VariableInstruction.LocalGet -> localGetDispatcher(LocalGet(instruction.localIdx.idx))
        is VariableInstruction.LocalSet -> localSetDispatcher(LocalSet(instruction.localIdx.idx))
        is VariableInstruction.LocalTee -> localTeeDispatcher(LocalTee(instruction.localIdx.idx))
        is VariableInstruction.GlobalGet -> globalGetPredecoder(context, instruction).bind()
        is VariableInstruction.GlobalSet -> globalSetPredecoder(context, instruction).bind()
    }
}
