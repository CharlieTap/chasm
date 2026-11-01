package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.invoke
import io.github.charlietap.chasm.runtime.value.NumberValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlin.test.Test
import kotlin.test.assertIs

class DefaultIssueExceptionTest {

    @Test
    fun `can run a host function that throws an exception and return a chasm error`() = integrationTest(FIXTURE) {

        val result = invoke(
            name = "create_array",
            arguments = listOf(NumberValue.I32(7)),
        )

        val values = assertIs<ChasmResult.Success<List<*>>>(result, engine.name()).result
        assertIs<ReferenceValue.Array>(values.single(), engine.name())
    }

    private companion object {
        const val FIXTURE = "default_issue"
    }
}
