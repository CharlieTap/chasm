package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.gc
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
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
import kotlin.test.assertNotEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CreateExternRefTest {

    @Test
    fun `creates reads converts and releases an extern reference`() {
        val store = publicStore()
        val value = HostValue("Link")
        val reference = createExternRef(store, value).expect("expected extern creation to succeed")

        assertSame(value, reference.hostValue().expect("expected extern observation to succeed"))
        assertSame(value, reference.hostValueAs<HostValue>().expect("expected typed observation to succeed"))
        assertTrue(reference.asValue().expect("expected conversion to succeed") is ReferenceValue.Extern)

        gc(store).expect("expected collection to succeed")
        assertSame(value, reference.hostValue().expect("expected retained extern to survive collection"))

        reference.close()
        reference.close()
        assertTrue(reference.hostValue() is ChasmResult.Error)
        assertTrue(reference.asValue() is ChasmResult.Error)

        gc(store).expect("expected collection to succeed")
    }

    @Test
    fun `null extern has no arena allocation or retained root`() {
        val store = publicStore()
        val reference = createExternRef(store, null).expect("expected null extern creation to succeed")

        assertEquals(null, reference.hostValue().expect("expected null observation to succeed"))
        assertEquals(null, reference.hostValueAs<HostValue>().expect("expected typed null observation to succeed"))
        assertEquals(
            ReferenceValue.Null(AbstractHeapType.Extern),
            reference.asValue().expect("expected null conversion to succeed"),
        )
        reference.close()
        assertTrue(reference.hostValue() is ChasmResult.Error)
    }

    @Test
    fun `typed observation rejects another host value type`() {
        val store = publicStore()
        val reference = createExternRef(store, HostValue("Zelda"))
            .expect("expected extern creation to succeed")

        val result = reference.hostValueAs<String>()

        assertTrue(result is ChasmResult.Error)
        assertTrue(result.error.error.contains("HostValue"))
        assertTrue(result.error.error.contains("String"))
        reference.close()
    }

    @Test
    fun `creating the same object twice creates distinct extern identities`() {
        val store = publicStore()
        val value = HostValue("Epona")
        val first = createExternRef(store, value).expect("expected first extern creation to succeed")
        val second = createExternRef(store, value).expect("expected second extern creation to succeed")

        assertNotEquals(
            first.asValue().expect("expected first conversion to succeed"),
            second.asValue().expect("expected second conversion to succeed"),
        )
        assertSame(value, first.hostValue().expect("expected first observation to succeed"))
        assertSame(value, second.hostValue().expect("expected second observation to succeed"))

        first.close()
        second.close()
    }

    @Test
    fun `store drop invalidates extern handles and close remains safe`() {
        val store = publicStore()
        val reference = createExternRef(store, Any()).expect("expected extern creation to succeed")

        dropStore(store).expect("expected store drop to succeed")

        assertTrue(reference.hostValue() is ChasmResult.Error)
        assertTrue(reference.asValue() is ChasmResult.Error)
        assertTrue(createExternRef(store, "replacement") is ChasmResult.Error)
        reference.close()
        reference.close()
    }

    @Test
    fun `released extern cannot observe a value from a reused arena slot`() {
        val store = publicStore()
        val released = createExternRef(store, "released").expect("expected extern creation to succeed")
        val releasedValue = released.asValue().expect("expected conversion to succeed")
        released.close()
        gc(store).expect("expected collection to succeed")

        val replacement = createExternRef(store, "replacement").expect("expected replacement creation to succeed")

        assertEquals(releasedValue, replacement.asValue().expect("expected replacement conversion to succeed"))
        assertTrue(released.hostValue() is ChasmResult.Error)
        assertEquals("replacement", replacement.hostValue().expect("expected replacement observation to succeed"))
        replacement.close()
    }

    @Test
    fun `globals and tables can retain an extern after its embedding handle closes`() {
        val store = publicStore()
        val hostValue = HostValue("Navi")
        val reference = createExternRef(store, hostValue).expect("expected extern creation to succeed")
        val value = reference.asValue().expect("expected extern conversion to succeed")
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
        gc(store).expect("expected collection to succeed")

        val rawReference = HostExternReference(value.toLongFromBoxed())
        assertSame(hostValue, store.store.heap.value(rawReference))

        val nullExtern = ReferenceValue.Null(AbstractHeapType.Extern)
        writeGlobal(store, global, nullExtern).expect("expected global write to succeed")
        writeTable(store, table, 0, nullExtern).expect("expected table write to succeed")
        gc(store).expect("expected collection to succeed")

        val replacement = createExternRef(store, "replacement").expect("expected replacement creation to succeed")
        assertEquals(value, replacement.asValue().expect("expected replacement conversion to succeed"))
        replacement.close()
    }

    private data class HostValue(val name: String)
}
