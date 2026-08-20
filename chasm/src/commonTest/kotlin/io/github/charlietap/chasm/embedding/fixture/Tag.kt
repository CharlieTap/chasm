package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.fixture.runtime.instance.tagExternalValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.store.Store

fun publicTag(
    reference: ExternalValue.Tag = tagExternalValue(),
    store: Store = store(),
) = Tag(reference, store)
