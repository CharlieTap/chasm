package io.github.charlietap.chasm.compiler.backend.abi

data class TryRegion(
    val start: Int,
    val end: Int,
    val parent: Int?,
    val catches: List<CatchClause>,
)
