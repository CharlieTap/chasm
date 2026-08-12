package io.github.charlietap.chasm.executor.invoker.type

import io.github.charlietap.chasm.fixture.runtime.instance.structInstance
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
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
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
        val runtimeTypes = store.runtimeTypes.register(structChain())
        store.structs += structInstance(rtt = runtimeTypes[1])
        store.structs += structInstance(rtt = runtimeTypes[0])
        val rootTest = ReferenceTypeTest.from(
            ReferenceType.Ref(ConcreteHeapType.TypeIndex(0)),
            runtimeTypes,
        )
        val leafTest = ReferenceTypeTest.from(
            ReferenceType.Ref(ConcreteHeapType.TypeIndex(1)),
            runtimeTypes,
        )

        assertEquals(true, Caster(RV_TYPE_STRUCT, rootTest, store))
        assertEquals(true, Caster(RV_TYPE_STRUCT, leafTest, store))
        assertEquals(false, Caster((1L shl RV_SHIFT_BITS) or RV_TYPE_STRUCT, leafTest, store))
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
