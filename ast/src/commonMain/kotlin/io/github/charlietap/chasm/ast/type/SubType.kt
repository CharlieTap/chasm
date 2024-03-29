package io.github.charlietap.chasm.ast.type

import io.github.charlietap.chasm.ast.module.Index

sealed interface SubType : Type {

    val typeIndices: List<Index.TypeIndex>
    val compositeType: CompositeType

    data class Open(
        override val typeIndices: List<Index.TypeIndex>,
        override val compositeType: CompositeType,
    ) : SubType

    data class Final(
        override val typeIndices: List<Index.TypeIndex>,
        override val compositeType: CompositeType,
    ) : SubType
}
