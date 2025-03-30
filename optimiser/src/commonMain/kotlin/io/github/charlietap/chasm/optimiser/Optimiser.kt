package io.github.charlietap.chasm.optimiser

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.ControlFlowPass
import io.github.charlietap.chasm.optimiser.passes.FusionPass
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
    )

internal inline fun Optimiser(
    config: RuntimeConfig,
    module: Module,
    noinline control: Pass,
    noinline fusion: Pass,
): Module {
    val context = PassContext(module)
    val passes = buildList {
        if (config.bytecodeFusion) {
            add(fusion)
        }
        add(control)
    }

    return passes.fold(module) { module, pass ->
        pass(context, module)
    }
}
