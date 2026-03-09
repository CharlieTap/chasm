package io.github.charlietap.chasm.predecoder.instruction.variablefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.variablefused.GlobalGetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variablefused.GlobalSetDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.variablefused.LocalSetDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedVariableInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.globalAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instruction.FusedVariableInstruction as RuntimeFusedVariableInstruction

internal fun FusedVariableInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedVariableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedVariableInstruction.GlobalGet -> FusedGlobalGetInstructionPredecoder(context, instruction).bind()
        is FusedVariableInstruction.GlobalSet -> FusedGlobalSetInstructionPredecoder(context, instruction).bind()
        is FusedVariableInstruction.LocalSet -> FusedLocalSetInstructionPredecoder(context, instruction)
        is FusedVariableInstruction.LocalTee -> unsupportedUnloweredVariableInstruction()
    }
}

private fun FusedGlobalGetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedVariableInstruction.GlobalGet,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val global = resolveGlobal(context, instruction.globalIdx).bind()
    val destinationSlot = strictVariableDestinationSlot(context, instruction.destination)
        ?: return@binding unsupportedUnloweredVariableInstruction()
    GlobalGetDispatcher(RuntimeFusedVariableInstruction.GlobalGetS(global, destinationSlot))
}

private fun FusedGlobalSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedVariableInstruction.GlobalSet,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val global = resolveGlobal(context, instruction.globalIdx).bind()
    strictGlobalSetInstruction(context, instruction.operand, global)
}

private fun FusedLocalSetInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedVariableInstruction.LocalSet,
): DispatchableInstruction {
    val localSlot = context.localSlot(instruction.localIdx.idx)
    val immediate = strictVariableImmediate(instruction.operand)
    val sourceSlot = strictVariableOperandSlot(context, instruction.operand)

    return when {
        immediate != null -> LocalSetDispatcher(RuntimeFusedVariableInstruction.LocalSetI(immediate, localSlot))
        sourceSlot != null -> LocalSetDispatcher(RuntimeFusedVariableInstruction.LocalSetS(sourceSlot, localSlot))
        else -> unsupportedUnloweredVariableInstruction()
    }
}

private fun strictGlobalSetInstruction(
    context: PredecodingContext,
    operand: FusedOperand,
    global: GlobalInstance,
): DispatchableInstruction {
    val immediate = strictVariableImmediate(operand)
    val sourceSlot = strictVariableOperandSlot(context, operand)

    return when {
        immediate != null -> GlobalSetDispatcher(RuntimeFusedVariableInstruction.GlobalSetI(immediate, global))
        sourceSlot != null -> GlobalSetDispatcher(RuntimeFusedVariableInstruction.GlobalSetS(sourceSlot, global))
        else -> unsupportedUnloweredVariableInstruction()
    }
}

private fun strictVariableImmediate(
    operand: FusedOperand,
): Long? = when (operand) {
    is FusedOperand.I32Const -> operand.const.toLong()
    is FusedOperand.I64Const -> operand.const
    is FusedOperand.F32Const -> operand.const.toRawBits().toLong()
    is FusedOperand.F64Const -> operand.const.toRawBits()
    else -> null
}

private fun strictVariableOperandSlot(
    context: PredecodingContext,
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> context.localSlot(operand.index.idx)
    else -> null
}

private fun strictVariableDestinationSlot(
    context: PredecodingContext,
    destination: FusedDestination,
): Int? = when (destination) {
    is FusedDestination.FrameSlot -> destination.offset
    is FusedDestination.LocalSet -> context.localSlot(destination.index.idx)
    else -> null
}

private fun resolveGlobal(
    context: PredecodingContext,
    globalIdx: Index.GlobalIndex,
): Result<GlobalInstance, ModuleTrapError> = binding {
    val address = context.instance.globalAddress(globalIdx).bind()
    context.store.global(address)
}

private fun unsupportedUnloweredVariableInstruction(): DispatchableInstruction =
    error("variable fused instruction must be frame-slot lowered to immediate/frame-slot shapes before predecode")
