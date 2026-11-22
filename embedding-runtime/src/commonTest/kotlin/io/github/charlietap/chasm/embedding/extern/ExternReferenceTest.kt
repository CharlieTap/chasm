package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.runtime.store
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
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class ExternReferenceTest {

    @Test
    fun `a host object held only by a global or a table survives a collection as the same object`() {

        val store = interpreterStore()
        val hostValue = HostValue("Navi")
        val global = externGlobal(store, hostValue)
        val table = externTable(store, 1u)
        writeTable(table, 0, hostValue)

        collect(store)

        assertSame(hostValue, readGlobalReference(global))
        assertSame(hostValue, readTable(table, 0))
    }

    @Test
    fun `the same host object passed twice takes two slots and comes back as that object both times`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val hostValue = HostValue("Epona")
        val table = externTable(store, 2u)

        writeTable(table, 0, hostValue)
        writeTable(table, 1, hostValue)

        val elements = runtime.tables.single().elements
        assertNotEquals(elements[0], elements[1])
        assertSame(hostValue, readTable(table, 0))
        assertSame(hostValue, readTable(table, 1))
    }

    @Test
    fun `a host object written to an anyref global survives a collection as the same object`() {

        val store = interpreterStore()
        val hostValue = HostValue("Impa")
        val anyref = ReferenceType.RefNull(AbstractHeapType.Any)
        val global = global(store, GlobalType(ValueType.Reference(anyref), Mutability.Var), ReferenceValue.Null(AbstractHeapType.Any))

        writeGlobalReference(global, hostValue)
        collect(store)

        assertSame(hostValue, readGlobalReference(global))
    }

    @Test
    fun `a null extern is stored without a slot and reads back as null`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val table = externTable(store, 1u)
        writeTable(table, 0, HostValue("Zelda"))

        writeTable(table, 0, null)

        assertNull(readTable(table, 0))
        collect(store)
        assertNull(readTable(table, 0))
    }

    @Test
    fun `a slot the guest released is reused without exposing its old object`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val table = externTable(store, 2u)
        writeTable(table, 0, "released")
        val releasedWord = runtime.tables.single().elements[0]
        writeTable(table, 0, null)

        collect(store)
        writeTable(table, 1, "replacement")

        assertEquals(releasedWord, runtime.tables.single().elements[1])
        assertNull(readTable(table, 0))
        assertEquals("replacement", readTable(table, 1))
    }

    @Test
    fun `a global releases its host object once the guest overwrites it`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val global = externGlobal(store, HostValue("Link"))
        val globalWord = runtime.globals.single().value

        writeGlobalReference(global, null)
        collect(store)
        writeGlobalReference(global, "replacement")

        assertEquals(globalWord, runtime.globals.single().value)
        assertEquals("replacement", readGlobalReference(global))
    }

    @Test
    fun `store drop invalidates guest held externs and leaves host objects alone`() {

        val store = interpreterStore()
        val hostValue = HostValue("Saria")
        val global = externGlobal(store, hostValue)

        dropStore(store)

        assertFailsWith<IllegalStateException> { readGlobalReference(global) }
        assertFailsWith<IllegalStateException> { externTable(store, 1u) }
        assertEquals("Saria", hostValue.name)
    }

    private fun externGlobal(
        store: Store,
        value: Any,
    ): Global {
        return global(
            store = store,
            type = GlobalType(ValueType.Reference(EXTERNREF), Mutability.Var),
            value = ReferenceValue.Extern(value),
        )
    }

    private fun externTable(
        store: Store,
        size: ULong,
    ): Table {
        return table(
            store = store,
            type = TableType(AddressType.I32, EXTERNREF, Limits(size)),
            initial = null,
        )
    }

    private data class HostValue(val name: String)

    private companion object {
        val EXTERNREF = ReferenceType.RefNull(AbstractHeapType.Extern)
    }
}
