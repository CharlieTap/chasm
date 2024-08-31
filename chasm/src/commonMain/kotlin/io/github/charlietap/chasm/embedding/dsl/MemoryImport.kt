package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.MemoryType
import io.github.charlietap.chasm.embedding.shapes.Store

fun memoryImport(store: Store, builder: MemoryImportBuilder.() -> Unit): Import {
    return MemoryImportBuilder(store).apply(builder).build()
}

class MemoryImportBuilder(private val store: Store) {

    lateinit var moduleName: String
    lateinit var entityName: String
    private var type: MemoryType? = null

    fun type(builder: MemoryTypeBuilder.() -> Unit) {
        type = MemoryTypeBuilder().apply(builder).build()
    }

    fun build(): Import {
        val memoryType = requireNotNull(type)
        return Import(
            moduleName,
            entityName,
            memory(store, memoryType),
        )
    }
}
