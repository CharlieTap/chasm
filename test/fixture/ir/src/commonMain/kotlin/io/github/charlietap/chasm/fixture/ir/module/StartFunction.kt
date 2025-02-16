package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.module.StartFunction

fun startFunction(
    idx: Index.FunctionIndex = functionIndex(),
) = StartFunction(
    idx = idx,
)
