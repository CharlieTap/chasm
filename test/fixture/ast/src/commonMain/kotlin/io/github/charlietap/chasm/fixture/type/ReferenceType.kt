package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.ReferenceType

fun referenceType() = ReferenceType.RefNull(HeapType.Func)
