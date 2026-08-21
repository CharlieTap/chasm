package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DefaultIssueExceptionTest {

    @Test
    fun `can run a host function that throws an exception and return a chasm error`() {

        val result = testRunner(
            fileName = "default_issue.wasm",
            fileDirectory = FILE_DIR,
            functionName = "create_array",
            arguments = listOf(NumberValue.I32(7)),
        )

        val values = assertIs<ChasmResult.Success<List<*>>>(result).result
        assertEquals(1, values.size)
        assertIs<ReferenceValue.Array>(values.single())
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
