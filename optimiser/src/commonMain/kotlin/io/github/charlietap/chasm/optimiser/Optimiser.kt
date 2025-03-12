package io.github.charlietap.chasm.optimiser

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.ControlFlowPass
import io.github.charlietap.chasm.optimiser.passes.FusionPass
import io.github.charlietap.chasm.optimiser.passes.Pass

typealias Optimiser = (RuntimeConfig, Module) -> Module

fun Optimiser(
    config: RuntimeConfig,
    module: Module,
): Module =
    Optimiser(
        config = config,
        module = module,
        control = ::ControlFlowPass,
        fusion = ::FusionPass,
    )

internal inline fun Optimiser(
    config: RuntimeConfig,
    module: Module,
    control: Pass,
    fusion: Pass,
): Module {
    return control(module).let { compiled ->
        if (config.bytecodeFusion) {
            fusion(compiled)
        } else {
            compiled
        }
    }
}
