package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
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
import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.Dexter
import io.github.charlietap.dexter.DexterException
import io.github.charlietap.dexter.DexterFileFullException
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
        claimOrder = ::largestFilesFirst,
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
    claimOrder: (ModulePlan, DexLayout) -> IntArray,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val declarations = ModuleIndexSpaces(module)
    val summaries = summarizeFunctions(code, declarations, taskExecutor, workers)
    val plan = ModulePlan(module, code, options, declarations, analyzeModule(module, code, declarations, summaries, OBJECT_TYPE))
    return try {
        lowerFiles(plan, partitionedLayout(plan), taskExecutor, workers, claimOrder, functionLowerer, entryLowerer, installLowerer)
    } catch (tooLarge: DexTooLarge) {
        if (tooLarge.cause !is DexterFileFullException) throw tooLarge
        lowerFiles(plan, filePerBatchLayout(plan), taskExecutor, workers, claimOrder, functionLowerer, entryLowerer, installLowerer)
    }
}

private suspend fun lowerFiles(
    plan: ModulePlan,
    layout: DexLayout,
    taskExecutor: ParallelTaskExecutor,
    workers: WorkerSelection,
    claimOrder: (ModulePlan, DexLayout) -> IntArray,
    functionLowerer: FunctionLowerer,
    entryLowerer: EntryLowerer,
    installLowerer: InstallLowerer,
): DexModule {

    val lowering = ParallelLowering(plan, layout, claimOrder(plan, layout))
    val tasks = List<ParallelTaskScope.() -> ModuleLowering>(loweringWorkers(plan, layout, workers)) { { lowerClaimedFiles(lowering, functionLowerer) } }
    val lowerings = taskExecutor.execute(tasks)

    when (val failure = lowering.failures.error) {
        null -> Unit
        else -> {
            lowering.codeAndEntryFile?.abort()
            if (failure is DexterException) throw DexTooLarge(failure.message ?: failure.toString(), failure)
            throw failure
        }
    }

    return try {
        val bound = boundResources(lowerings.map(ModuleLowering::usedObjects), lowerings.map(ModuleLowering::usedValues))
        val codeDex = layout.files.indices.mapNotNull { file ->
            if (layout.holdsEntries(file)) null else checkNotNull(lowering.codeDex[file]) { "dex file $file was not lowered" }
        }
        finishModule(plan, codeDex, lowering.codeAndEntryFile, lowering.lowered, lowering.chunkStarts, bound, entryLowerer, installLowerer)
    } catch (tooLarge: DexterException) {
        throw DexTooLarge(tooLarge.message ?: tooLarge.toString(), tooLarge)
    }
}

private class ParallelLowering(
    val plan: ModulePlan,
    val layout: DexLayout,
    val order: IntArray,
) {
    val cursor = WorkCursor(order.size)
    val codeDex = arrayOfNulls<ByteArray>(layout.files.size)
    var codeAndEntryFile: DexFile? = null
    val lowered = LoweredFunctions(plan.functionCount)
    val chunkStarts = arrayOfNulls<IntArray>(plan.functionCount)
    val failures = EarliestFailure<Throwable>()
}

private fun ParallelTaskScope.lowerClaimedFiles(
    lowering: ParallelLowering,
    functionLowerer: FunctionLowerer,
): ModuleLowering {

    val worker = ModuleLowering(lowering.plan, lowering.chunkStarts)
    val dexter = Dexter()

    claimEach(lowering.cursor) { claimed ->
        val file = lowering.order[claimed]
        val batches = lowering.layout.files[file]
        if (lowering.failures.isBeyond(batches.first)) return@claimEach
        val dex = lowering.layout.beginFile(file, dexter)
        var batch = batches.first
        try {
            while (batch <= batches.last) {
                lowerBatch(worker, batch, dex, lowering.lowered, functionLowerer)
                batch++
            }
            if (lowering.layout.holdsEntries(file)) lowering.codeAndEntryFile = dex else lowering.codeDex[file] = dex.finish()
        } catch (cancelled: CancellationException) {
            dex.abort()
            throw cancelled
        } catch (failure: Throwable) {
            dex.abort()
            lowering.failures.report(batch, failure)
        }
    }

    return worker
}

internal fun largestFilesFirst(
    plan: ModulePlan,
    layout: DexLayout,
): IntArray {
    val work = LongArray(layout.files.size) { file -> fileWork(plan, layout.files[file]) }
    return layout.files.indices.sortedByDescending { file -> work[file] }.toIntArray()
}

private fun loweringWorkers(
    plan: ModulePlan,
    layout: DexLayout,
    workers: WorkerSelection,
): Int {

    var total = 0L
    var largest = 0L
    for (batches in layout.files) {
        val work = fileWork(plan, batches)
        total += work
        largest = maxOf(largest, work)
    }

    return workers(total, largest, layout.files.size)
}

private fun fileWork(
    plan: ModulePlan,
    batches: IntRange,
): Long = batches.sumOf { batch -> plan.batchWork(batch).toLong() }

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

private const val SUMMARY_BATCH_WORK = 16_000
