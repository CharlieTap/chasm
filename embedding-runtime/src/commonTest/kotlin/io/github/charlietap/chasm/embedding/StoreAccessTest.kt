package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.internal.instantiate
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.memory.readInt
import io.github.charlietap.chasm.embedding.memory.sizeMemory
import io.github.charlietap.chasm.embedding.memory.writeBytes
import io.github.charlietap.chasm.embedding.reference.readArrayLength
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.PreparedFunction
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail

class StoreAccessTest {

    @Test
    fun `handles from another store are refused`() {
        val store = publicStore()
        val other = publicStore()
        val exportedFunction = functionExternalValue(functionAddress(0))
        val instance = publicInstance(
            store = store.store,
            moduleInstance = moduleInstance(exports = mutableListOf(exportInstance(nameValue("run"), exportedFunction))),
        )

        assertEquals(FOREIGN, readInt(store, publicMemory(store = other.store), 0))
        assertEquals(FOREIGN, writeBytes(store, publicMemory(store = other.store), 0, byteArrayOf(1)))
        assertEquals(FOREIGN, growMemory(store, publicMemory(store = other.store), 1))
        assertEquals(FOREIGN, sizeMemory(store, publicMemory(store = other.store)))
        assertEquals(FOREIGN, readGlobal(store, publicGlobal(store = other.store)))
        assertEquals(FOREIGN, writeGlobal(store, publicGlobal(store = other.store), i32(1)))
        assertEquals(FOREIGN, readTable(store, publicTable(store = other.store), 0))
        assertEquals(FOREIGN, writeTable(store, publicTable(store = other.store), 0, nullReferenceValue()))
        assertEquals(FOREIGN, invoke(store, publicInstance(store = other.store, moduleInstance = instance.instance), "run"))
        assertEquals(FOREIGN, invoke(store, instance, publicFunction(exportedFunction, other.store)))
        assertEquals(FOREIGN, prepareFunction(store, publicInstance(store = other.store, moduleInstance = instance.instance), "run"))
    }

    @Test
    fun `a dropped store refuses its handles and instances and any instantiation`() {
        val store = publicStore(store(data = mutableListOf(dataInstance())))
        val exportedFunction = functionExternalValue(functionAddress(0))
        val instance = publicInstance(
            store = store.store,
            moduleInstance = moduleInstance(
                exports = mutableListOf(exportInstance(nameValue("run"), exportedFunction)),
                dataAddresses = mutableListOf(dataAddress(0)),
            ),
        )
        val memory = publicMemory(store = store.store)
        val global = publicGlobal(store = store.store)
        val table = publicTable(store = store.store)

        dropStore(store)

        assertEquals(DROPPED, readInt(store, memory, 0))
        assertEquals(DROPPED, writeBytes(store, memory, 0, byteArrayOf(1)))
        assertEquals(DROPPED, sizeMemory(store, memory))
        assertEquals(DROPPED, readGlobal(store, global))
        assertEquals(DROPPED, writeTable(store, table, 0, nullReferenceValue()))
        assertEquals(DROPPED, readArrayLength(store, arrayReferenceValue()))
        assertEquals(DROPPED, invoke(store, instance, "run"))
        assertEquals(DROPPED, prepareFunction(store, instance, "run"))
        assertEquals(DROPPED, dropInstance(store, instance))
        assertEquals(
            DROPPED,
            instantiate(store, emptyList(), RuntimeConfig(), { null }) { _, _, _ -> fail("a dropped store is never instantiated into") },
        )
    }

    @Test
    fun `an engine failure is returned as an error by invoke and by a prepared function`() {
        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val failure = InvocationException(InvocationError.FunctionLookupFailed(functionAddress(0)))
        val instance = publicInstance(
            store = store.store,
            moduleInstance = moduleInstance(
                exports = mutableListOf(exportInstance(nameValue("run"), functionExternalValue(functionAddress(0)))),
            ),
        )
        val expected = ChasmResult.Error(ChasmError.ExecutionError(failure.error.toString()))
        val prepared = assertIs<ChasmResult.Success<PreparedFunction>>(prepareFunction(store, instance, "run")).result
        store.store.functions[0] = hostFunctionInstance()

        assertEquals(expected, invoke(store, instance, "run", emptyList(), invoker = { _, _, _, _, _ -> throw failure }))
        assertEquals(expected, prepared())
    }

    private companion object {
        val FOREIGN = ChasmResult.Error(ChasmError.ExecutionError(InvocationError.ForeignHandle.toString()))
        val DROPPED = ChasmResult.Error(ChasmError.ExecutionError(InvocationError.StoreDropped.toString()))
    }
}
