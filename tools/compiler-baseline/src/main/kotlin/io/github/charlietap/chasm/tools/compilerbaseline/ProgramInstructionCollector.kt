package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.compiler.diagnostic.CompilerInstructionObserver
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.program.Program
import java.util.IdentityHashMap

interface ProgramInstructionCollector : CompilerInstructionObserver {

    fun instructions(
        program: Program,
        fromIndex: Int,
        toIndex: Int,
    ): List<ObservedInstruction>
}

class ObservedInstruction(
    val dispatchable: DispatchableInstruction,
    val instruction: LinkedInstruction,
)

class IdentityProgramInstructionCollector : ProgramInstructionCollector {

    private val instructions = IdentityHashMap<DispatchableInstruction, LinkedInstruction>()

    override fun onInstruction(
        dispatchableInstruction: DispatchableInstruction,
        instruction: LinkedInstruction,
    ) {
        val previous = instructions.putIfAbsent(dispatchableInstruction, instruction)
        check(previous == null || previous == instruction) {
            "dispatchable instruction was associated with both $previous and $instruction"
        }
    }

    override fun instructions(
        program: Program,
        fromIndex: Int,
        toIndex: Int,
    ): List<ObservedInstruction> = (fromIndex until toIndex).map { index ->
        val dispatchable = program.instructions[index]
        val instruction = checkNotNull(instructions[dispatchable]) {
            "no compiler instruction was recorded at program index $index"
        }
        ObservedInstruction(dispatchable, instruction)
    }
}
