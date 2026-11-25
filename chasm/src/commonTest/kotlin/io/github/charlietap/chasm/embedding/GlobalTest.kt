package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.embedding.value.VectorValue
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.VectorType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GlobalTest {

    @Test
    fun `can allocate a global in the store and return a handle`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = globalType()

        val actual = global(store, type, NumberValue.I32(117))

        assertEquals(type, actual.type)
        assertEquals(globalType(), runtime.globals[0].type)
        assertEquals(117L, runtime.globals[0].value)
    }

    @Test
    fun `a value of another type is rejected before the global is allocated`() {

        val runtime = store()
        val store = interpreterStore(runtime)

        assertFailsWith<IllegalArgumentException> {
            global(store, globalType(valueType = i32ValueType()), NumberValue.I64(117L))
        }
        assertEquals(0, runtime.globals.size)
    }

    @Test
    fun `a v128 global cannot be created from the embedding`() {

        val store = interpreterStore()
        val type = globalType(valueType = ValueType.Vector(VectorType.V128))

        assertFailsWith<IllegalArgumentException> {
            global(store, type, VectorValue.V128(ByteArray(16)))
        }
    }
}
