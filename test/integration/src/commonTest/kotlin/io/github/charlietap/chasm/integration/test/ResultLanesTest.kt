package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.integration.TestRunner
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals

class ResultLanesTest {

    @Test
    fun `a number and a reference arrive in the order the function returns them`() = integrationTest(FIXTURE) {
        val runner = runner()
        val numberFirst = results(runner, "number_then_reference")
        val referenceFirst = results(runner, "reference_then_number")
        val nullReference = results(runner, "null_reference").single()

        assertEquals(NumberValue.I64(7), numberFirst[0], engine.name())
        assertNotEquals(nullReference, numberFirst[1], engine.name())
        assertNotEquals(nullReference, referenceFirst[0], engine.name())
        assertEquals(NumberValue.I64(7), referenceFirst[1], engine.name())
        assertEquals(numberFirst[1], referenceFirst[0], engine.name())
    }

    private fun results(
        runner: TestRunner,
        function: String,
    ): List<ExecutionValue> {
        return assertIs<ChasmResult.Success<List<ExecutionValue>>>(runner.invoke(function)).result
    }

    private companion object {
        const val FIXTURE = "result_lanes"
    }
}
