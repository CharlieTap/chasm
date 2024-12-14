package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryExternalValue

fun publicMemory(reference: ExternalValue.Memory = memoryExternalValue()) = Memory(reference)
