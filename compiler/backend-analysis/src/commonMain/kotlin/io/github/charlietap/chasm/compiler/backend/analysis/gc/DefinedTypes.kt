package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.SubType

fun DefinedType.subType(): SubType {
    return recursiveType.subTypes[recursiveTypeIndex]
}

fun resolveDefinedType(
    type: HeapType,
    types: List<DefinedType>,
    owner: DefinedType?,
): DefinedType {
    return when (type) {
        is ConcreteHeapType.TypeIndex -> types[type.index]
        is ConcreteHeapType.Defined -> type.definedType
        is ConcreteHeapType.RecursiveTypeIndex -> {
            val group = checkNotNull(owner) { "$type names a type in a group without an owner" }
            DefinedType(group.recursiveType, type.index, group.typeIndex - group.recursiveTypeIndex + type.index)
        }
        is AbstractHeapType -> error("$type does not name a defined type")
    }
}
