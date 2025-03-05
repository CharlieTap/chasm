package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun publicGlobal(reference: ExternalValue.Global = globalExternalValue()) = Global(reference)
