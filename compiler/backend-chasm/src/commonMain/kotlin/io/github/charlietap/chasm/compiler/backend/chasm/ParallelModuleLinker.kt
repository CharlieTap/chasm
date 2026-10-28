package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.compiler.backend.chasm.link.LinkContext
import io.github.charlietap.chasm.compiler.backend.chasm.link.PendingWasmCall
import io.github.charlietap.chasm.compiler.backend.chasm.link.createLinkContext
import io.github.charlietap.chasm.parallel.EarliestFailure
import io.github.charlietap.chasm.parallel.ParallelPlatform
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import io.github.charlietap.chasm.parallel.WorkBatches
import io.github.charlietap.chasm.parallel.WorkCursor
import io.github.charlietap.chasm.parallel.availableParallelProcessors
import io.github.charlietap.chasm.parallel.claimEach
import io.github.charlietap.chasm.parallel.executeWorkers
import io.github.charlietap.chasm.parallel.parallelPlatform
import io.github.charlietap.chasm.parallel.selectWorkerCount
import io.github.charlietap.chasm.parallel.workBatches
import io.github.charlietap.chasm.parallel.workersForBatches
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store

typealias ParallelModuleLinker = suspend (Store, ModuleInstance, Module, CirModule, ParallelTaskExecutor) -> Unit

suspend fun ParallelModuleLinker(
    store: Store,
    instance: ModuleInstance,
    module: Module,
    code: CirModule,
    taskExecutor: ParallelTaskExecutor,
) = ParallelModuleLinker(
    store = store,
    instance = instance,
    module = module,
    code = code,
    taskExecutor = taskExecutor,
    availableProcessors = availableParallelProcessors(),
    policy = linkingParallelism(),
    layoutBuilder = ::ModuleLayoutBuilder,
    functionLinker = ::FunctionLinker,
    programInstaller = ::ModuleProgramInstaller,
    moduleLinker = ::ModuleLinker,
)

internal suspend fun ParallelModuleLinker(
    store: Store,
    instance: ModuleInstance,
    module: Module,
    code: CirModule,
    taskExecutor: ParallelTaskExecutor,
    availableProcessors: Int,
    policy: ParallelismPolicy,
    layoutBuilder: ModuleLayoutBuilder,
    functionLinker: FunctionLinker,
    programInstaller: ModuleProgramInstaller,
    moduleLinker: ModuleLinker,
) {
    val functions = code.functions
    var work = 0L
    var largestFunction = 0L
    for (index in functions.indices) {
        val functionWork = linkingWork(functions[index].instructions.size).toLong()
        work += functionWork
        if (functionWork > largestFunction) largestFunction = functionWork
    }
    val selectedWorkers = selectWorkerCount(work, largestFunction, functions.size, policy, availableProcessors)
    if (selectedWorkers <= 1) return moduleLinker(store, instance, module, code)
    val batches = workBatches(functions.size, BATCH_WORK) { index -> linkingWork(functions[index].instructions.size) }
    val workerCount = workersForBatches(selectedWorkers, batches.count, policy)
    if (workerCount <= 1) return moduleLinker(store, instance, module, code)

    val context = createLinkContext(store, instance, module.definedTypes)
    val plan = layoutBuilder(context, code, store.program.size)
    val parallelLinking = ParallelLinking(batches)
    val pendingWasmCalls = taskExecutor.executeWorkers(workerCount) {
        linkFunctions(context.forWorker(), plan, parallelLinking, functionLinker)
    }
    parallelLinking.failures.error?.let { failure -> throw failure }
    programInstaller(store.program, context, plan, pendingWasmCalls)
}

private class ParallelLinking(
    val batches: WorkBatches,
) {
    val cursor = WorkCursor(batches.count)
    val failures = EarliestFailure<Throwable>()
}

private fun ParallelTaskScope.linkFunctions(
    context: LinkContext,
    plan: ModuleLinkPlan,
    work: ParallelLinking,
    functionLinker: FunctionLinker,
): List<PendingWasmCall> {
    claimEach(work.cursor) { batch ->
        for (index in work.batches.start(batch) until work.batches.end(batch)) {
            if (work.failures.isBeyond(index)) continue
            try {
                plan.link(context, index, functionLinker)
            } catch (exception: Throwable) {
                work.failures.report(index, exception)
            }
        }
    }
    return context.pendingWasmCalls
}

private fun linkingParallelism(): ParallelismPolicy = when (parallelPlatform) {
    ParallelPlatform.JVM -> JVM
    ParallelPlatform.ANDROID -> ANDROID
    ParallelPlatform.NATIVE -> NATIVE
}

private fun linkingWork(
    instructionCount: Int,
): Int = instructionCount + FUNCTION_LINKING_WORK

private val JVM = ParallelismPolicy(minimumWork = 8_000, workPerWorker = 2_000)
private val ANDROID = ParallelismPolicy(minimumWork = 64_000, workPerWorker = 4_000)
private val NATIVE = ParallelismPolicy(minimumWork = 8_000, workPerWorker = 2_000)

private const val FUNCTION_LINKING_WORK = 32
private const val BATCH_WORK = 2_048
