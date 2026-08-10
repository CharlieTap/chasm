package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceSuperInstruction

fun ReferenceSuperInstructionDispatcher(
    instruction: ReferenceSuperInstruction,
): DispatchableInstruction = when (instruction) {
    is ReferenceSuperInstruction.RefAsNonNullS -> RefAsNonNullDispatcher(instruction)
    is ReferenceSuperInstruction.RefCastS -> RefCastDispatcher(instruction)
    is ReferenceSuperInstruction.RefEqSs -> RefEqDispatcher(instruction)
    is ReferenceSuperInstruction.RefFuncS -> RefFuncDispatcher(instruction)
    is ReferenceSuperInstruction.RefIsNullS -> RefIsNullDispatcher(instruction)
    is ReferenceSuperInstruction.RefNullS -> RefNullDispatcher(instruction)
    is ReferenceSuperInstruction.RefTestS -> RefTestDispatcher(instruction)
}
