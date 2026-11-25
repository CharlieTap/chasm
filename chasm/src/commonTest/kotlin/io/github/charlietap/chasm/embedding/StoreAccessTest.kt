package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.decodedModule
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.fixture.registerArrayType
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobalReference
import io.github.charlietap.chasm.embedding.memory.copyMemory
import io.github.charlietap.chasm.embedding.memory.readInt
import io.github.charlietap.chasm.embedding.memory.sizeMemory
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.reference.allocateNumericArray
import io.github.charlietap.chasm.embedding.reference.arrayLength
import io.github.charlietap.chasm.embedding.reference.createException
import io.github.charlietap.chasm.embedding.reference.readArrayElement
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.table.copyTable
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.embedding.table.writeTableElements
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class StoreAccessTest {

    @Test
    fun `a host-created memory or table above the store's limit is refused and allocates nothing`() {

        val runtime = store(config = RuntimeConfig(limits = Limits(memory = MemoryLimits(linearMemory = 2.pages, tables = 16.bytes))))
        val store = interpreterStore(runtime)

        assertFailsWith<IllegalArgumentException> { memory(store, memoryType(limits = limits(min = 3u))) }
        assertFailsWith<IllegalArgumentException> {
            table(store, tableType(referenceType = refNullReferenceType(), limits = limits(min = 3u)), null)
        }

        assertEquals(0, runtime.memories.size)
        assertEquals(0, runtime.tables.size)
    }

    @Test
    fun `handles from another store are refused`() {

        val store = interpreterStore()
        val other = interpreterStore()
        val memory = memory(store, memoryType(limits = limits(min = 1u)))
        val foreignMemory = memory(other, memoryType(limits = limits(min = 1u)))
        val table = table(store, tableType(referenceType = refNullReferenceType(), limits = limits(min = 1u)), null)
        val foreignTable = table(other, tableType(referenceType = refNullReferenceType(), limits = limits(min = 1u)), null)
        val foreignFunction = functionReference(function(other, functionType(), HostFunction { _, _ -> }))

        assertFailsWith<IllegalArgumentException> { copyMemory(foreignMemory, 0, memory, 0, 1) }
        assertFailsWith<IllegalArgumentException> { copyTable(foreignTable, 0, table, 0, 1) }
        assertFailsWith<IllegalArgumentException> { writeTable(table, 0, foreignFunction) }
        assertFailsWith<IllegalArgumentException> { writeTableElements(table, 0, arrayOf(foreignFunction)) }
        assertNull(readTable(table, 0))
    }

    @Test
    fun `references from another store are refused`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val otherRuntime = store()
        val other = interpreterStore(otherRuntime)
        val exnref = referenceValueType(refNullReferenceType(AbstractHeapType.Exception))
        val global = global(store, globalType(valueType = exnref), ReferenceValue.Null(AbstractHeapType.Exception))
        val tag = tag(store, tagType(functionType = functionType(params = resultType(listOf(exnref)))))
        val foreignTag = tag(other, tagType())
        val foreignException = createException(foreignTag, LongArray(0), arrayOfNulls(0))
        val foreignArray = allocateNumericArray(other, registerArrayType(otherRuntime, fieldType()), 1, 0L)

        assertFailsWith<IllegalArgumentException> { writeGlobalReference(global, foreignException) }
        assertFailsWith<IllegalArgumentException> { createException(tag, LongArray(1), arrayOf(foreignException)) }
        assertFailsWith<IllegalArgumentException> {
            table(store, tableType(referenceType = refNullReferenceType(AbstractHeapType.Array), limits = limits(min = 1u)), foreignArray)
        }
    }

    @Test
    fun `a dropped store refuses its handles and instances and any instantiation`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        val instance = instantiate(store, INSTANCE_MODULE)
        val prepared = assertNotNull(prepareFunction(instance, "trap").getOrNull())
        val exports = exports(instance)
        val memory = assertNotNull(exports.memory("memory"))
        val global = assertNotNull(exports.global("global"))
        val table = assertNotNull(exports.table("table"))
        val array = allocateNumericArray(store, registerArrayType(runtime, fieldType()), 1, 0L)
        val module = decodedModule(INSTANCE_MODULE)

        dropStore(store)

        assertFailsWith<IllegalStateException> { readInt(memory, 0) }
        assertFailsWith<IllegalStateException> { writeBytes(memory, 0, byteArrayOf(1)) }
        assertFailsWith<IllegalStateException> { sizeMemory(memory) }
        assertFailsWith<IllegalStateException> { readGlobal(global) }
        assertFailsWith<IllegalStateException> { writeTable(table, 0, null) }
        assertFailsWith<IllegalStateException> { arrayLength(array) }
        assertFailsWith<IllegalStateException> { readArrayElement(array, 0) }
        assertEquals(DROPPED, invoke(instance, "trap"))
        assertEquals(DROPPED, prepareFunction(instance, "trap"))
        assertEquals(DROPPED, prepared())
        assertFailsWith<IllegalStateException> { exports(instance) }
        assertFailsWith<IllegalStateException> { dropInstance(instance) }
        assertFailsWith<IllegalStateException> { memory(store, memoryType()) }
        assertFailsWith<IllegalStateException> { global(store, globalType(), NumberValue.I32(0)) }
        assertFailsWith<IllegalStateException> { instance(store, module, emptyList()) }
    }

    @Test
    fun `collect and fuel calls on a dropped store throw illegal state`() {

        val store = interpreterStore(store(config = RuntimeConfig(interrupts = true, limits = Limits(cpu = CpuLimits.Metered(0.fuel)))))

        dropStore(store)

        assertFailsWith<IllegalStateException> { collect(store) }
        assertFailsWith<IllegalStateException> { addFuel(store, 1L) }
        assertFailsWith<IllegalStateException> { resetFuel(store) }
        assertFailsWith<IllegalStateException> { remainingFuel(store) }
        assertFailsWith<IllegalStateException> { interrupt(store) }
    }

    private companion object {
        val DROPPED = ChasmResult.Error(ExecutionError.Rejected(InvocationError.StoreDropped.toString()))
    }
}
