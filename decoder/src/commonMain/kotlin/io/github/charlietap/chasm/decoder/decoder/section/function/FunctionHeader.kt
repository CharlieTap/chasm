package io.github.charlietap.chasm.decoder.decoder.section.function

import io.github.charlietap.chasm.ast.module.Index

internal data class FunctionHeader(
    val idx: Index.FunctionIndex,
    val typeIndex: Index.TypeIndex,
)
