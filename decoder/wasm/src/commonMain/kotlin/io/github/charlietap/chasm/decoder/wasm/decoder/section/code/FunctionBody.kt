package io.github.charlietap.chasm.decoder.wasm.decoder.section.code

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local

internal data class FunctionBody(
    val idx: Index.FunctionIndex,
    val locals: List<Local>,
    val body: Expression,
)
