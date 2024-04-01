package io.github.charlietap.chasm.fixture.type

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.fixture.module.typeIndex

fun heapType(): HeapType = abstractHeapType()

fun abstractHeapType(): AbstractHeapType = functionHeapType()

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
