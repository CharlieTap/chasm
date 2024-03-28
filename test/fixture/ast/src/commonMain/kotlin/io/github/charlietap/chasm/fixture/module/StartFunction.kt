package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.StartFunction

fun startFunction(
    idx: Index.FunctionIndex = Index.FunctionIndex(0u),
) = StartFunction(
    idx = idx,
)
