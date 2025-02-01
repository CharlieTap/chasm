package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertIs

class WehTest {

    @Test
    fun `can instantiate the weh demo`() {

        val byteStream = Resource(FILE_DIR + "weh.wasm").readBytes()
        val reader = FakeSourceReader(byteStream)

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
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "random_get"
                type {
                    params {
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "clock_time_get"
                type {
                    params {
                        i32()
                        i64()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "poll_oneoff"
                type {
                    params {
                        i32()
                        i32()
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "clock_time_get"
                type {
                    params {
                        i32()
                        i64()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "proc_exit"
                type {
                    params {
                        i32()
                    }
                    results { }
                }
                reference { emptyList() }
            }
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
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "clock_time_get"
                type {
                    params {
                        i32()
                        i64()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "args_sizes_get"
                type {
                    params {
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "args_get"
                type {
                    params {
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "fd_prestat_get"
                type {
                    params {
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "fd_prestat_dir_name"
                type {
                    params {
                        i32()
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "path_open"
                type {
                    params {
                        i32()
                        i32()
                        i32()
                        i32()
                        i32()
                        i64()
                        i64()
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "fd_filestat_get"
                type {
                    params {
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "fd_seek"
                type {
                    params {
                        i32()
                        i64()
                        i32()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "fd_pread"
                type {
                    params {
                        i32()
                        i32()
                        i32()
                        i64()
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
            function {
                moduleName = "wasi_snapshot_preview1"
                entityName = "fd_close"
                type {
                    params {
                        i32()
                    }
                    results {
                        i32()
                    }
                }
                reference { emptyList() }
            }
        }

        val actual = module(reader)
            .flatMap { module ->
                instance(store, module, imports)
            }

        assertIs<ChasmResult.Success<List<ExecutionValue>>>(actual)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
