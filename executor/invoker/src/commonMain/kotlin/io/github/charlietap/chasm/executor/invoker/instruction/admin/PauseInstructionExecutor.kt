package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun PauseInstructionExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.Pause,
) = PauseInstructionExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    garbageCollector = ::GarbageCollector,
)

internal inline fun PauseInstructionExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.Pause,
    garbageCollector: GarbageCollector,
) {
    // Prevent runs on instances where one exported function calls another
    if (cstack.instructionStack().depth() == 1) {
        garbageCollector(store, vstack)
    }
}
