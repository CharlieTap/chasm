package io.github.charlietap.chasm.executor.invoker.type

import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class CasterTest {

    @Test
    fun `abstract reference tests use the expected runtime tags`() {
        val cases = listOf(
            AbstractHeapType.Func to setOf(RV_TYPE_FUNCTION),
            AbstractHeapType.Extern to setOf(RV_TYPE_EXTERN),
            AbstractHeapType.Exception to setOf(RV_TYPE_EXCEPTION),
            AbstractHeapType.Any to setOf(RV_TYPE_I31, RV_TYPE_STRUCT, RV_TYPE_ARRAY, RV_TYPE_HOST),
            AbstractHeapType.Eq to setOf(RV_TYPE_I31, RV_TYPE_STRUCT, RV_TYPE_ARRAY),
            AbstractHeapType.I31 to setOf(RV_TYPE_I31),
            AbstractHeapType.Struct to setOf(RV_TYPE_STRUCT),
            AbstractHeapType.Array to setOf(RV_TYPE_ARRAY),
            AbstractHeapType.NoFunc to emptySet(),
            AbstractHeapType.NoExtern to emptySet(),
            AbstractHeapType.NoException to emptySet(),
            AbstractHeapType.None to emptySet(),
        )

        cases.forEach { (heapType, acceptedTags) ->
            val test = ReferenceTypeTest.from(ReferenceType.Ref(heapType), RuntimeTypeMap.Empty)
            REFERENCE_TAGS.forEach { tag ->
                assertEquals(tag in acceptedTags, Caster(tag, test, Store()), "$heapType against tag $tag")
            }
        }
    }

    @Test
    fun `null only passes nullable reference tests`() {
        val nonNull = ReferenceTypeTest.from(ReferenceType.Ref(AbstractHeapType.Any), RuntimeTypeMap.Empty)
        val nullable = ReferenceTypeTest.from(ReferenceType.RefNull(AbstractHeapType.Any), RuntimeTypeMap.Empty)

        assertEquals(false, Caster(RV_TYPE_NULL, nonNull, Store()))
        assertEquals(true, Caster(RV_TYPE_NULL, nullable, Store()))
    }

    @Test
    fun `concrete reference tests use canonical subtype displays`() {
        val store = Store()
        val runtimeTypes = store.heap.registerRuntimeTypes(structChain())
        val leafReference = store.heap.allocateStruct(runtimeTypes[1], LongArray(0))
        val rootReference = store.heap.allocateStruct(runtimeTypes[0], LongArray(0))
        val rootTest = ReferenceTypeTest.from(
            ReferenceType.Ref(ConcreteHeapType.TypeIndex(0)),
            runtimeTypes,
        )
        val leafTest = ReferenceTypeTest.from(
            ReferenceType.Ref(ConcreteHeapType.TypeIndex(1)),
            runtimeTypes,
        )

        assertEquals(true, Caster(leafReference, rootTest, store))
        assertEquals(true, Caster(leafReference, leafTest, store))
        assertEquals(false, Caster(rootReference, leafTest, store))
    }

    @Test
    fun `concrete struct casts reject wrong kind forged interior and stale values`() {
        val store = Store()
        val runtimeTypes = store.heap.registerRuntimeTypes(twoFieldStruct())
        val reference = store.heap.allocateStruct(runtimeTypes[0], longArrayOf(1L, 2L))
        val test = ReferenceTypeTest.from(
            ReferenceType.Ref(ConcreteHeapType.TypeIndex(0)),
            runtimeTypes,
        )
        val interior = reference + (1L shl RV_SHIFT_BITS)
        val forged = (500_000L shl RV_SHIFT_BITS) or RV_TYPE_STRUCT
        val wrongKind = (reference and RV_TYPE_STRUCT.inv()) or RV_TYPE_ARRAY

        assertEquals(true, Caster(reference, test, store))
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(interior))
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(forged))
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(wrongKind))
        assertEquals(false, Caster(interior, test, store))
        assertEquals(false, Caster(forged, test, store))
        assertEquals(false, Caster(wrongKind, test, store))

        store.heap.collectGarbage(store)

        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(reference))
        assertEquals(false, Caster(reference, test, store))
    }

    private companion object {
        val REFERENCE_TAGS = listOf(
            RV_TYPE_I31,
            RV_TYPE_STRUCT,
            RV_TYPE_ARRAY,
            RV_TYPE_FUNCTION,
            RV_TYPE_HOST,
            RV_TYPE_EXCEPTION,
            RV_TYPE_EXTERN,
        )

        fun structChain() = DefinedTypeFactory(
            listOf(
                recursiveStruct(parent = null),
                recursiveStruct(parent = 0),
            ),
        )

        fun twoFieldStruct() = DefinedTypeFactory(
            listOf(
                RecursiveType(
                    subTypes = listOf(
                        SubType.Final(
                            superTypes = emptyList(),
                            compositeType = CompositeType.Struct(
                                StructType(
                                    List(2) {
                                        FieldType(
                                            StorageType.Value(ValueType.Number(NumberType.I64)),
                                            Mutability.Var,
                                        )
                                    },
                                ),
                            ),
                        ),
                    ),
                    state = RecursiveType.State.SYNTAX,
                ),
            ),
        )

        fun recursiveStruct(parent: Int?) = RecursiveType(
            subTypes = listOf(
                SubType.Open(
                    superTypes = parent?.let { listOf(ConcreteHeapType.TypeIndex(it)) }.orEmpty(),
                    compositeType = CompositeType.Struct(StructType(emptyList())),
                ),
            ),
            state = RecursiveType.State.SYNTAX,
        )
    }
}
