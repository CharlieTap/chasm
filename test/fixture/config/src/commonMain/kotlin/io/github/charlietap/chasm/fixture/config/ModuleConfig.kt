package io.github.charlietap.chasm.fixture.config

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.Profile
import io.github.charlietap.chasm.config.Proposal

fun moduleConfig(
    profile: Profile = Profile.Default,
    proposals: Set<Proposal> = emptySet(),
    decodeNameSection: Boolean = false,
) = ModuleConfig(
    profile = profile,
    proposals = proposals,
    decodeNameSection = decodeNameSection,
)
