package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.compiler.passes.ControlFlowPass
import io.github.charlietap.chasm.compiler.passes.FusionPass
import io.github.charlietap.chasm.compiler.passes.GCPass
import io.github.charlietap.chasm.compiler.passes.Pass
import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.ir.module.Module

/** Public API entry point for the compiler pipeline. */
typealias Compiler = (RuntimeConfig, Module) -> Module

/** Temporary alias for consumers still referring to Optimiser. */
typealias Optimiser = Compiler

fun Compiler(
    config: RuntimeConfig,
    module: Module,
): Module =
    Compiler(
        config = config,
        module = module,
        control = ::ControlFlowPass,
        fusion = ::FusionPass,
        gc = ::GCPass,
    )

internal inline fun Compiler(
    config: RuntimeConfig,
    module: Module,
    noinline control: Pass,
    noinline fusion: Pass,
    noinline gc: Pass,
): Module {
    val context = PassContext(config, module)
    val passes = buildList {
        add(control)
        if (config.bytecodeFusion) add(fusion)
        if (config.gcStrategy != GCStrategy.MANUAL) add(gc)
    }

    return passes.fold(module) { acc, pass -> pass(context, acc) }
}
