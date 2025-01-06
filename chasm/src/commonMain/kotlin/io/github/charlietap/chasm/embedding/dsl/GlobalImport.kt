package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.shapes.GlobalType
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue

fun globalImport(store: Store, builder: GlobalImportBuilder.() -> Unit): Import {
    return GlobalImportBuilder(store).apply(builder).build()
}

class GlobalImportBuilder(private val store: Store) {

    lateinit var moduleName: String
    lateinit var entityName: String
    private var type: GlobalType? = null
    lateinit var value: ExecutionValue

    fun type(builder: GlobalTypeBuilder.() -> Unit) {
        type = GlobalTypeBuilder().apply(builder).build()
    }

    fun build(): Import {
        return Import(
            moduleName,
            entityName,
            global(store, requireNotNull(type), value),
        )
    }
}
