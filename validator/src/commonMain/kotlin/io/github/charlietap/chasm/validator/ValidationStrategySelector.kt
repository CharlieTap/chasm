package io.github.charlietap.chasm.validator

import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.parallel.availableParallelProcessors

internal fun selectValidationStrategy(
    functions: List<Function>,
    mode: ValidationMode,
    availableProcessors: Int = availableParallelProcessors(),
): ValidationStrategy {
    val workerLimit = minOf(
        functions.size,
        maxOf(availableProcessors - 1, 1),
        VALIDATOR_WORKER_LIMIT,
    )
    if (mode == ValidationMode.SERIAL || workerLimit <= 1) return ValidationStrategy.Serial

    val functionCosts = Array(functions.size) { functionIndex ->
        val function = functions[functionIndex]
        FunctionCost(
            functionIndex = functionIndex,
            estimatedCost = function.body.instructions.size.toLong() +
                function.locals.size +
                FUNCTION_VALIDATION_COST,
        )
    }
    val serialCost = functionCosts.sumOf(FunctionCost::estimatedCost)

    if (mode == ValidationMode.AUTO) {
        val minimumParallelCost = estimateMinimumParallelCost(serialCost, workerLimit)
        if (!shouldParallelize(serialCost, minimumParallelCost)) return ValidationStrategy.Serial
    }
    functionCosts.sortByEstimatedCost()

    return if (mode == ValidationMode.PARALLEL) {
        ValidationStrategy.Parallel(assignFunctions(functionCosts, workerLimit).toFunctionIndices())
    } else {
        selectAutomaticValidationStrategy(functionCosts, serialCost, workerLimit)
    }
}

private fun selectAutomaticValidationStrategy(
    functionCosts: Array<FunctionCost>,
    serialCost: Long,
    workerLimit: Int,
): ValidationStrategy {
    var bestAssignments = assignFunctions(functionCosts, 2)
    var lowestParallelCost = estimateParallelCost(bestAssignments)
    for (workerCount in 3..workerLimit) {
        val assignments = assignFunctions(functionCosts, workerCount)
        val parallelCost = estimateParallelCost(assignments)
        if (parallelCost < lowestParallelCost) {
            lowestParallelCost = parallelCost
            bestAssignments = assignments
        }
    }

    return if (shouldParallelize(serialCost, lowestParallelCost)) {
        ValidationStrategy.Parallel(bestAssignments.toFunctionIndices())
    } else {
        ValidationStrategy.Serial
    }
}

internal enum class ValidationMode {
    AUTO,
    SERIAL,
    PARALLEL,
}

internal sealed interface ValidationStrategy {
    data object Serial : ValidationStrategy

    class Parallel(val assignments: Array<IntArray>) : ValidationStrategy
}

private fun estimateMinimumParallelCost(
    serialCost: Long,
    workerLimit: Int,
): Long {
    var minimumCost = Long.MAX_VALUE
    for (workerCount in 2..workerLimit) {
        val criticalPath = (serialCost + workerCount - 1) / workerCount
        val parallelCost = criticalPath + PARALLEL_VALIDATION_COST + workerCount * VALIDATOR_WORKER_COST
        if (parallelCost < minimumCost) minimumCost = parallelCost
    }
    return minimumCost
}

private fun shouldParallelize(
    serialCost: Long,
    parallelCost: Long,
): Boolean = parallelCost * COST_SCALE <= serialCost * MINIMUM_PARALLEL_COST_PERCENT

private fun estimateParallelCost(assignments: Array<WorkerAssignment>): Long =
    assignments.maxOf(WorkerAssignment::estimatedCost) +
        PARALLEL_VALIDATION_COST +
        assignments.size * VALIDATOR_WORKER_COST

private fun Array<FunctionCost>.sortByEstimatedCost() {
    sortWith(
        compareByDescending<FunctionCost>(FunctionCost::estimatedCost)
            .thenBy(FunctionCost::functionIndex),
    )
}

private fun assignFunctions(
    functionCosts: Array<FunctionCost>,
    workerCount: Int,
): Array<WorkerAssignment> {
    val initialCapacity = maxOf((functionCosts.size + workerCount - 1) / workerCount, 1)
    val assignments = Array(workerCount) { WorkerAssignment(initialCapacity) }
    for (functionCost in functionCosts) {
        var lightestWorker = 0
        for (workerIndex in 1 until assignments.size) {
            if (assignments[workerIndex].estimatedCost < assignments[lightestWorker].estimatedCost) {
                lightestWorker = workerIndex
            }
        }
        assignments[lightestWorker].add(functionCost)
    }
    return assignments
}

private fun Array<WorkerAssignment>.toFunctionIndices(): Array<IntArray> =
    Array(size) { index -> this[index].toIntArray().also(IntArray::sort) }

private class WorkerAssignment(initialCapacity: Int) {

    private var functionIndices = IntArray(initialCapacity)
    private var size = 0
    var estimatedCost = 0L
        private set

    fun add(functionCost: FunctionCost) {
        if (size == functionIndices.size) functionIndices = functionIndices.copyOf(functionIndices.size * 2)
        functionIndices[size++] = functionCost.functionIndex
        estimatedCost += functionCost.estimatedCost
    }

    fun toIntArray(): IntArray = functionIndices.copyOf(size)
}

private class FunctionCost(
    val functionIndex: Int,
    val estimatedCost: Long,
)

private const val FUNCTION_VALIDATION_COST = 32L
private const val PARALLEL_VALIDATION_COST = 2_048L
private const val VALIDATOR_WORKER_COST = 3_072L
private const val MINIMUM_PARALLEL_COST_PERCENT = 85L
private const val COST_SCALE = 100L
private const val VALIDATOR_WORKER_LIMIT = 6
