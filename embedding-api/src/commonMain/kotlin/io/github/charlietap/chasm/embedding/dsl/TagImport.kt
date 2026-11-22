package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.type.TagType

fun tagImport(store: Store, builder: TagImportBuilder.() -> Unit): Import {
    return TagImportBuilder(store).apply(builder).build()
}

class TagImportBuilder(private val store: Store) {

    lateinit var moduleName: String
    lateinit var entityName: String
    private var type: TagType? = null

    fun type(builder: TagTypeBuilder.() -> Unit) {
        type = TagTypeBuilder().apply(builder).build()
    }

    fun build(): Import {
        return Import(
            moduleName,
            entityName,
            tag(store, requireNotNull(type)),
        )
    }
}
