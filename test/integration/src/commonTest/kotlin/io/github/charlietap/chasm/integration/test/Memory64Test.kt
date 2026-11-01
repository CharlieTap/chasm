package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.invoke
import io.github.charlietap.chasm.runtime.error.InstantiationError
import kotlin.test.Test
import kotlin.test.assertEquals

class Memory64Test {

    @Test
    fun `returns an unsupported memory 64 module error when executing`() = integrationTest(FIXTURE) {

        val actual = invoke(
            name = "memory64",
        )

        val expected = ChasmError.ExecutionError(InstantiationError.UnsupportedMemory64Module.toString())
        assertEquals(ChasmResult.Error(expected), actual, engine.name())
    }

    private companion object {
        const val FIXTURE = "memory64"
    }
}
