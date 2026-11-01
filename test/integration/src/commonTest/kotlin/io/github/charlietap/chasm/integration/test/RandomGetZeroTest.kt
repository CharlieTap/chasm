package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertIs

class RandomGetZeroTest {

    @Test
    fun `can execute _start from random_get_zero successfully`() = integrationTest(FIXTURE) {

        val store = store()
        val imports = imports(store) {
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "fd_write"
                type {
                    params {
                        i32()
                        i32()
                        i32()
                        i32()
                    }
                    results { i32() }
                }
                reference { _, results -> results.writeI32(0, 0) }
            }
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
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "proc_exit"
                type { params { i32() } }
                reference { parameters, _ ->
                    throw HostFunctionException("proc_exit: ${parameters.readI32(0)}")
                }
            }
        }

        val actual = runner(store, imports, RuntimeConfig()).invoke(
            name = "_start",
        )

        assertIs<ChasmResult.Success<List<ExecutionValue>>>(actual, engine.name())
    }

    private companion object {
        const val FIXTURE = "random_get_zero"
    }
}
