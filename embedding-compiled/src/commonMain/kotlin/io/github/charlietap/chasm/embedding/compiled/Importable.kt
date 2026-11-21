package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.InternalChasmApi

sealed class Importable(
    @property:InternalChasmApi val engine: CompiledEngine,
    @property:InternalChasmApi val payload: Any,
) {

    override fun equals(other: Any?): Boolean {
        return other is Importable && other.javaClass == javaClass && other.engine === engine && other.payload == payload
    }

    override fun hashCode(): Int {
        return payload.hashCode()
    }
}

class Function
    @InternalChasmApi
    constructor(
        engine: CompiledEngine,
        payload: Any,
    ) : Importable(engine, payload)

class Memory
    @InternalChasmApi
    constructor(
        engine: CompiledEngine,
        payload: Any,
    ) : Importable(engine, payload)

class Global
    @InternalChasmApi
    constructor(
        engine: CompiledEngine,
        payload: Any,
    ) : Importable(engine, payload)

class Table
    @InternalChasmApi
    constructor(
        engine: CompiledEngine,
        payload: Any,
    ) : Importable(engine, payload)

class Tag
    @InternalChasmApi
    constructor(
        engine: CompiledEngine,
        payload: Any,
    ) : Importable(engine, payload)

data class Import(
    val moduleName: String,
    val entityName: String,
    val value: Importable,
)

data class Export(
    val name: String,
    val value: Importable,
)
