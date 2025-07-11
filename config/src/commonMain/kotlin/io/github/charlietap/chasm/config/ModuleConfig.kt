package io.github.charlietap.chasm.config

data class ModuleConfig(
    val profile: Profile = Profile.Default,
    val proposals: Set<Proposal> = Proposal.entries.toSet(),
    val decodeNameSection: Boolean = false,
)
