package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertIs

class RandomGetZeroTest {

    @Test
    fun `can execute _start from random_get_zero successfully`() {

        val byteStream = Resource(FILE_DIR + "random_get_zero.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)
        val config = RuntimeConfig(bytecodeFusion = true)

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
                reference { _ -> listOf(NumberValue.I32(0)) }
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
                reference { _ -> listOf(NumberValue.I32(0)) }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "proc_exit"
                type { params { i32() } }
                reference { values -> throw HostFunctionException("proc_exit: ${values[0]}") }
            }
        }

        val actual = module(reader)
            .flatMap { module -> instance(store, module, imports, config) }
            .flatMap { instance -> invoke(store, instance, "_start") }

        assertIs<ChasmResult.Success<List<ExecutionValue>>>(actual)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
