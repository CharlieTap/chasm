package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.compiler.passes.gc.ArenaFunctionCompiler
import io.github.charlietap.chasm.compiler.passes.gc.TraditionalFunctionCompiler
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.ir.module.Module

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
