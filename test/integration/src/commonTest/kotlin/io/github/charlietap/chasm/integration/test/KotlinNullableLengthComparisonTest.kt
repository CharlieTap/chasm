package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinNullableLengthComparisonTest {

    // Regression test for https://github.com/CharlieTap/chasm/issues/118
    @Test
    fun `can invoke checkLen from the Kotlin nullable length comparison artifact`() = integrationTest(FIXTURE) {

        val store = store()
        val imports = imports(store) {
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "random_get"
                type {
                    params {
                        i32()
                        i32()
                    }
                    results { i32() }
                }
                reference { _, results -> results.writeI32(0, 0) }
            }
        }

        val result = runner(store, imports).invoke(
            name = "checkLen",
        )

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(1))),
            result,
            engine.name(),
        )
    }

    private companion object {
        const val FIXTURE = "kotlin_nullable_length_comparison"
    }
}
