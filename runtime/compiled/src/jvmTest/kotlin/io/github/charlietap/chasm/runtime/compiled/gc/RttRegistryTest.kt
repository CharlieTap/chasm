package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.immutableFieldType
import io.github.charlietap.chasm.fixture.type.openSubType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.structCompositeType
import io.github.charlietap.chasm.fixture.type.structType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class RttRegistryTest {

    @Test
    fun `equal rec groups from two modules get one RTT`() {

        val store = RttStoreFixture()
        val first = store.register(listOf(struct(i32ValueType(), typeIndex = 0)), listOf(POINT_KEY))
        val second = store.register(
            listOf(definedType(functionRecursiveType(), typeIndex = 0), struct(i32ValueType(), typeIndex = 1)),
            listOf(FUNCTION_KEY, POINT_KEY),
        )

        assertSame(first[0], second[1])
    }

    @Test
    fun `a key collision with different definitions gets two RTTs`() {

        val store = RttStoreFixture()
        val first = store.register(listOf(struct(i32ValueType(), typeIndex = 0)), listOf(POINT_KEY))
        val second = store.register(listOf(struct(i64ValueType(), typeIndex = 0)), listOf(POINT_KEY))

        assertNotSame(first[0], second[0])
        assertNotEquals(first[0].id, second[0].id)
    }

    @Test
    fun `an RTT's supertypes end with itself at its depth`() {

        val store = RttStoreFixture()
        val rtts = store.register(
            listOf(
                definedType(recursiveType(listOf(openSubType(compositeType = point(i32ValueType()))))),
                definedType(recursiveType(listOf(openSubType(listOf(ConcreteHeapType.TypeIndex(0)), point(i32ValueType())))), typeIndex = 1),
                definedType(recursiveType(listOf(finalSubType(listOf(ConcreteHeapType.TypeIndex(1)), point(i32ValueType())))), typeIndex = 2),
            ),
            listOf(BASE_KEY, MIDDLE_KEY, LEAF_KEY),
        )
        val (base, middle, leaf) = rtts

        assertEquals(listOf(0, 1, 2), rtts.map { rtt -> rtt.depth })
        assertEquals(listOf(base), base.supertypes.toList())
        assertEquals(listOf(base, middle), middle.supertypes.toList())
        assertEquals(listOf(base, middle, leaf), leaf.supertypes.toList())
    }

    @Test
    fun `two stores get different RTTs for one type`() {

        val first = RttStoreFixture()
        val second = RttStoreFixture()
        val types = listOf(struct(i32ValueType(), typeIndex = 0))

        val firstRtt = first.register(types, listOf(POINT_KEY)).single()
        val secondRtt = second.register(types, listOf(POINT_KEY)).single()

        assertNotSame(firstRtt, secondRtt)
        assertSame(first.managedStore.engine, firstRtt.engine)
        assertSame(second.managedStore.engine, secondRtt.engine)
    }

    @Test
    fun `an RTT holds its type's key interned`() {

        val store = RttStoreFixture()
        val key = String(POINT_KEY.toCharArray())

        val rtt = store.register(listOf(struct(i32ValueType(), typeIndex = 0)), listOf(key)).single()

        assertSame(POINT_KEY, rtt.key)
    }

    private fun struct(
        field: ValueType,
        typeIndex: Int,
    ): DefinedType {
        return definedType(recursiveType(listOf(SubType.Final(emptyList(), point(field)))), typeIndex = typeIndex)
    }

    private fun point(field: ValueType): CompositeType {
        return structCompositeType(structType(listOf(immutableFieldType(valueStorageType(field)))))
    }

    private class RttStoreFixture(
        private val store: Store = store(),
    ) {

        val managedStore = ManagedStore(store) { managedStore -> RttTypes(managedStore, RttRegistry(managedStore)) }
            .apply { engine = fakeEngine() }

        fun register(
            types: List<DefinedType>,
            keys: List<String>,
        ): List<Rtt> {

            val registry = (managedStore.types as RttTypes).registry

            return registry.register(types, keys, store.heap.registerRuntimeTypes(types)) { null }
        }
    }

    private companion object {
        const val POINT_KEY = "0123456789abcdef0123456789abcdef_0"
        const val FUNCTION_KEY = "fedcba9876543210fedcba9876543210_0"
        const val BASE_KEY = "00000000000000000000000000000001_0"
        const val MIDDLE_KEY = "00000000000000000000000000000002_0"
        const val LEAF_KEY = "00000000000000000000000000000003_0"
    }
}
