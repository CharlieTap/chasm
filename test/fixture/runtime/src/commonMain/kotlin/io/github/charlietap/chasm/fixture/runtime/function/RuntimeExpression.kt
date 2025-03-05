package io.github.charlietap.chasm.fixture.runtime.function

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.function.Expression

fun runtimeExpression(
    instructions: Array<DispatchableInstruction> = emptyArray(),
) = Expression(
    instructions = instructions,
)
