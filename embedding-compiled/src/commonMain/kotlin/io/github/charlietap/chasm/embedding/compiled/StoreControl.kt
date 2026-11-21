package io.github.charlietap.chasm.embedding.compiled

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.value.ExecutionValue
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType

fun dropStore(store: Store) {
    store.engine.dropStore()
}

fun dropInstance(instance: Instance) {
    instance.engine.dropInstance(instance)
}

fun addFuel(
    store: Store,
    fuel: Long,
) {
    store.engine.addFuel(fuel)
}

fun resetFuel(store: Store) {
    store.engine.resetFuel()
}

fun remainingFuel(store: Store): Long {
    return store.engine.remainingFuel()
}

fun interrupt(store: Store): Boolean {
    return store.engine.interrupt()
}

fun collect(store: Store) {
    store.engine.collect()
}

suspend fun instance(
    store: Store,
    module: Module,
    imports: List<Import> = emptyList(),
    config: RuntimeConfig = RuntimeConfig(),
): ChasmResult<Instance, InstantiationError<ExceptionReference>> {
    return store.engine.instance(module, imports, config)
}

fun function(
    store: Store,
    type: FunctionType,
    function: HostFunction,
): Function {
    return store.engine.function(type, function)
}

fun memory(
    store: Store,
    type: MemoryType,
): Memory {
    return store.engine.memory(type)
}

fun global(
    store: Store,
    type: GlobalType,
    value: ExecutionValue,
): Global {
    return store.engine.global(type, value)
}

fun table(
    store: Store,
    type: TableType,
    initial: Any?,
): Table {
    return store.engine.table(type, initial)
}

fun tag(
    store: Store,
    type: TagType,
): Tag {
    return store.engine.tag(type)
}
