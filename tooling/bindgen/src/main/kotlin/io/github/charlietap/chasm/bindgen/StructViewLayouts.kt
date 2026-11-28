package io.github.charlietap.chasm.bindgen

import io.github.charlietap.chasm.storage.layout.StructLayout
import io.github.charlietap.chasm.storage.layout.storageLayouts
import io.github.charlietap.chasm.type.DefinedType

internal interface StructViewLayouts {

    fun layout(type: DefinedType): StructLayout
}

internal class StorageLayoutsStructViewLayouts : StructViewLayouts {

    override fun layout(type: DefinedType): StructLayout {
        return checkNotNull(storageLayouts(listOf(type)).struct(0)) { "type ${type.typeIndex} has no storage layout" }
    }
}
