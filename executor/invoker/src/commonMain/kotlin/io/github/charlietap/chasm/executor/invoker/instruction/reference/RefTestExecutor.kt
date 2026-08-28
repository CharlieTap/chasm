package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ValueStack

internal fun RefTestExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefTest,
) = RefTestExecutor(
    vstack = vstack,
    context = context,
    instruction = instruction,
    caster = ::Caster,
)

internal inline fun RefTestExecutor(
    vstack: ValueStack,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefTest,
    crossinline caster: Caster,
) {
    if (caster(vstack.pop(), instruction.typeTest, context)) {
        vstack.push(1L)
    } else {
        vstack.push(0L)
    }
}
