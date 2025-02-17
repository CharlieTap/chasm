package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ir.type.GlobalType
import io.github.charlietap.chasm.ir.type.Mutability
import io.github.charlietap.chasm.ir.type.ValueType

class GlobalTypeBuilder {

    lateinit var valueType: ValueType
    lateinit var mutability: Mutability

    fun build() = GlobalType(
        valueType,
        mutability,
    )
}
