package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.internal.NumericHostCall
import io.github.charlietap.chasm.embedding.internal.ReferenceHostCall
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FunctionTest {

    @Test
    fun `can allocate a function in the store and return a handle`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = functionType()

        val actual = function(store, type, HostFunction { _, _ -> })

        assertEquals(type, actual.type)
        assertEquals(type, runtime.functions.single().functionType)
    }

    @Test
    fun `a host function without references is installed without a reference lane`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )

        function(store, type, HostFunction { _, _ -> })

        val instance = assertIs<FunctionInstance.HostFunction>(runtime.functions.single())
        assertIs<NumericHostCall>(instance.function)
    }

    @Test
    fun `a host function with a reference parameter or result is installed with a reference lane`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val externref = referenceValueType(refNullReferenceType(AbstractHeapType.Extern))
        val types = listOf(
            functionType(params = resultType(listOf(externref))),
            functionType(results = resultType(listOf(externref))),
        )

        types.forEach { type ->
            function(store, type, HostFunction { _, _ -> })
        }

        runtime.functions.forEach { function ->
            val instance = assertIs<FunctionInstance.HostFunction>(function)
            assertIs<ReferenceHostCall>(instance.function)
        }
    }
}
