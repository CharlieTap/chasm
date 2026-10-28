package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.WorkBatches
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.selectWorkerCount
import io.github.charlietap.chasm.parallel.workBatches

internal fun selectCompilationWorkers(
    functions: List<Function>,
    mode: CompilationMode,
    policy: ParallelismPolicy,
    availableProcessors: Int = availableParallelProcessors(),
): Int = when (mode) {
    CompilationMode.SERIAL -> 1
    CompilationMode.PARALLEL -> minOf(functions.size, availableProcessors, policy.maximumWorkers).coerceAtLeast(1)
    CompilationMode.AUTO -> {
        var work = 0L
        var largestFunction = 0
        for (index in functions.indices) {
            val functionWork = compilationWork(functions[index])
            work += functionWork
            if (functionWork > largestFunction) largestFunction = functionWork
        }
        selectWorkerCount(work, largestFunction.toLong(), functions.size, policy, availableProcessors)
    }
}

internal fun batchFunctions(
    functions: List<Function>,
): WorkBatches =
    workBatches(functions.size, BATCH_WORK) { index -> compilationWork(functions[index]) }

internal enum class CompilationMode {
    AUTO,
    SERIAL,
    PARALLEL,
}

private fun compilationWork(
    function: Function,
): Int = function.body.instructions.size + FUNCTION_COMPILATION_WORK

private const val FUNCTION_COMPILATION_WORK = 64
private const val BATCH_WORK = 2_048
