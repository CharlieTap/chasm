package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.decoder.section.DataCountSection

internal fun dataCountSection(
    dataCount: UInt = 0u,
) = DataCountSection(dataCount)
