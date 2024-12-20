package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.GlobalSetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.LocalGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.LocalSetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variable.LocalTeeDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.GlobalGet
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.GlobalSet
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.LocalGet
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.LocalSet
import io.github.charlietap.chasm.executor.runtime.instruction.VariableInstruction.LocalTee

internal fun VariableInstructionPredecoder(
    context: InstantiationContext,
    instruction: VariableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    VariableInstructionPredecoder(
        context = context,
        instruction = instruction,
        localGetDispatcher = ::LocalGetDispatcher,
        localSetDispatcher = ::LocalSetDispatcher,
        localTeeDispatcher = ::LocalTeeDispatcher,
        globalGetDispatcher = ::GlobalGetDispatcher,
        globalSetDispatcher = ::GlobalSetDispatcher,
    )

internal inline fun VariableInstructionPredecoder(
    context: InstantiationContext,
    instruction: VariableInstruction,
    crossinline localGetDispatcher: Dispatcher<LocalGet>,
    crossinline localSetDispatcher: Dispatcher<LocalSet>,
    crossinline localTeeDispatcher: Dispatcher<LocalTee>,
    crossinline globalGetDispatcher: Dispatcher<GlobalGet>,
    crossinline globalSetDispatcher: Dispatcher<GlobalSet>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is VariableInstruction.LocalGet -> localGetDispatcher(LocalGet(instruction.localIdx.idx.toInt()))
        is VariableInstruction.LocalSet -> localSetDispatcher(LocalSet(instruction.localIdx.idx.toInt()))
        is VariableInstruction.LocalTee -> localTeeDispatcher(LocalTee(instruction.localIdx.idx.toInt()))
        is VariableInstruction.GlobalGet -> globalGetDispatcher(GlobalGet(instruction.globalIdx))
        is VariableInstruction.GlobalSet -> globalSetDispatcher(GlobalSet(instruction.globalIdx))
    }
}
