package io.github.charlietap.chasm.decoder.section.function

import io.github.charlietap.chasm.ast.instruction.Index

data class FunctionHeader(
    val idx: Index.FunctionIndex,
    val typeIndex: Index.TypeIndex,
)
