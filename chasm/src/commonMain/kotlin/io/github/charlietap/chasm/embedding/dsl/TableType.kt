package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType

class TableTypeBuilder {

    var referenceType: ReferenceType? = null
    private var limits: Limits? = null

    fun limits(builder: LimitsBuilder.() -> Unit) {
        limits = LimitsBuilder().apply(builder).build()
    }

    fun build() = TableType(
        referenceType = requireNotNull(referenceType),
        limits = requireNotNull(limits),
    )
}
