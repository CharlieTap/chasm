package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.gc.ArenaFunctionCompiler
import io.github.charlietap.chasm.optimiser.passes.gc.TraditionalFunctionCompiler

internal fun GCPass(
    context: PassContext,
    module: Module,
): Module = GCPass(
    context = context,
    module = module,
    arenaCompiler = ::ArenaFunctionCompiler,
    traditionalCompiler = ::TraditionalFunctionCompiler,
)

internal inline fun GCPass(
    context: PassContext,
    module: Module,
    arenaCompiler: ArenaFunctionCompiler,
    traditionalCompiler: TraditionalFunctionCompiler,
): Module = when (context.config.gcStrategy) {
    GCStrategy.ARENA -> arenaCompiler(context, module)
    GCStrategy.TRADITIONAL -> traditionalCompiler(context, module)
    GCStrategy.MANUAL -> module
}
