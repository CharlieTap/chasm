package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.functionReference
import io.github.charlietap.chasm.embedding.global
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class GlobalCallsTest {

    @Test
    fun `can read a value from a global`() {

        val global = global(interpreterStore(), globalType(), NumberValue.I32(117))

        assertEquals(117L, readGlobal(global))
    }

    @Test
    fun `can write a value to a global`() {

        val runtime = store()
        val global = global(interpreterStore(runtime), globalType(mutability = varMutability()), NumberValue.I32(0))

        writeGlobal(global, 117L)

        assertEquals(117L, runtime.globals[0].value)
        assertEquals(117L, readGlobal(global))
    }

    @Test
    fun `writing an immutable global throws illegal argument`() {

        val runtime = store()
        val global = global(interpreterStore(runtime), globalType(mutability = constMutability()), NumberValue.I32(3))

        assertFailsWith<IllegalArgumentException> {
            writeGlobal(global, 117L)
        }
        assertEquals(3L, runtime.globals[0].value)
    }

    @Test
    fun `reading or writing a global through the wrong lane throws illegal argument`() {

        val store = interpreterStore()
        val number = global(store, globalType(valueType = i32ValueType()), NumberValue.I32(3))
        val funcref = referenceValueType(refNullReferenceType(AbstractHeapType.Func))
        val reference = global(store, globalType(valueType = funcref), ReferenceValue.Null(AbstractHeapType.Func))

        assertFailsWith<IllegalArgumentException> { readGlobalReference(number) }
        assertFailsWith<IllegalArgumentException> { writeGlobalReference(number, null) }
        assertFailsWith<IllegalArgumentException> { readGlobal(reference) }
        assertFailsWith<IllegalArgumentException> { writeGlobal(reference, 1L) }
        assertEquals(3L, readGlobal(number))
    }

    @Test
    fun `can read and write a reference global`() {

        val store = interpreterStore()
        val funcref = referenceValueType(refNullReferenceType(AbstractHeapType.Func))
        val global = global(store, globalType(valueType = funcref), ReferenceValue.Null(AbstractHeapType.Func))
        val reference = functionReference(function(store, functionType(), HostFunction { _, _ -> }))

        assertNull(readGlobalReference(global))

        writeGlobalReference(global, reference)

        assertEquals(reference, readGlobalReference(global))
    }

    @Test
    fun `a reference of another type is refused by a reference global`() {

        val store = interpreterStore()
        val funcref = referenceValueType(refNullReferenceType(AbstractHeapType.Func))
        val global = global(store, globalType(valueType = funcref), ReferenceValue.Null(AbstractHeapType.Func))

        assertFailsWith<IllegalArgumentException> {
            writeGlobalReference(global, "not a function")
        }
        assertNull(readGlobalReference(global))
    }
}
