package io.github.charlietap.chasm.validator

import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.WorkBatches
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.selectWorkerCount
import io.github.charlietap.chasm.parallel.workBatches

internal fun selectValidationWorkers(
    functions: List<Function>,
    mode: ValidationMode,
    policy: ParallelismPolicy,
    availableProcessors: Int = availableParallelProcessors(),
): Int = when (mode) {
    ValidationMode.SERIAL -> 1
    ValidationMode.PARALLEL -> minOf(functions.size, availableProcessors, policy.maximumWorkers).coerceAtLeast(1)
    ValidationMode.AUTO -> {
        var work = 0L
        var largestFunction = 0
        for (index in functions.indices) {
            val functionWork = validationWork(functions[index])
            work += functionWork
            if (functionWork > largestFunction) largestFunction = functionWork
        }
        selectWorkerCount(work, largestFunction.toLong(), functions.size, policy, availableProcessors)
    }
}

internal fun batchFunctions(functions: List<Function>): WorkBatches =
    workBatches(functions.size, BATCH_WORK) { index -> validationWork(functions[index]) }

internal enum class ValidationMode {
    AUTO,
    SERIAL,
    PARALLEL,
}

private fun validationWork(function: Function): Int = function.body.instructions.size + function.locals.size

private const val BATCH_WORK = 4_096
