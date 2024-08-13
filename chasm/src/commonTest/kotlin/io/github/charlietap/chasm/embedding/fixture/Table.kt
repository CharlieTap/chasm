package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.instance.tableExternalValue

fun publicTable(reference: ExternalValue.Table = tableExternalValue()) = Table(reference)
