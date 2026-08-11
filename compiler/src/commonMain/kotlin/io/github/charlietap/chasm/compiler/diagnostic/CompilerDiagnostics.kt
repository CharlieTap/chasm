package io.github.charlietap.chasm.compiler.diagnostic

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction

class CompilerDiagnostics(
    val instructionObserver: CompilerInstructionObserver? = null,
)

fun interface CompilerInstructionObserver {

    fun onInstruction(
        dispatchableInstruction: DispatchableInstruction,
        instruction: LinkedInstruction,
    )
}
