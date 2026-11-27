package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
data class TryRegion(
    val start: Int,
    val end: Int,
    val parent: Int?,
    val catches: List<CatchClause>,
)
