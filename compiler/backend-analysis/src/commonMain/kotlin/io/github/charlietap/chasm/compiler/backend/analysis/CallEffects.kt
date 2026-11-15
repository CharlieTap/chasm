package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.CirModule

/**
 * Whether calling each function may grow a memory or run a guest collection: through its own code, an import, an
 * indirect call, or a function it calls directly or in tail position.
 */
internal class CallEffects(
    code: CirModule,
    declarations: ModuleIndexSpaces,
    summaries: List<FunctionSummary>,
) {
    val mayGrowMemory = BooleanArray(declarations.functionCount)
    val mayCollect = BooleanArray(declarations.functionCount)
    val mayUnwind = BooleanArray(declarations.functionCount)
    val requestsTailCall = BooleanArray(declarations.functionCount)

    init {
        mayGrowMemory.fill(true, 0, declarations.importedFunctionCount)
        mayCollect.fill(true, 0, declarations.importedFunctionCount)
        mayUnwind.fill(true, 0, declarations.importedFunctionCount)

        var calls = 0
        for (summary in summaries) calls += summary.directCallees.size
        val callees = IntArray(calls)
        val callers = IntArray(calls)
        calls = 0

        summaries.forEachIndexed { function, summary ->
            val caller = code.functions[function].index.index
            if (summary.requestsTailCall) requestsTailCall[caller] = true
            if (summary.mayGrowMemory) mayGrowMemory[caller] = true
            if (summary.mayCollect) mayCollect[caller] = true
            if (summary.mayUnwind) mayUnwind[caller] = true
            for (callee in summary.directCallees) {
                callees[calls] = callee
                callers[calls++] = caller
            }
        }

        val graph = PropagationGraph(declarations.functionCount, callees, callers)
        graph.spread(mayGrowMemory)
        graph.spread(mayCollect)
        graph.spread(mayUnwind)
    }
}
