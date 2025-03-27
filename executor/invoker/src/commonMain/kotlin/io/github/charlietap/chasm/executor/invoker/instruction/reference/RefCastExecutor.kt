package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store

internal fun RefCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
) = RefCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun RefCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
    crossinline caster: Caster,
) {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceValue = vstack.pop()
    val casted = caster(referenceValue, instruction.referenceType, moduleInstance, store)

    if (casted) {
        vstack.push(referenceValue)
    } else {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
}
