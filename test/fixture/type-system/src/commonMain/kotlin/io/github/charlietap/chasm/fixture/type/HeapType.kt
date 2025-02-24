package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType

fun heapType(): HeapType = abstractHeapType()

fun abstractHeapType(): AbstractHeapType = functionHeapType()

fun noneHeapType() = AbstractHeapType.None

fun functionHeapType() = AbstractHeapType.Func

fun concreteHeapType(): ConcreteHeapType = concreteTypeIndexHeapType()

fun concreteTypeIndexHeapType(
    typeIndex: Int = 0,
) = ConcreteHeapType.TypeIndex(typeIndex)

fun concreteRecursiveTypeIndexHeapType(
    recursiveTypeIndex: Int = 0,
) = ConcreteHeapType.RecursiveTypeIndex(recursiveTypeIndex)

fun concreteDefinedTypeHeapType(
    definedType: DefinedType = definedType(),
) = ConcreteHeapType.Defined(definedType)
