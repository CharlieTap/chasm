package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.wrapI31
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue

internal fun RefI31Executor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.RefI31,
): InstructionPointer =
    RefI31Executor(
        ip = ip,
        vstack = vstack,
        cstack = cstack,
        store = store,
        context = context,
        instruction = instruction,
        i31Wrapper = Int::wrapI31,
    )

internal inline fun RefI31Executor(
    ip: InstructionPointer,
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AggregateInstruction.RefI31,
    crossinline i31Wrapper: (Int) -> ReferenceValue.I31,
): InstructionPointer {
    val value = vstack.popI32()
    val i31 = i31Wrapper(value).toLong()

    vstack.push(i31)

    return ip + 1
}
