package io.github.charlietap.chasm.predecoder.instruction.referencefused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefAsNonNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefCastDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefEqDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefFuncDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefIsNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefNullDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.RefTestDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.functionAddress
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction as RuntimeFusedReferenceInstruction

internal fun FusedReferenceInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedReferenceInstruction.RefEq -> strictRefEqInstruction(instruction)
        is FusedReferenceInstruction.RefIsNull -> strictRefIsNullInstruction(instruction)
        is FusedReferenceInstruction.RefAsNonNull -> strictRefAsNonNullInstruction(instruction)
        is FusedReferenceInstruction.RefFunc -> strictRefFuncInstruction(context, instruction).bind()
        is FusedReferenceInstruction.RefNull -> strictRefNullInstruction(instruction)
        is FusedReferenceInstruction.RefTest -> strictRefTestInstruction(context, instruction)
        is FusedReferenceInstruction.RefCast -> strictRefCastInstruction(context, instruction)
    }
}

private fun strictRefEqInstruction(
    instruction: FusedReferenceInstruction.RefEq,
): DispatchableInstruction {
    val reference1Slot = strictReferenceOperandSlot(instruction.reference1)
    val reference2Slot = strictReferenceOperandSlot(instruction.reference2)
    val destinationSlot = strictReferenceDestinationSlot(instruction.destination)

    return when {
        reference1Slot != null && reference2Slot != null && destinationSlot != null -> {
            RefEqDispatcher(RuntimeFusedReferenceInstruction.RefEqSs(reference1Slot, reference2Slot, destinationSlot))
        }

        else -> unsupportedUnloweredReferenceInstruction()
    }
}

private fun strictRefIsNullInstruction(
    instruction: FusedReferenceInstruction.RefIsNull,
): DispatchableInstruction {
    val valueSlot = strictReferenceOperandSlot(instruction.value)
    val destinationSlot = strictReferenceDestinationSlot(instruction.destination)

    return when {
        valueSlot != null && destinationSlot != null -> {
            RefIsNullDispatcher(RuntimeFusedReferenceInstruction.RefIsNullS(valueSlot, destinationSlot))
        }

        else -> unsupportedUnloweredReferenceInstruction()
    }
}

private fun strictRefAsNonNullInstruction(
    instruction: FusedReferenceInstruction.RefAsNonNull,
): DispatchableInstruction {
    val valueSlot = strictReferenceOperandSlot(instruction.value)
    val destinationSlot = strictReferenceDestinationSlot(instruction.destination)

    return when {
        valueSlot != null && destinationSlot != null -> {
            RefAsNonNullDispatcher(RuntimeFusedReferenceInstruction.RefAsNonNullS(valueSlot, destinationSlot))
        }

        else -> unsupportedUnloweredReferenceInstruction()
    }
}

private fun strictRefNullInstruction(
    instruction: FusedReferenceInstruction.RefNull,
): DispatchableInstruction {
    val destinationSlot = strictReferenceDestinationSlot(instruction.destination) ?: return unsupportedUnloweredReferenceInstruction()
    val reference = ReferenceValue.Null(instruction.type).toLong()
    return RefNullDispatcher(RuntimeFusedReferenceInstruction.RefNullS(reference, destinationSlot))
}

private fun strictRefFuncInstruction(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction.RefFunc,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val destinationSlot = strictReferenceDestinationSlot(instruction.destination) ?: return@binding unsupportedUnloweredReferenceInstruction()
    val address = context.instance.functionAddress(instruction.funcIdx).bind()
    val reference = ReferenceValue.Function(address).toLong()
    RefFuncDispatcher(RuntimeFusedReferenceInstruction.RefFuncS(reference, destinationSlot))
}

private fun strictRefTestInstruction(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction.RefTest,
): DispatchableInstruction {
    val referenceSlot = strictReferenceOperandSlot(instruction.reference)
    val destinationSlot = strictReferenceDestinationSlot(instruction.destination)

    preResolveSupertypes(context, instruction.referenceType)

    return when {
        referenceSlot != null && destinationSlot != null -> {
            RefTestDispatcher(RuntimeFusedReferenceInstruction.RefTestS(referenceSlot, destinationSlot, instruction.referenceType))
        }

        else -> unsupportedUnloweredReferenceInstruction()
    }
}

private fun strictRefCastInstruction(
    context: PredecodingContext,
    instruction: FusedReferenceInstruction.RefCast,
): DispatchableInstruction {
    val referenceSlot = strictReferenceOperandSlot(instruction.reference)
    val destinationSlot = strictReferenceDestinationSlot(instruction.destination)

    preResolveSupertypes(context, instruction.referenceType)

    return when {
        referenceSlot != null && destinationSlot != null -> {
            RefCastDispatcher(RuntimeFusedReferenceInstruction.RefCastS(referenceSlot, destinationSlot, instruction.referenceType))
        }

        else -> unsupportedUnloweredReferenceInstruction()
    }
}

private fun preResolveSupertypes(
    context: PredecodingContext,
    referenceType: io.github.charlietap.chasm.type.ReferenceType,
) {
    when (val heapType = referenceType.heapType) {
        is ConcreteHeapType.TypeIndex -> context.instance.runtimeTypes[heapType.index].hydrate()
        else -> Unit
    }
}

private fun strictReferenceOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictReferenceDestinationSlot(
    destination: FusedDestination,
): Int? = when (destination) {
    is FusedDestination.FrameSlot -> destination.offset
    is FusedDestination.LocalSet -> destination.index.idx
    else -> null
}

private fun unsupportedUnloweredReferenceInstruction(): DispatchableInstruction =
    error("reference fused instruction must be frame-slot lowered to frame-slot operands and destinations before predecode")
