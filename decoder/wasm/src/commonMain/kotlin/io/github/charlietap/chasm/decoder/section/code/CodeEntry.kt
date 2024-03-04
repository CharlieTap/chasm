package io.github.charlietap.chasm.decoder.section.code

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Local

data class CodeEntry(
    val size: UInt,
    val locals: List<Local>,
    val body: Expression,
)
