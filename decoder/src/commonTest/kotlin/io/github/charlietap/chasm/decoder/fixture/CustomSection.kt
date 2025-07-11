package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Custom
import io.github.charlietap.chasm.decoder.section.CustomSection
import io.github.charlietap.chasm.fixture.ast.module.custom

internal fun customSection(
    custom: Custom = custom(),
) = CustomSection(custom)
