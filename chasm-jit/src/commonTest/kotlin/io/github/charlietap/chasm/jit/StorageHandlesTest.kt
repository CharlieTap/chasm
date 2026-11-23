package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.dropInstance
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.memory.sizeMemory
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.growTable
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.sizeTable
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.SharedStatus
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.ValueType
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StorageHandlesTest {

    @Test
    fun referenceCallsOnANumericGlobalAreRejected() {

        val store = store()
        val numeric = global(store, GlobalType(ValueType.Number(NumberType.I32), Mutability.Var), I32(1))

        assertFailsWith<IllegalArgumentException> { readGlobalReference(numeric) }
        assertFailsWith<IllegalArgumentException> { writeGlobalReference(numeric, "value") }
    }

    @Test
    fun numericCallsOnAReferenceGlobalAreRejected() {

        val store = store()
        val reference = global(store, GlobalType(EXTERN_TYPE, Mutability.Var), ReferenceValue.Extern("value"))

        assertFailsWith<IllegalArgumentException> { readGlobal(reference) }
    }

    @Test
    fun handlesOfADroppedInstanceThrow() {

        val store = store()
        val instance = instantiate(store, "exported_storage", emptyList())
        val exports = exports(instance)
        val table = checkNotNull(exports.table("table"))
        val references = checkNotNull(exports.global("refs"))

        dropInstance(instance)

        assertFailsWith<IllegalStateException> { readTable(table, 0) }
        assertFailsWith<IllegalStateException> { readGlobalReference(references) }
    }

    @Test
    fun handlesOfADroppedStoreThrow() {

        val store = store()
        val exports = exports(instantiate(store, "exported_storage", emptyList()))
        val table = checkNotNull(exports.table("table"))
        val count = checkNotNull(exports.global("count"))

        dropStore(store)

        assertFailsWith<IllegalStateException> { readTable(table, 0) }
        assertFailsWith<IllegalStateException> { readGlobal(count) }
    }

    @Test
    fun reexportedHostStorageIsTheSameHandle() {

        val store = store()
        val hostTable = table(store, TableType(AddressType.I32, ReferenceType.RefNull(AbstractHeapType.Func), Limits(2u)), null)
        val hostGlobal = global(store, GlobalType(EXTERN_TYPE, Mutability.Var), ReferenceValue.Extern("value"))
        val imports = listOf(Import("host", "table", hostTable), Import("host", "refs", hostGlobal))

        val exports = exports(instantiate(store, "reexport_storage", imports))
        val table = checkNotNull(exports.table("table"))
        val global = checkNotNull(exports.global("refs"))

        assertEquals(hostTable, table)
        assertEquals(hostTable.hashCode(), table.hashCode())
        assertEquals(hostGlobal, global)
        assertEquals(hostGlobal.hashCode(), global.hashCode())
        writeGlobalReference(global, "written")
        assertEquals("written", readGlobalReference(hostGlobal))
    }

    @Test
    fun handleTypesKeepTheLimitsTheyWereCreatedWith() {

        val store = store()
        val memoryType = MemoryType(AddressType.I32, Limits(1u, 3u), SharedStatus.Unshared)
        val tableType = TableType(AddressType.I32, ReferenceType.RefNull(AbstractHeapType.Func), Limits(2u))
        val hostMemory = memory(store, memoryType)
        val hostTable = table(store, tableType, null)
        val imports = listOf(Import("host", "table", hostTable), Import("host", "refs", referenceGlobal(store)))
        val exported = checkNotNull(exports(instantiate(store, "reexport_storage", imports)).table("table"))

        growMemory(hostMemory, 1)
        growTable(hostTable, 1, null)

        assertEquals(Limits(1u, 3u), hostMemory.type.limits)
        assertEquals(Limits(2u), hostTable.type.limits)
        assertEquals(Limits(2u), exported.type.limits)
        assertEquals(Limits(1u, 3u), memoryType.limits)
        assertEquals(Limits(2u), tableType.limits)
        assertEquals(2, sizeMemory(hostMemory))
        assertEquals(3, sizeTable(hostTable))
    }

    @Test
    fun importsFromAnotherStoreAreRejected() {

        val store = store()
        val foreignTable = table(store(), TableType(AddressType.I32, ReferenceType.RefNull(AbstractHeapType.Func), Limits(2u)), null)
        val module = runBlocking { module(resourceBytes("modules/reexport_storage.wasm")) }.expect("reexport_storage")
        val imports = listOf(Import("host", "table", foreignTable), Import("host", "refs", referenceGlobal(store)))

        val failure = assertFailsWith<IllegalArgumentException> {
            runBlocking { instance(store, module, imports) }
        }

        assertEquals("the import host.table belongs to a different store", failure.message)
    }

    private fun referenceGlobal(store: Store): Global {
        return global(store, GlobalType(EXTERN_TYPE, Mutability.Var), ReferenceValue.Extern("value"))
    }

    private fun instantiate(
        store: Store,
        name: String,
        imports: List<Import>,
    ): Instance {

        val module = runBlocking { module(resourceBytes("modules/$name.wasm")) }.expect(name)

        return runBlocking { instance(store, module, imports) }.expect(name)
    }

    private companion object {
        val EXTERN_TYPE = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Extern))
    }
}
