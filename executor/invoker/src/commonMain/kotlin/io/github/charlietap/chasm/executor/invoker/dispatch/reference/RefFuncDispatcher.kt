package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefFuncExecutor
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefFuncDispatcher(
    instruction: ReferenceInstruction.RefFunc,
): DispatchableInstruction = DispatchableInstruction { vstack, context, nextIp ->
    RefFuncExecutor(vstack, context, instruction)
    nextIp
}
