package io.github.charlietap.chasm.executor.invoker.dispatch.reference

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefAsNonNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefCastExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefEqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefFuncExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefIsNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.reference.RefTestExecutor
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction

fun RefCastDispatcher(
    instruction: ReferenceInstruction.RefCastS,
) = dispatchInstruction { vstack, context ->
    RefCastExecutor(vstack, context, instruction)
}

fun RefEqDispatcher(
    instruction: ReferenceInstruction.RefEqSs,
) = dispatchInstruction { vstack, context ->
    RefEqExecutor(vstack, context, instruction)
}

fun RefIsNullDispatcher(
    instruction: ReferenceInstruction.RefIsNullS,
) = dispatchInstruction { vstack, context ->
    RefIsNullExecutor(vstack, context, instruction)
}

fun RefAsNonNullDispatcher(
    instruction: ReferenceInstruction.RefAsNonNullS,
) = dispatchInstruction { vstack, context ->
    RefAsNonNullExecutor(vstack, context, instruction)
}

fun RefNullDispatcher(
    instruction: ReferenceInstruction.RefNullS,
) = dispatchInstruction { vstack, context ->
    RefNullExecutor(vstack, context, instruction)
}

fun RefFuncDispatcher(
    instruction: ReferenceInstruction.RefFuncS,
) = dispatchInstruction { vstack, context ->
    RefFuncExecutor(vstack, context, instruction)
}

fun RefTestDispatcher(
    instruction: ReferenceInstruction.RefTestS,
) = dispatchInstruction { vstack, context ->
    RefTestExecutor(vstack, context, instruction)
}
