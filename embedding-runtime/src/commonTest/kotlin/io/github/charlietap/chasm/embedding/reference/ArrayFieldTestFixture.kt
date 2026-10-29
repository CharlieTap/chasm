package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory

internal class ArrayFieldTestFixture(
    val store: Store,
    val reference: ReferenceValue.Array,
    val rawReference: Long,
)

internal fun arrayFieldTestFixture(
    arrayType: ArrayType,
    fields: LongArray,
): ArrayFieldTestFixture {
    val internalStore = store()
    val types = DefinedTypeFactory(
        listOf(
            RecursiveType(
                subTypes = listOf(
                    SubType.Final(
                        superTypes = emptyList(),
                        compositeType = CompositeType.Array(arrayType),
                    ),
                ),
                state = RecursiveType.State.SYNTAX,
            ),
        ),
    )
    val runtimeType = internalStore.heap.registerRuntimeTypes(types)[0]
    val rawReference = internalStore.heap.allocateArrayFromElements(runtimeType, fields, 0, fields.size)
    return ArrayFieldTestFixture(
        store = publicStore(internalStore),
        reference = ReferenceValue.Array(
            Address.Array((rawReference shr RV_SHIFT_BITS).toInt()),
        ),
        rawReference = rawReference,
    )
}
