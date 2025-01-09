package io.github.charlietap.chasm.config

fun moduleConfig(
    profile: Profile = Profile.Default,
    proposals: Set<Proposal> = emptySet(),
) = ModuleConfig(
    profile = profile,
    proposals = proposals,
)
