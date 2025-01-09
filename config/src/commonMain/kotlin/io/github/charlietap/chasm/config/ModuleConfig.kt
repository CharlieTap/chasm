package io.github.charlietap.chasm.config

data class ModuleConfig(
    val profile: Profile,
    val proposals: Set<Proposal>,
) {
    companion object {
        val WASM_3_PROPOSALS = setOf(
            Proposal.TailCall,
            Proposal.ExtendedConstExpression,
            Proposal.TypedFunctionReferences,
            Proposal.WasmGC,
            Proposal.MultipleMemories,
            Proposal.ExceptionHandling,
        )

        fun default() = ModuleConfig(
            profile = Profile.Default,
            proposals = Proposal.entries.toSet(),
        )
    }
}
