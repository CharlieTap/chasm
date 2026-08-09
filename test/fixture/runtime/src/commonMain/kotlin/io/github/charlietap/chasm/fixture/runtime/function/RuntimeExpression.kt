package io.github.charlietap.chasm.fixture.runtime.function

import io.github.charlietap.chasm.runtime.function.Expression
import io.github.charlietap.chasm.runtime.program.EXIT_IP

fun runtimeExpression(
    entryIp: Int = EXIT_IP,
) = Expression(entryIp)
