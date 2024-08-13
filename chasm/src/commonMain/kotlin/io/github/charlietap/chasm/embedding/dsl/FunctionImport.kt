package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.FunctionType
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.embedding.shapes.HostFunctionFactory
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store

fun functionImport(store: Store, builder: FunctionImportBuilder.() -> Unit): Import {
    return FunctionImportBuilder(store).apply(builder).build()
}

class FunctionImportBuilder(private val store: Store) {

    lateinit var moduleName: String
    lateinit var entityName: String
    private var type: FunctionType? = null
    private lateinit var hostFunction: HostFunction

    fun type(builder: FunctionTypeBuilder.() -> Unit) {
        type = FunctionTypeBuilder().apply(builder).build()
    }

    fun reference(reference: HostFunctionFactory) {
        hostFunction = reference(store)
    }

    fun reference(reference: HostFunction) {
        hostFunction = reference
    }

    fun build(): Import {
        return Import(
            moduleName,
            entityName,
            function(store, requireNotNull(type), hostFunction),
        )
    }
}
