package io.github.charlietap.chasm.tools.aot

import io.github.charlietap.chasm.corpus.ChasmCorpusRunner
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.corpus.lib.CorpusFileReader
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.Fixture
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CorpusHostStubsTest {
    @Test
    fun `explicit WASI function stubs run without an unrelated host configuration`() {
        val fixture = Json.decodeFromString<Fixture>(
            """
            {
              "name": "wasi-stub", "path": "wasi-stub.wasm",
              "imports": [{"module": "wasi_snapshot_preview1", "name": "random_get", "kind": "function",
                           "stub": {"returns": [{"type": "i32", "value": "17"}]}}],
              "tests": [{"steps": [{"type": "function.invoke", "function": "sample", "params": [],
                                     "results": [{"type": "i32", "value": "17"}]}]}]
            }
            """.trimIndent(),
        )
        val stores = mutableListOf<Store>()
        val runner = ChasmCorpusRunner(
            object : CorpusFileReader {
                override fun readText(path: String): String = error("No text reads expected")

                override fun readBytes(path: String): ByteArray = checkNotNull(javaClass.getResourceAsStream("/wasi-stub.wasm")).use { it.readBytes() }
            },
            storeFactory = { store().also(stores::add) },
        )
        try {
            assertEquals(CorpusResult.Success, runner.execute("unused", fixture, CorpusPhase.INVOCATION).result)
            val missingStub = fixture.copy(imports = fixture.imports.map { it.copy(stub = null) })
            assertIs<CorpusResult.Skipped>(runner.execute("unused", missingStub, CorpusPhase.INVOCATION).result)
        } finally {
            stores.forEach(::dropStore)
        }
    }
}
