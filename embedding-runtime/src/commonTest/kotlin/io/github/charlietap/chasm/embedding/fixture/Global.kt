package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.store.Store

fun publicGlobal(
    reference: ExternalValue.Global = globalExternalValue(),
    store: Store = store(),
) = Global(reference, store)
