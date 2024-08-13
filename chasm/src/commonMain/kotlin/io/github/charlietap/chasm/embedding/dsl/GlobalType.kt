package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.Mutability
import io.github.charlietap.chasm.embedding.shapes.ValueType

class GlobalTypeBuilder {

    lateinit var valueType: ValueType
    lateinit var mutability: Mutability

    fun build() = GlobalType(
        valueType,
        mutability,
    )
}
