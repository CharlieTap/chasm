package io.github.charlietap.chasm.config

fun moduleConfig(
    profile: Profile = Profile.Default,
    proposals: Set<Proposal> = emptySet(),
    decodeNameSection: Boolean = false,
) = ModuleConfig(
    profile = profile,
    proposals = proposals,
    decodeNameSection = decodeNameSection,
)
