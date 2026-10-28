package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.frontend.context.CompilerContext
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.frontend.context.createCompilerContext
import io.github.charlietap.chasm.declarations.ModuleTypeResolver
import io.github.charlietap.chasm.parallel.EarliestFailure
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.WorkBatches
import io.github.charlietap.chasm.parallel.WorkCursor
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.claimEach
import io.github.charlietap.chasm.parallel.executeWorkers
import io.github.charlietap.chasm.parallel.workersForBatches

typealias ParallelModuleCompiler = suspend (Module, ModuleTypeResolver, ParallelTaskExecutor) -> Result<CirModule, CompilationError>

suspend fun ParallelModuleCompiler(
    module: Module,
    types: ModuleTypeResolver = module.typeResolver(),
    taskExecutor: ParallelTaskExecutor,
): Result<CirModule, CompilationError> = ParallelModuleCompiler(
    module = module,
    types = types,
    taskExecutor = taskExecutor,
    mode = CompilationMode.AUTO,
    availableProcessors = availableParallelProcessors(),
    policy = compilationParallelism(),
    functionCompiler = ::FunctionCompiler,
    moduleCompiler = ::ModuleCompiler,
)

internal suspend fun ParallelModuleCompiler(
    module: Module,
    types: ModuleTypeResolver,
    taskExecutor: ParallelTaskExecutor,
    mode: CompilationMode,
    availableProcessors: Int,
    policy: ParallelismPolicy,
    functionCompiler: FunctionCompiler,
    moduleCompiler: ModuleCompiler,
): Result<CirModule, CompilationError> {
    val selectedWorkers = selectCompilationWorkers(module.functions, mode, policy, availableProcessors)
    val batches = if (selectedWorkers == 1) null else batchFunctions(module.functions)
    val workerCount = if (mode == CompilationMode.AUTO && batches != null) {
        workersForBatches(selectedWorkers, batches.count, policy)
    } else {
        selectedWorkers
    }
    if (workerCount <= 1) return moduleCompiler(module, types)

    val context = createCompilerContext(module, types)
    val compilation = ParallelCompilation(module.functions, checkNotNull(batches))
    taskExecutor.executeWorkers(workerCount) { compileFunctions(context, compilation, functionCompiler) }
    return when (val failure = compilation.failures.error) {
        null -> {
            @Suppress("UNCHECKED_CAST")
            Ok(CirModule(functions = (compilation.compiled as Array<CirFunction>).asList()))
        }
        is FunctionFailure.Returned -> Err(failure.error)
        is FunctionFailure.Thrown -> throw failure.exception
    }
}

private class ParallelCompilation(
    val functions: List<Function>,
    val batches: WorkBatches,
) {
    val cursor = WorkCursor(batches.count)
    val compiled = arrayOfNulls<CirFunction>(functions.size)
    val failures = EarliestFailure<FunctionFailure>()
}

private sealed interface FunctionFailure {
    class Returned(
        val error: CompilationError,
    ) : FunctionFailure

    class Thrown(
        val exception: Throwable,
    ) : FunctionFailure
}

private fun ParallelTaskScope.compileFunctions(
    context: CompilerContext,
    compilation: ParallelCompilation,
    functionCompiler: FunctionCompiler,
) {
    val workspace = FunctionCompilerWorkspace()

    claimEach(compilation.cursor) { batch ->
        for (functionIndex in compilation.batches.start(batch) until compilation.batches.end(batch)) {
            if (compilation.failures.isBeyond(functionIndex)) continue

            try {
                functionCompiler(context, compilation.functions[functionIndex], workspace).fold(
                    success = { compiled -> compilation.compiled[functionIndex] = compiled },
                    failure = { error -> compilation.failures.report(functionIndex, FunctionFailure.Returned(error)) },
                )
            } catch (exception: Throwable) {
                compilation.failures.report(functionIndex, FunctionFailure.Thrown(exception))
            }
        }
    }
}
