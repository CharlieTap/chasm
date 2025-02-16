package io.github.charlietap.chasm.optimiser

import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.FusionPass
import io.github.charlietap.chasm.optimiser.passes.Pass

typealias Optimiser = (Module) -> Module

fun Optimiser(
    module: Module,
): Module =
    Optimiser(
        module = module,
        fusion = ::FusionPass,
    )

internal inline fun Optimiser(
    module: Module,
    fusion: Pass,
): Module {
    return fusion(module)
}
