package io.github.charlietap.chasm.runtime.program

import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction

/** Optional execution backend, called after linking and before module initialization. */
fun interface ProgramCompiler {
    /**
     * [instructions] describes the appended module at [firstIp]. Function entries
     * and branch targets are absolute program IPs. Implementations may replace
     * dispatchers but must preserve these IPs and the guest calling convention.
     * Return an error instead of silently falling back when compilation fails.
     */
    fun compile(
        program: Program,
        firstIp: Int,
        instructions: List<LinkedInstruction>,
        functionEntryIps: IntArray,
    ): ModuleTrapError?
}
