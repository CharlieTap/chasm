package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.runtime.instance.ExternalValue

fun publicTable(reference: ExternalValue.Table = tableExternalValue()) = Table(reference)
