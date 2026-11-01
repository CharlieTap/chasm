package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.dsl.imports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals

class WehTest {

    @Test
    fun `can run a function from the weh demo`() = integrationTest(FIXTURE) {

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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
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
                reference { _, _ -> }
            }
        }

        val actual = runner(store, imports).invoke(
            name = "dummy",
        )

        assertEquals(ChasmResult.Success(emptyList<ExecutionValue>()), actual, engine.name())
    }

    private companion object {
        const val FIXTURE = "weh"
    }
}
