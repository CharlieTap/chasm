package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import kotlin.test.Test
import kotlin.test.assertIs

class RefCastIssueExceptionTest {

    @Test
    fun `can initialize ref cast issue corpus`() = integrationTest(FIXTURE) {

        val runner = runner { store ->
            imports(store) {
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
        }

        val result = runner.invoke(
            name = "_initialize",
        )

        assertIs<ChasmResult.Success<Unit>>(result, engine.name())
    }

    private companion object {
        const val FIXTURE = "ref_cast_issue"
    }
}
