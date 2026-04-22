package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinNullableLengthComparisonTest {

    // Regression test for https://github.com/CharlieTap/chasm/issues/118
    @Test
    fun `can invoke checkLen from the Kotlin nullable length comparison artifact`() {

        val config = RuntimeConfig(bytecodeFusion = true)
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
                reference { _ -> listOf(NumberValue.I32(0)) }
            }
        }

        val result = testRunner(
            store = store,
            fileName = "kotlin_nullable_length_comparison.wasm",
            fileDirectory = FILE_DIR,
            functionName = "checkLen",
            imports = imports,
            config = config,
        )

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(1))),
            result,
        )
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
