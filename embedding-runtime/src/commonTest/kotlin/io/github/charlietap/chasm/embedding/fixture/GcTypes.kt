package io.github.charlietap.chasm.embedding.fixture

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

internal fun registerStructType(
    runtime: RuntimeStore,
    fields: List<FieldType>,
): HostGcType {
    return registerType(runtime, CompositeType.Struct(StructType(fields)))
}

internal fun registerArrayType(
    runtime: RuntimeStore,
    field: FieldType,
): HostGcType {
    return registerType(runtime, CompositeType.Array(ArrayType(field)))
}

private fun registerType(
    runtime: RuntimeStore,
    compositeType: CompositeType,
): HostGcType {

    val subType = SubType.Final(
        superTypes = emptyList(),
        compositeType = compositeType,
    )
    val types = DefinedTypeFactory(listOf(RecursiveType(listOf(subType), RecursiveType.State.SYNTAX)))
    val runtimeType = runtime.heap.registerRuntimeTypes(types)[0]

    return HostGcType(runtimeType.value)
}
