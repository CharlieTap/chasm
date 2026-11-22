package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.constMutability
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WriteGlobalTest {

    @Test
    fun `can write a value to a global`() {

        val value = NumberValue.I32(117)
        val instance = globalInstance()
        val store = publicStore(store(globals = mutableListOf(instance)))
        val address = globalAddress(0)
        val global = Global(globalExternalValue(address), store.store)

        writeGlobal(
            store = store,
            global = global,
            value = value,
        )

        assertEquals(117, store.store.globals[0].value)
    }

    @Test
    fun `writing an immutable global throws illegal argument`() {

        val instance = globalInstance(type = globalType(mutability = constMutability()), value = 3L)
        val store = publicStore(store(globals = mutableListOf(instance)))
        val global = Global(globalExternalValue(globalAddress(0)), store.store)

        assertFailsWith<IllegalArgumentException> {
            writeGlobal(store, global, NumberValue.I32(117))
        }
        assertEquals(3L, store.store.globals[0].value)
    }

    @Test
    fun `writing a value of the wrong type throws illegal argument`() {

        val instance = globalInstance(type = globalType(valueType = i32ValueType()), value = 3L)
        val store = publicStore(store(globals = mutableListOf(instance)))
        val global = Global(globalExternalValue(globalAddress(0)), store.store)

        assertFailsWith<IllegalArgumentException> {
            writeGlobal(store, global, NumberValue.I64(117L))
        }
        assertEquals(3L, store.store.globals[0].value)
    }
}
