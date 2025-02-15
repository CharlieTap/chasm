package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.module.Index

fun fusedDestination(): FusedDestination = valueStackDestination()

fun valueStackDestination(): FusedDestination = FusedDestination.ValueStack

fun localSetDestination(
    index: Index.LocalIndex = localIndex(),
): FusedDestination = FusedDestination.LocalSet(
    index = index,
)

fun globalSetDestination(
    index: Index.GlobalIndex = globalIndex(),
): FusedDestination = FusedDestination.GlobalSet(
    index = index,
)
