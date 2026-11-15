package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.analysis.FunctionSummary
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces
import io.github.charlietap.chasm.compiler.backend.analysis.analyzeModule
import io.github.charlietap.chasm.compiler.backend.analysis.functionSummary
import io.github.charlietap.chasm.compiler.backend.dex.binding.boundResources
import io.github.charlietap.chasm.parallel.EarliestFailure
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.WorkCursor
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.claimEach
import io.github.charlietap.chasm.parallel.selectWorkerCount
import io.github.charlietap.chasm.parallel.workBatches
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.DexterException
import kotlin.coroutines.cancellation.CancellationException

typealias ParallelModuleLowerer = suspend (Module, CirModule, DexOptions, ParallelTaskExecutor) -> DexModule

suspend fun ParallelModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions,
    taskExecutor: ParallelTaskExecutor,
): DexModule {
    return ParallelModuleLowerer(
        module = module,
        code = code,
        options = options,
        taskExecutor = taskExecutor,
        workers = { work, largest, items -> selectWorkerCount(work, largest, items, loweringParallelism(), availableParallelProcessors()) },
        claimOrder = ::largestBatchesFirst,
        functionLowerer = ::FunctionLowerer,
        entryLowerer = ::EntryLowerer,
        installLowerer = ::InstallLowerer,
    )
}

internal typealias WorkerSelection = (work: Long, largest: Long, items: Int) -> Int

internal suspend fun ParallelModuleLowerer(
    module: Module,
    code: CirModule,
    options: DexOptions,
    taskExecutor: ParallelTaskExecutor,
    workers: WorkerSelection,
    claimOrder: (ModulePlan) -> IntArray,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val declarations = ModuleIndexSpaces(module)
    val summaries = summarizeFunctions(code, declarations, taskExecutor, workers)
    val plan = ModulePlan(module, code, options, declarations, analyzeModule(module, code, declarations, summaries))
    val lowering = ParallelLowering(plan, claimOrder(plan))
    val tasks = List<ParallelTaskScope.() -> ModuleLowering>(loweringWorkers(plan, workers)) { { lowerBatches(lowering, functionLowerer) } }
    val lowerings = taskExecutor.execute(tasks)

    when (val failure = lowering.failures.error) {
        null -> Unit
        is DexterException -> throw DexTooLarge(failure.message ?: failure.toString(), failure)
        else -> throw failure
    }

    return try {
        val bound = boundResources(lowerings.map(ModuleLowering::usedObjects), lowerings.map(ModuleLowering::usedValues))

        @Suppress("UNCHECKED_CAST")
        val dex = (lowering.dex as Array<ByteArray>).asList()
        finishModule(plan, Dexter(), dex, lowering.lowered, lowering.chunkStarts, bound, entryLowerer, installLowerer)
    } catch (tooLarge: DexterException) {
        throw DexTooLarge(tooLarge.message ?: tooLarge.toString(), tooLarge)
    }
}

private class ParallelLowering(
    val plan: ModulePlan,
    val order: IntArray,
) {
    val cursor = WorkCursor(order.size)
    val dex = arrayOfNulls<ByteArray>(plan.batches.size)
    val lowered = LoweredFunctions(plan.functionCount)
    val chunkStarts = arrayOfNulls<IntArray>(plan.functionCount)
    val failures = EarliestFailure<Throwable>()
}

private fun ParallelTaskScope.lowerBatches(
    lowering: ParallelLowering,
    functionLowerer: FunctionLowerer,
): ModuleLowering {

    val worker = ModuleLowering(lowering.plan, lowering.chunkStarts)
    val dexter = Dexter()

    claimEach(lowering.cursor) { claimed ->
        val batch = lowering.order[claimed]
        if (lowering.failures.isBeyond(batch)) return@claimEach
        val file = dexter.beginFile()
        try {
            lowerBatch(worker, batch, file, lowering.lowered, functionLowerer)
            lowering.dex[batch] = file.finish()
        } catch (cancelled: CancellationException) {
            file.abort()
            throw cancelled
        } catch (failure: Throwable) {
            file.abort()
            lowering.failures.report(batch, failure)
        }
    }

    return worker
}

internal fun largestBatchesFirst(plan: ModulePlan): IntArray {
    val work = IntArray(plan.batches.size) { batch -> batchWork(plan, batch) }
    return plan.batches.indices.sortedByDescending { batch -> work[batch] }.toIntArray()
}

private fun loweringWorkers(
    plan: ModulePlan,
    workers: WorkerSelection,
): Int {

    var total = 0L
    var largest = 0L
    for (batch in plan.batches.indices) {
        val work = batchWork(plan, batch).toLong()
        total += work
        largest = maxOf(largest, work)
    }

    return workers(total, largest, plan.batches.size)
}

private suspend fun summarizeFunctions(
    code: CirModule,
    declarations: ModuleIndexSpaces,
    taskExecutor: ParallelTaskExecutor,
    workers: WorkerSelection,
): List<FunctionSummary> {

    val functions = code.functions
    var total = 0L
    var largest = 0L
    for (function in functions) {
        val work = (function.instructions.size + FUNCTION_WORK).toLong()
        total += work
        largest = maxOf(largest, work)
    }

    val summaries = arrayOfNulls<FunctionSummary>(functions.size)
    val batches = workBatches(functions.size, SUMMARY_BATCH_WORK) { function -> functions[function].instructions.size + FUNCTION_WORK }
    val cursor = WorkCursor(batches.count)
    val tasks = List<ParallelTaskScope.() -> Unit>(workers(total, largest, batches.count)) {
        {
            claimEach(cursor) { batch ->
                for (function in batches.start(batch) until batches.end(batch)) {
                    summaries[function] = functionSummary(functions[function], declarations)
                }
            }
        }
    }
    taskExecutor.execute(tasks)

    @Suppress("UNCHECKED_CAST")
    return (summaries as Array<FunctionSummary>).asList()
}

private fun batchWork(
    plan: ModulePlan,
    batch: Int,
): Int {
    return plan.batches[batch].sumOf { function -> function.instructions.size + FUNCTION_WORK }
}

private const val FUNCTION_WORK = 64

private const val SUMMARY_BATCH_WORK = 16_000
