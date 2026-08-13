package io.github.charlietap.chasm.compiler

import io.github.charlietap.chasm.ast.module.Function

internal fun CompilationPlanner(
    functions: List<Function>,
    mode: CompilationMode,
    availableProcessors: Int = availableCompilerProcessors(),
): CompilationPlan {
    val workerLimit = minOf(
        functions.size,
        maxOf(availableProcessors - 1, 1),
        COMPILER_WORKER_LIMIT,
    )
    if (mode == CompilationMode.SERIAL || workerLimit <= 1) return CompilationPlan.Serial

    var serialCost = 0L
    val scheduledFunctions = Array(functions.size) { functionIndex ->
        val function = functions[functionIndex]
        val scheduledFunction = ScheduledFunction(
            functionIndex = functionIndex,
            estimatedCost = function.body.instructions.size.toLong() + FUNCTION_COMPILATION_COST,
        )
        serialCost += scheduledFunction.estimatedCost
        scheduledFunction
    }

    if (mode == CompilationMode.PARALLEL) {
        scheduledFunctions.sortByEstimatedCost()
        val assignments = scheduleFunctions(scheduledFunctions, workerLimit)
        return CompilationPlan.Parallel(Array(assignments.size) { index -> assignments[index].toIntArray() })
    }

    if (!couldBenefitFromParallelism(serialCost, workerLimit)) return CompilationPlan.Serial
    scheduledFunctions.sortByEstimatedCost()

    var bestCost = serialCost
    var bestAssignments: Array<FunctionAssignment>? = null
    for (workerCount in 2..workerLimit) {
        val assignments = scheduleFunctions(scheduledFunctions, workerCount)
        val parallelCost = assignments.maxOf(FunctionAssignment::estimatedCost) +
            PARALLEL_COMPILATION_COST +
            workerCount * COMPILER_WORKER_COST
        if (parallelCost < bestCost) {
            bestCost = parallelCost
            bestAssignments = assignments
        }
    }

    val assignments = bestAssignments ?: return CompilationPlan.Serial
    return if (bestCost * COST_SCALE <= serialCost * MINIMUM_PARALLEL_COST_PERCENT) {
        CompilationPlan.Parallel(Array(assignments.size) { index -> assignments[index].toIntArray() })
    } else {
        CompilationPlan.Serial
    }
}

internal enum class CompilationMode {
    AUTO,
    SERIAL,
    PARALLEL,
}

private fun couldBenefitFromParallelism(
    serialCost: Long,
    workerLimit: Int,
): Boolean {
    for (workerCount in 2..workerLimit) {
        val minimumCriticalPath = (serialCost + workerCount - 1) / workerCount
        val minimumParallelCost = minimumCriticalPath +
            PARALLEL_COMPILATION_COST +
            workerCount * COMPILER_WORKER_COST
        if (minimumParallelCost * COST_SCALE <= serialCost * MINIMUM_PARALLEL_COST_PERCENT) return true
    }
    return false
}

private fun Array<ScheduledFunction>.sortByEstimatedCost() {
    sortWith(
        compareByDescending<ScheduledFunction>(ScheduledFunction::estimatedCost)
            .thenBy(ScheduledFunction::functionIndex),
    )
}

internal sealed interface CompilationPlan {
    data object Serial : CompilationPlan

    class Parallel(val assignments: Array<IntArray>) : CompilationPlan
}

private fun scheduleFunctions(
    functions: Array<ScheduledFunction>,
    workerCount: Int,
): Array<FunctionAssignment> {
    val initialCapacity = maxOf((functions.size + workerCount - 1) / workerCount, 1)
    val assignments = Array(workerCount) { FunctionAssignment(initialCapacity) }
    for (index in functions.indices) {
        var lightestWorker = 0
        for (workerIndex in 1 until assignments.size) {
            if (assignments[workerIndex].estimatedCost < assignments[lightestWorker].estimatedCost) {
                lightestWorker = workerIndex
            }
        }
        assignments[lightestWorker].add(functions[index])
    }
    return assignments
}

private class FunctionAssignment(initialCapacity: Int) {

    private var functionIndices = IntArray(initialCapacity)
    private var size = 0
    var estimatedCost = 0L
        private set

    fun add(function: ScheduledFunction) {
        if (size == functionIndices.size) functionIndices = functionIndices.copyOf(functionIndices.size * 2)
        functionIndices[size++] = function.functionIndex
        estimatedCost += function.estimatedCost
    }

    fun toIntArray(): IntArray = functionIndices.copyOf(size)
}

private class ScheduledFunction(
    val functionIndex: Int,
    val estimatedCost: Long,
)

private const val FUNCTION_COMPILATION_COST = 64L
private const val PARALLEL_COMPILATION_COST = 2_048L
private const val COMPILER_WORKER_COST = 128L
private const val MINIMUM_PARALLEL_COST_PERCENT = 85L
private const val COST_SCALE = 100L
private const val COMPILER_WORKER_LIMIT = 6
