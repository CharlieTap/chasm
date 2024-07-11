package io.github.charlietap.chasm.type.differ

import io.github.charlietap.chasm.ast.type.ReferenceType

fun ReferenceTypeDiffer(
    type1: ReferenceType,
    type2: ReferenceType,
): ReferenceType = when (type2) {
    is ReferenceType.Ref -> {
        type1
    }
    is ReferenceType.RefNull -> {
        ReferenceType.Ref(type1.heapType)
    }
}
