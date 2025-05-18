package io.github.charlietap.chasm.optimiser

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.ControlFlowPass
import io.github.charlietap.chasm.optimiser.passes.FusionPass
import io.github.charlietap.chasm.optimiser.passes.GCPass
import io.github.charlietap.chasm.optimiser.passes.Pass
import io.github.charlietap.chasm.optimiser.passes.PassContext

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
        gc = ::GCPass,
    )

internal inline fun Optimiser(
    config: RuntimeConfig,
    module: Module,
    noinline control: Pass,
    noinline fusion: Pass,
    noinline gc: Pass,
): Module {

    val context = PassContext(config, module)
    val passes = buildList {
        add(control)
        if (config.bytecodeFusion) {
            add(fusion)
        }
        if (config.gcStrategy != GCStrategy.MANUAL) {
            add(gc)
        }
    }

    return passes.fold(module) { module, pass ->
        pass(context, module)
    }
}
