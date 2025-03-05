package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun publicFunction(
    reference: ExternalValue.Function = functionExternalValue(),
): Function = Function(reference)
