package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.invoke
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TypeSystemCorrectnessTest {

    @Test
    fun `ref test recognizes a non-null exception reference`() = integrationTest(FIXTURE) {

        val result = invoke(
            name = "test_exception_reference",
        )

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(1))),
            result,
            engine.name(),
        )
    }

    @Test
    fun `ref cast accepts a non-null exception reference`() = integrationTest(FIXTURE) {

        val result = invoke(
            name = "cast_exception_reference",
        )

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(1))),
            result,
            engine.name(),
        )
    }

    private companion object {
        const val FIXTURE = "type_system_exception_cast"
    }
}
