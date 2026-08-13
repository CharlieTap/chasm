package io.github.charlietap.chasm.gradle

import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.gradle.fixture.function
import io.github.charlietap.chasm.gradle.fixture.functionParameter
import io.github.charlietap.chasm.gradle.fixture.functionProxy
import io.github.charlietap.chasm.gradle.fixture.functionReturn
import io.github.charlietap.chasm.gradle.fixture.integerScalarType
import io.github.charlietap.chasm.gradle.fixture.wasmInterface
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertFalse

class WasmInterfaceGeneratorTest {

    @Test
    fun `implementation prepares exports once and invokes the prepared function`() {
        val spec = WasmInterfaceGenerator()(
            interfaceVisibility = TypeVisibility.PUBLIC,
            implementationVisibility = TypeVisibility.PUBLIC,
            wasmInterface = wasmInterface(
                interfaceName = "AnswerService",
                packageName = "com.example",
                functions = listOf(
                    function(
                        name = "answer",
                        params = listOf(functionParameter("input", integerScalarType())),
                        returns = functionReturn(integerScalarType()),
                        resultTypes = listOf(i32ValueType()),
                        implementation = functionProxy("answer_export"),
                    ),
                ),
            ),
        ).last().toString()

        assertContains(spec, "private val answerPreparedFunction: PreparedFunction")
        assertContains(spec, "virtualMachine.prepareFunction(")
        assertContains(spec, "\"answer_export\"")
        assertContains(spec, "answerPreparedFunction(args)")
        assertFalse(spec.contains("functionInvokeTyped("))
    }
}
