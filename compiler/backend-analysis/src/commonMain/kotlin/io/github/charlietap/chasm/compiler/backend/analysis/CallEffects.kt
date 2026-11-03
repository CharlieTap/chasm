package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.CirModule
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.MemoryInstruction

/**
 * Whether calling each function may grow a memory or run a guest collection: through its own code, an import, an
 * indirect call, or a function it calls directly or in tail position.
 */
internal class CallEffects(
    code: CirModule,
    declarations: ModuleIndexSpaces,
) {
    val mayGrowMemory = BooleanArray(declarations.functionCount)
    val mayCollect = BooleanArray(declarations.functionCount)

    init {
        mayGrowMemory.fill(true, 0, declarations.importedFunctionCount)
        mayCollect.fill(true, 0, declarations.importedFunctionCount)
        var callees = IntArray(INITIAL_CALLS)
        var callers = IntArray(INITIAL_CALLS)
        var calls = 0
        for (function in code.functions) {
            val caller = function.index.index
            for (instruction in function.instructions) {
                when (instruction) {
                    is ControlInstruction -> {
                        val callee = instruction.directCallee()
                        if (callee != null) {
                            if (calls == callees.size) {
                                callees = callees.copyOf(calls * 2)
                                callers = callers.copyOf(calls * 2)
                            }
                            callees[calls] = callee.index
                            callers[calls++] = caller
                        } else if (instruction.callsUnknownFunction()) {
                            mayGrowMemory[caller] = true
                            mayCollect[caller] = true
                        } else if (instruction.allocatesGuestObject()) {
                            mayCollect[caller] = true
                        }
                    }
                    is MemoryInstruction -> if (instruction.growsMemory()) mayGrowMemory[caller] = true
                    is AggregateInstruction -> if (instruction.allocatesGuestObject()) mayCollect[caller] = true
                    else -> Unit
                }
            }
        }
        val graph = PropagationGraph(declarations.functionCount, callees.copyOf(calls), callers.copyOf(calls))
        graph.spread(mayGrowMemory)
        graph.spread(mayCollect)
    }
}

private fun ControlInstruction.directCallee(): FunctionIndex? = when (this) {
    is ControlInstruction.Call -> function
    is ControlInstruction.ReturnCall -> function
    else -> null
}

private fun ControlInstruction.callsUnknownFunction(): Boolean = this is ControlInstruction.CallIndirectI ||
    this is ControlInstruction.CallIndirectS || this is ControlInstruction.CallRefS ||
    this is ControlInstruction.ReturnCallIndirectI || this is ControlInstruction.ReturnCallIndirectS ||
    this is ControlInstruction.ReturnCallRefS

private fun MemoryInstruction.growsMemory(): Boolean = this is MemoryInstruction.MemoryGrowI || this is MemoryInstruction.MemoryGrowS

private const val INITIAL_CALLS = 64
