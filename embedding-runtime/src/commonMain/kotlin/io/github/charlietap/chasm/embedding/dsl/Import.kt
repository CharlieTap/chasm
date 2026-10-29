package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store

fun imports(store: Store, builder: ImportsBuilder.() -> Unit): List<Import> {
    return ImportsBuilder(store).apply(builder).build()
}

class ImportsBuilder(private val store: Store) {
    private val imports = mutableListOf<Import>()

    fun function(builder: FunctionImportBuilder.() -> Unit) {
        val import = FunctionImportBuilder(store).apply(builder).build()
        imports.add(import)
    }

    fun global(builder: GlobalImportBuilder.() -> Unit) {
        val import = GlobalImportBuilder(store).apply(builder).build()
        imports.add(import)
    }

    fun memory(builder: MemoryImportBuilder.() -> Unit) {
        val import = MemoryImportBuilder(store).apply(builder).build()
        imports.add(import)
    }

    fun table(builder: TableImportBuilder.() -> Unit) {
        val import = TableImportBuilder(store).apply(builder).build()
        imports.add(import)
    }

    fun tag(builder: TagImportBuilder.() -> Unit) {
        val import = TagImportBuilder(store).apply(builder).build()
        imports.add(import)
    }

    fun build(): List<Import> = imports
}
