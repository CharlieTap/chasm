package io.github.charlietap.chasm.fixture.ir.type

import io.github.charlietap.chasm.fixture.ir.module.typeIndex
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.ConcreteHeapType
import io.github.charlietap.chasm.ir.type.DefinedType
import io.github.charlietap.chasm.ir.type.HeapType

fun heapType(): HeapType = abstractHeapType()

fun abstractHeapType(): AbstractHeapType = functionHeapType()

fun noneHeapType() = AbstractHeapType.None

fun functionHeapType() = AbstractHeapType.Func

fun concreteHeapType(): ConcreteHeapType = concreteTypeIndexHeapType()

fun concreteTypeIndexHeapType(
    typeIndex: Index.TypeIndex = typeIndex(),
) = ConcreteHeapType.TypeIndex(typeIndex)

fun concreteRecursiveTypeIndexHeapType(
    recursiveTypeIndex: Int = 0,
) = ConcreteHeapType.RecursiveTypeIndex(recursiveTypeIndex)

fun concreteDefinedTypeHeapType(
    definedType: DefinedType = definedType(),
) = ConcreteHeapType.Defined(definedType)
