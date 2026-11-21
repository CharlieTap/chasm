package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.dropInstance
import io.github.charlietap.chasm.embedding.compiled.dropStore
import io.github.charlietap.chasm.embedding.compiled.exports
import io.github.charlietap.chasm.embedding.compiled.global
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.readGlobal
import io.github.charlietap.chasm.embedding.compiled.readGlobalReference
import io.github.charlietap.chasm.embedding.compiled.readTable
import io.github.charlietap.chasm.embedding.compiled.table
import io.github.charlietap.chasm.embedding.compiled.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.compiled.value.ReferenceValue
import io.github.charlietap.chasm.embedding.compiled.writeGlobalReference
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
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
