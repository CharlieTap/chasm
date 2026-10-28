package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun ReferenceInstructionDispatcher(
    instruction: ReferenceInstruction,
): DispatchableInstruction = when (instruction) {
    is ReferenceInstruction.RefAsNonNullS -> RefAsNonNullSDispatcher(instruction.valueSlot, instruction.destinationSlot)
    is ReferenceInstruction.RefCastS -> RefCastSDispatcher(instruction.referenceSlot, instruction.destinationSlot, instruction.typeTest)
    is ReferenceInstruction.RefEqSs -> RefEqSsDispatcher(instruction.reference1Slot, instruction.reference2Slot, instruction.destinationSlot)
    is ReferenceInstruction.RefFuncS -> RefFuncSDispatcher(instruction.reference, instruction.destinationSlot)
    is ReferenceInstruction.RefIsNullS -> RefIsNullSDispatcher(instruction.valueSlot, instruction.destinationSlot)
    is ReferenceInstruction.RefNullS -> RefNullSDispatcher(instruction.reference, instruction.destinationSlot)
    is ReferenceInstruction.RefTestS -> RefTestSDispatcher(instruction.referenceSlot, instruction.destinationSlot, instruction.typeTest)
}
