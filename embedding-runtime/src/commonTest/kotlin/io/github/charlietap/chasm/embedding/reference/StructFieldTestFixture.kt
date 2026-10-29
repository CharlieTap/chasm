package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory

internal class StructFieldTestFixture(
    val store: Store,
    val reference: ReferenceValue.Struct,
    val rawReference: Long,
)

internal fun structFieldTestFixture(
    fieldType: FieldType,
    initialValue: Long,
): StructFieldTestFixture {
    val internalStore = store()
    val types = DefinedTypeFactory(
        listOf(
            RecursiveType(
                subTypes = listOf(
                    SubType.Final(
                        superTypes = emptyList(),
                        compositeType = CompositeType.Struct(StructType(listOf(fieldType))),
                    ),
                ),
                state = RecursiveType.State.SYNTAX,
            ),
        ),
    )
    val runtimeType = internalStore.heap.registerRuntimeTypes(types)[0]
    val rawReference = internalStore.heap.allocateStruct(runtimeType, longArrayOf(initialValue))
    return StructFieldTestFixture(
        store = publicStore(internalStore),
        reference = ReferenceValue.Struct(Address.Struct((rawReference shr 8).toInt())),
        rawReference = rawReference,
    )
}
