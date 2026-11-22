package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

sealed class Importable(
    @PublishedApi internal val engine: Engine,
    @PublishedApi internal val payload: Any,
) {

    override fun equals(other: Any?): Boolean {
        return other is Importable && other.engine === engine && other.payload == payload
    }

    override fun hashCode(): Int {
        return payload.hashCode()
    }
}

class Function internal constructor(
    engine: Engine,
    payload: Any,
    val type: FunctionType,
) : Importable(engine, payload)

class Memory internal constructor(
    engine: Engine,
    payload: Any,
    val type: MemoryType,
) : Importable(engine, payload)

class Global internal constructor(
    engine: Engine,
    payload: Any,
    val type: GlobalType,
) : Importable(engine, payload)

class Table internal constructor(
    engine: Engine,
    payload: Any,
    val type: TableType,
) : Importable(engine, payload)

class Tag internal constructor(
    engine: Engine,
    payload: Any,
    val type: TagType,
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

@InternalChasmApi
fun _function(
    engine: Engine,
    payload: Any,
    type: FunctionType,
): Function {
    return Function(engine, payload, type)
}

@InternalChasmApi
fun _memory(
    engine: Engine,
    payload: Any,
    type: MemoryType,
): Memory {
    return Memory(engine, payload, type)
}

@InternalChasmApi
fun _global(
    engine: Engine,
    payload: Any,
    type: GlobalType,
): Global {
    return Global(engine, payload, type)
}

@InternalChasmApi
fun _table(
    engine: Engine,
    payload: Any,
    type: TableType,
): Table {
    return Table(engine, payload, type)
}

@InternalChasmApi
fun _tag(
    engine: Engine,
    payload: Any,
    type: TagType,
): Tag {
    return Tag(engine, payload, type)
}

@InternalChasmApi
inline fun Importable._engine(): Engine {
    return engine
}

@InternalChasmApi
inline fun Importable._payload(): Any {
    return payload
}
