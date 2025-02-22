package io.github.charlietap.chasm.predecoder.instruction.variablefused

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variablefused.GlobalSetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variablefused.LocalSetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variablefused.LocalTeeDispatcher
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instruction.FusedVariableInstruction.GlobalSet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedVariableInstruction.LocalSet
import io.github.charlietap.chasm.executor.runtime.instruction.FusedVariableInstruction.LocalTee
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.predecoder.LoadFactory
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.globalAddress

internal fun FusedVariableInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedVariableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> =
    FusedVariableInstructionPredecoder(
        context = context,
        instruction = instruction,
        loadFactory = ::LoadFactory,
        globalSetDispatcher = ::GlobalSetDispatcher,
        localSetDispatcher = ::LocalSetDispatcher,
        localTeeDispatcher = ::LocalTeeDispatcher,
    )

internal inline fun FusedVariableInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedVariableInstruction,
    crossinline loadFactory: LoadFactory,
    crossinline globalSetDispatcher: Dispatcher<GlobalSet>,
    crossinline localSetDispatcher: Dispatcher<LocalSet>,
    crossinline localTeeDispatcher: Dispatcher<LocalTee>,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedVariableInstruction.GlobalSet -> {
            val address = context.instance.globalAddress(instruction.globalIdx)?.bind()
                ?: Err(InstantiationError.PredecodingError).bind()
            val global = context.store.global(address)
            val operand = loadFactory(context, instruction.operand)

            globalSetDispatcher(GlobalSet(operand, global))
        }
        is FusedVariableInstruction.LocalSet -> {
            val operand = loadFactory(context, instruction.operand)
            localSetDispatcher(LocalSet(operand, instruction.localIdx.idx))
        }
        is FusedVariableInstruction.LocalTee -> {
            val operand = loadFactory(context, instruction.operand)
            localTeeDispatcher(LocalTee(operand, instruction.localIdx.idx))
        }
    }
}
