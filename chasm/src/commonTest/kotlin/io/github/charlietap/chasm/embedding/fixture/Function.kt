package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionExternalValue

fun publicFunction(
    reference: ExternalValue.Function = functionExternalValue(),
): Function = Function(reference)
