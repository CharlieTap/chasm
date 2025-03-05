package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.runtime.instance.ExternalValue

object ImportableMapper : BidirectionalMapper<Importable, ExternalValue> {
    override fun map(input: Importable): ExternalValue = when (input) {
        is Function -> input.reference
        is Global -> input.reference
        is Memory -> input.reference
        is Table -> input.reference
        is Tag -> input.reference
    }

    override fun bimap(input: ExternalValue): Importable = when (input) {
        is ExternalValue.Function -> Function(input)
        is ExternalValue.Global -> Global(input)
        is ExternalValue.Memory -> Memory(input)
        is ExternalValue.Table -> Table(input)
        is ExternalValue.Tag -> Tag(input)
    }
}
