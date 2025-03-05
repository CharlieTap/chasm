package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun publicMemory(reference: ExternalValue.Memory = memoryExternalValue()) = Memory(reference)
