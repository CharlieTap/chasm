package io.github.charlietap.chasm.decoder.fixture

import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.decoder.section.TagSection

internal fun tagSection(
    tags: List<Tag> = emptyList(),
) = TagSection(tags)
