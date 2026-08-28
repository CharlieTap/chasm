package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun RefCastExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
) = RefCastExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun RefCastExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
    crossinline caster: Caster,
) {
    val referenceValue = vstack.pop()
    val casted = caster(referenceValue, instruction.typeTest, context)

    if (casted) {
        vstack.push(referenceValue)
    } else {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
}
