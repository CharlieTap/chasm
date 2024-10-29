package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.instance.tagExternalValue

fun publicTag(reference: ExternalValue.Tag = tagExternalValue()) = Tag(reference)
