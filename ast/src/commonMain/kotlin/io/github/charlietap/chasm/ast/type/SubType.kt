package io.github.charlietap.chasm.ast.type

sealed interface SubType : Type {

    var compositeType: CompositeType
    var superTypes: List<HeapType>

    data class Open(
        override var superTypes: List<HeapType>,
        override var compositeType: CompositeType,
    ) : SubType

    data class Final(
        override var superTypes: List<HeapType>,
        override var compositeType: CompositeType,
    ) : SubType
}
