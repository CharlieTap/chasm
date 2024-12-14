package io.github.charlietap.chasm.fixture.executor.runtime.function

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.function.Expression

fun runtimeExpression(
    instructions: List<DispatchableInstruction> = emptyList(),
) = Expression(
    instructions = instructions,
)
