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
    ): List<String>
}

class IdentityProgramInstructionCollector(
    private val tagTranslator: CompilerInstructionTagTranslator,
) : ProgramInstructionCollector {

    private val instructionTags = IdentityHashMap<DispatchableInstruction, String>()

    override fun onInstruction(
        dispatchableInstruction: DispatchableInstruction,
        instruction: LinkedInstruction,
    ) {
        val tag = tagTranslator.translate(instruction)
        val previousTag = instructionTags.put(dispatchableInstruction, tag)
        check(previousTag == null || previousTag == tag) {
            "dispatchable instruction was associated with both $previousTag and $tag"
        }
    }

    override fun instructions(
        program: Program,
        fromIndex: Int,
        toIndex: Int,
    ): List<String> = (fromIndex until toIndex).map { index ->
        checkNotNull(instructionTags[program.instructions[index]]) {
            "no compiler instruction was recorded at program index $index"
        }
    }
}
