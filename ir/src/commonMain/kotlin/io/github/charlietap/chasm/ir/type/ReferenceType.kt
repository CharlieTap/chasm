package io.github.charlietap.chasm.ir.type

import kotlin.jvm.JvmInline

sealed interface ReferenceType : Type {

    val heapType: HeapType

    @JvmInline
    value class Ref(override val heapType: HeapType) : ReferenceType

    @JvmInline
    value class RefNull(override val heapType: HeapType) : ReferenceType
}
