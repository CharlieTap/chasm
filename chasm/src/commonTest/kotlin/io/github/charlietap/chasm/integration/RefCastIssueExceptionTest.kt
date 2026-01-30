package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertIs

class RefCastIssueExceptionTest {

    @Test
    fun `can run a host function that throws an exception and return a chasm error`() {

        val config = RuntimeConfig(bytecodeFusion = false)
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
                reference { params -> listOf(NumberValue.I32(0)) }
            }
        }

        val result = testRunner(
            store = store,
            fileName = "ref_cast_issue.wasm",
            fileDirectory = FILE_DIR,
            functionName = "_initialize",
            imports = imports,
            config = config,
        )

        print(result)

        assertIs<ChasmResult.Success<Unit>>(result)
    }

    companion object {
        private const val FILE_DIR = "integration/"
    }
}
