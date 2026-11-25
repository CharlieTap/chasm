package io.github.charlietap.chasm.dex

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.CompilationError
import io.github.charlietap.chasm.compiler.frontend.ParallelModuleCompiler
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor

internal typealias DexFrontend = suspend (
    Module,
    ModuleTypeResolver,
    RuntimeConfig,
    ParallelTaskExecutor,
) -> Result<CirModule, CompilationError>

internal suspend fun DexFrontend(
    module: Module,
    types: ModuleTypeResolver,
    runtime: RuntimeConfig,
    executor: ParallelTaskExecutor,
): Result<CirModule, CompilationError> {
    return ParallelModuleCompiler(
        module = module,
        types = types,
        taskExecutor = executor,
        devirtualiseIndirectCalls = !runtime.stacktraces && runtime.limits.cpu !is CpuLimits.Metered && !runtime.interrupts,
        recordProvenance = runtime.stacktraces,
    )
}
