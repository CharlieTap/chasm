package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun ReferenceInstructionDispatcher(
    instruction: ReferenceInstruction,
): DispatchableInstruction = when (instruction) {
    is ReferenceInstruction.RefAsNonNullS -> RefAsNonNullDispatcher(instruction)
    is ReferenceInstruction.RefCastS -> RefCastDispatcher(instruction)
    is ReferenceInstruction.RefEqSs -> RefEqDispatcher(instruction)
    is ReferenceInstruction.RefFuncS -> RefFuncDispatcher(instruction)
    is ReferenceInstruction.RefIsNullS -> RefIsNullDispatcher(instruction)
    is ReferenceInstruction.RefNullS -> RefNullDispatcher(instruction)
    is ReferenceInstruction.RefTestS -> RefTestDispatcher(instruction)
}
