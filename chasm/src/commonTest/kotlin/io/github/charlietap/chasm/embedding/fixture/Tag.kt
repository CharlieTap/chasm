package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tagExternalValue
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun publicTag(reference: ExternalValue.Tag = tagExternalValue()) = Tag(reference)
