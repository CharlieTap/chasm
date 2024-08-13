package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.ValueType

fun publicTableType(
    limits: Limits = publicLimits(),
    referenceType: ValueType.Reference = publicReferenceValueType(),
) = TableType(limits, referenceType)
