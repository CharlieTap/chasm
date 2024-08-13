package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.ValueType

class TableTypeBuilder {

    var referenceType: ValueType.Reference? = null
    private var limits: Limits? = null

    fun limits(builder: LimitsBuilder.() -> Unit) {
        limits = LimitsBuilder().apply(builder).build()
    }

    fun build() = TableType(requireNotNull(limits), requireNotNull(referenceType))
}
