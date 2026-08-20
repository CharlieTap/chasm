package io.github.charlietap.chasm.embedding.transform

import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Importable
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.store.Store

internal class ImportableMapper(
    private val store: Store,
) : BidirectionalMapper<Importable, ExternalValue> {
    override fun map(input: Importable): ExternalValue = when (input) {
        is Function -> input.reference
        is Global -> input.reference
        is Memory -> input.reference
        is Table -> input.reference
        is Tag -> input.reference
    }

    override fun bimap(input: ExternalValue): Importable = when (input) {
        is ExternalValue.Function -> Function(input, store)
        is ExternalValue.Global -> Global(input, store)
        is ExternalValue.Memory -> Memory(input, store)
        is ExternalValue.Table -> Table(input, store)
        is ExternalValue.Tag -> Tag(input, store)
    }
}
