package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.executor.runtime.value.arrayReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultIssueExceptionTest {

    @Test
    fun `can run a host function that throws an exception and return a chasm error`() {

        val result = testRunner(
            fileName = "default_issue.wasm",
            fileDirectory = FILE_DIR,
            functionName = "create_array",
            arguments = listOf(NumberValue.I32(7)),
        )

        val expected = ChasmResult.Success(
            listOf(
                arrayReferenceValue(
                    arrayAddress(0),
                ),
            ),
        )
        assertEquals(expected, result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
