package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.instance.globalExternalValue

fun publicGlobal(reference: ExternalValue.Global = globalExternalValue()) = Global(reference)
