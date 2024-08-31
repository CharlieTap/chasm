package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.embedding.shapes.ValueType

fun publicNumberValueType(): ValueType.Number = publicI32ValueType()

fun publicI32ValueType(): ValueType.Number = ValueType.Number.I32

fun publicI64ValueType(): ValueType.Number = ValueType.Number.I64

fun publicF32ValueType(): ValueType.Number = ValueType.Number.F32

fun publicF64ValueType(): ValueType.Number = ValueType.Number.F64
