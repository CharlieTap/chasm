package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.gc
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.shapes.hostValueAs
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.host.HostExternReference
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CreateExternRefTest {

    @Test
    fun `creates reads converts and releases an extern reference`() {

        val store = publicStore()
        val value = HostValue("Link")
        val reference = createExternRef(store, value)

        assertSame(value, reference.hostValue())
        assertSame(value, reference.hostValueAs<HostValue>())
        assertIs<ReferenceValue.Extern>(reference.asValue())

        gc(store)
        assertSame(value, reference.hostValue())

        reference.close()
        reference.close()
        assertFailsWith<IllegalStateException> { reference.hostValue() }
        assertFailsWith<IllegalStateException> { reference.asValue() }

        gc(store)
    }

    @Test
    fun `null extern has no arena allocation or retained root`() {

        val store = publicStore()
        val reference = createExternRef(store, null)

        assertEquals(null, reference.hostValue())
        assertEquals(null, reference.hostValueAs<HostValue>())
        assertEquals(ReferenceValue.Null(AbstractHeapType.Extern), reference.asValue())

        reference.close()
        assertFailsWith<IllegalStateException> { reference.hostValue() }
    }

    @Test
    fun `typed observation rejects another host value type`() {

        val store = publicStore()
        val reference = createExternRef(store, HostValue("Zelda"))

        val failure = assertFailsWith<IllegalArgumentException> { reference.hostValueAs<String>() }

        assertTrue(failure.message.orEmpty().contains("HostValue"))
        assertTrue(failure.message.orEmpty().contains("String"))
        reference.close()
    }

    @Test
    fun `creating the same object twice creates distinct extern identities`() {

        val store = publicStore()
        val value = HostValue("Epona")
        val first = createExternRef(store, value)
        val second = createExternRef(store, value)

        assertNotEquals(first.asValue(), second.asValue())
        assertSame(value, first.hostValue())
        assertSame(value, second.hostValue())

        first.close()
        second.close()
    }

    @Test
    fun `store drop invalidates extern handles and close remains safe`() {

        val store = publicStore()
        val reference = createExternRef(store, Any())

        dropStore(store)

        assertFailsWith<IllegalStateException> { reference.hostValue() }
        assertFailsWith<IllegalStateException> { reference.asValue() }
        assertFailsWith<IllegalStateException> { createExternRef(store, "replacement") }
        reference.close()
        reference.close()
    }

    @Test
    fun `released extern cannot observe a value from a reused arena slot`() {

        val store = publicStore()
        val released = createExternRef(store, "released")
        val releasedValue = released.asValue()
        released.close()
        gc(store)

        val replacement = createExternRef(store, "replacement")

        assertEquals(releasedValue, replacement.asValue())
        assertFailsWith<IllegalStateException> { released.hostValue() }
        assertEquals("replacement", replacement.hostValue())
        replacement.close()
    }

    @Test
    fun `globals and tables can retain an extern after its embedding handle closes`() {

        val store = publicStore()
        val hostValue = HostValue("Navi")
        val reference = createExternRef(store, hostValue)
        val value = reference.asValue()
        val referenceType = ReferenceType.RefNull(AbstractHeapType.Extern)
        val global = global(
            store = store,
            type = GlobalType(ValueType.Reference(referenceType), Mutability.Var),
            value = value,
        )
        val table = table(
            store = store,
            type = TableType(AddressType.I32, referenceType, Limits(1uL)),
            value = value,
        )

        reference.close()
        gc(store)

        val rawReference = HostExternReference(value.toLongFromBoxed())
        assertSame(hostValue, store.store.heap.value(rawReference))

        val nullExtern = ReferenceValue.Null(AbstractHeapType.Extern)
        writeGlobal(store, global, nullExtern)
        writeTable(store, table, 0, nullExtern)
        gc(store)

        val replacement = createExternRef(store, "replacement")
        assertEquals(value, replacement.asValue())
        replacement.close()
    }

    private data class HostValue(val name: String)
}
