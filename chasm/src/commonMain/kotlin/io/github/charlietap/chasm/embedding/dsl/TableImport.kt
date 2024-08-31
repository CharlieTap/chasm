package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.TableType
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.table

fun tableImport(store: Store, builder: TableImportBuilder.() -> Unit): Import {
    return TableImportBuilder(store).apply(builder).build()
}

class TableImportBuilder(private val store: Store) {

    lateinit var moduleName: String
    lateinit var entityName: String
    private var type: TableType? = null
    lateinit var value: Value.Reference

    fun type(builder: TableTypeBuilder.() -> Unit) {
        type = TableTypeBuilder().apply(builder).build()
    }

    fun build(): Import {
        return Import(
            moduleName,
            entityName,
            table(store, requireNotNull(type), value),
        )
    }
}
