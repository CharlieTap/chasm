package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.fixture.module.typeIndex

fun heapType() = abstractHeapType()

fun abstractHeapType() = AbstractHeapType.Func

fun concreteHeapType(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ConcreteHeapType(typeIndex)
