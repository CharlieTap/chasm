package io.github.charlietap.chasm.fixture.executor.instantiator

import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction

fun constantExpression(
    vararg instructions: ConstantInstruction,
) = ConstantExpression(instructions.toList())
