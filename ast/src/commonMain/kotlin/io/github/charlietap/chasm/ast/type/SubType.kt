package io.github.charlietap.chasm.ast.type

sealed interface SubType : Type {

    val compositeType: CompositeType
    val superTypes: List<HeapType>

    data class Open(
        override val superTypes: List<HeapType>,
        override val compositeType: CompositeType,
    ) : SubType

    data class Final(
        override val superTypes: List<HeapType>,
        override val compositeType: CompositeType,
    ) : SubType
}
