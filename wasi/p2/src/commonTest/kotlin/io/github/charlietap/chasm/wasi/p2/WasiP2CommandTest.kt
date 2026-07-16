package io.github.charlietap.chasm.wasi.p2

import io.github.charlietap.chasm.embedding.resourceValue
import io.github.charlietap.chasm.embedding.shapes.ComponentHostFunctionContext
import io.github.charlietap.chasm.embedding.shapes.ComponentImport
import io.github.charlietap.chasm.embedding.shapes.ComponentImportable
import io.github.charlietap.chasm.embedding.shapes.ComponentResourceType
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class WasiP2CommandTest {

    @Test
    fun `bounds random requests with the configured limit`() {
        val requests = mutableListOf<Int>()
        val harness = CommandHarness(
            config = WasiP2CommandConfig(maxRandomBytes = 4),
            entropy = WasiP2Entropy { size ->
                requests += size
                ByteArray(size) { index -> index.toByte() }
            },
        )

        val result = harness.randomBytes(4)
        val bytes = (result.single() as ComponentValue.ByteList).bytes

        assertContentEquals(byteArrayOf(0, 1, 2, 3), bytes)
        assertFailsWith<HostFunctionException> {
            harness.randomBytes(ULong.MAX_VALUE)
        }
        assertEquals(listOf(4), requests)
    }

    @Test
    fun `write requires and consumes the configured check-write permit`() {
        val sink = RecordingSink()
        val harness = CommandHarness(
            sink = sink,
            config = WasiP2CommandConfig(writePermitBytes = 3),
        )

        assertEquals(checkWriteResult(3), harness.checkWrite())
        assertFailsWith<HostFunctionException> {
            harness.write(byteArrayOf(1, 2, 3, 4))
        }
        assertEquals(OK_RESULT, harness.write(byteArrayOf(1, 2, 3)))
        assertFailsWith<HostFunctionException> {
            harness.write(byteArrayOf(4))
        }

        assertEquals(1, sink.writes.size)
        assertContentEquals(byteArrayOf(1, 2, 3), sink.writes.single())
    }

    @Test
    fun `blocking write sends the bounded payload once and flushes`() {
        val sink = RecordingSink()
        val harness = CommandHarness(
            sink = sink,
            config = WasiP2CommandConfig(writePermitBytes = 2),
        )
        val bytes = byteArrayOf(1, 2, 3, 4, 5)

        assertEquals(OK_RESULT, harness.blockingWriteAndFlush(bytes))

        assertEquals(1, sink.writes.size)
        assertContentEquals(bytes, sink.writes.single())
        assertEquals(1, sink.flushCount)
    }

    @Test
    fun `blocking write accepts 4096 bytes and traps above the limit`() {
        val sink = RecordingSink()
        val harness = CommandHarness(sink = sink)

        assertEquals(OK_RESULT, harness.blockingWriteAndFlush(ByteArray(4096)))
        assertFailsWith<HostFunctionException> {
            harness.blockingWriteAndFlush(ByteArray(4097))
        }

        assertEquals(listOf(4096), sink.writes.map(ByteArray::size))
        assertEquals(1, sink.flushCount)
    }

    @Test
    fun `maps an expected write failure to last-operation-failed and closes the stream`() {
        val failure = WasiP2ByteSinkFailure("write failed")
        val sink = RecordingSink(writeFailure = failure)
        val harness = CommandHarness(sink = sink)

        harness.checkWrite()
        val result = harness.write(byteArrayOf(1))

        harness.assertLastOperationFailed(result, failure)
        assertEquals(CLOSED_STREAM_RESULT, harness.checkWrite())
        assertEquals(CLOSED_STREAM_RESULT, harness.blockingFlush())
    }

    @Test
    fun `blocking flush invokes the sink and maps an expected failure`() {
        val failure = WasiP2ByteSinkFailure("flush failed")
        val sink = RecordingSink(flushFailure = failure)
        val harness = CommandHarness(sink = sink)

        val result = harness.blockingFlush()

        assertEquals(1, sink.flushCount)
        harness.assertLastOperationFailed(result, failure)
        assertEquals(CLOSED_STREAM_RESULT, harness.checkWrite())
    }
}

private class CommandHarness(
    sink: WasiP2ByteSink = RecordingSink(),
    config: WasiP2CommandConfig = WasiP2CommandConfig(),
    entropy: WasiP2Entropy = WasiP2Entropy(::ByteArray),
) {
    private val store = store()
    private val context = ComponentHostFunctionContext(store)
    private val imports = wasiP2CommandImports(store, sink, RecordingSink(), entropy, config)
    private val outputStream = invoke(WASI_CLI_STDOUT, "get-stdout").single() as ComponentValue.Resource.Own
    private val errorType = resourceType(WASI_IO_ERROR, "error")

    fun checkWrite(): List<ComponentValue> = invoke(
        WASI_IO_STREAMS,
        "[method]output-stream.check-write",
        listOf(outputStream),
    )

    fun write(bytes: ByteArray): List<ComponentValue> = invoke(
        WASI_IO_STREAMS,
        "[method]output-stream.write",
        listOf(outputStream, bytes.componentList()),
    )

    fun blockingWriteAndFlush(bytes: ByteArray): List<ComponentValue> = invoke(
        WASI_IO_STREAMS,
        "[method]output-stream.blocking-write-and-flush",
        listOf(outputStream, bytes.componentList()),
    )

    fun blockingFlush(): List<ComponentValue> = invoke(
        WASI_IO_STREAMS,
        "[method]output-stream.blocking-flush",
        listOf(outputStream),
    )

    fun randomBytes(size: ULong): List<ComponentValue> = invoke(
        WASI_RANDOM_RANDOM,
        "get-random-bytes",
        listOf(ComponentValue.U64(size)),
    )

    fun randomBytes(size: Int): List<ComponentValue> = randomBytes(size.toULong())

    fun assertLastOperationFailed(
        result: List<ComponentValue>,
        failure: WasiP2ByteSinkFailure,
    ) {
        val error = result.single() as ComponentValue.Result.Error
        val streamError = error.value as ComponentValue.Variant
        val resource = streamError.value as ComponentValue.Resource.Own

        assertEquals(0, streamError.caseIndex)
        assertSame(failure, with(context) { resourceValue(errorType, resource) })
    }

    private fun invoke(
        instance: String,
        function: String,
        arguments: List<ComponentValue> = emptyList(),
    ): List<ComponentValue> {
        val import = import(instance, function).value as ComponentImportable.Function
        return import.function(context, arguments)
    }

    private fun resourceType(
        instance: String,
        resource: String,
    ): ComponentResourceType {
        val import = import(instance, resource).value as ComponentImportable.ResourceType
        return import.type
    }

    private fun import(
        instance: String,
        name: String,
    ): ComponentImport {
        val instanceImport = imports.single { import -> import.name == instance }.value as ComponentImportable.Instance
        return instanceImport.imports.single { import -> import.name == name }
    }
}

private class RecordingSink(
    private val writeFailure: WasiP2ByteSinkFailure? = null,
    private val flushFailure: WasiP2ByteSinkFailure? = null,
) : WasiP2ByteSink {
    val writes = mutableListOf<ByteArray>()
    var flushCount = 0
        private set

    override fun write(bytes: ByteArray) {
        writeFailure?.let { failure -> throw failure }
        writes += bytes.copyOf()
    }

    override fun flush() {
        flushCount++
        flushFailure?.let { failure -> throw failure }
    }
}

private fun ByteArray.componentList(): ComponentValue.ByteList = ComponentValue.ByteList(this)

private fun checkWriteResult(permit: Int): List<ComponentValue> = listOf(
    ComponentValue.Result.Ok(ComponentValue.U64(permit.toULong())),
)

private val OK_RESULT: List<ComponentValue> = listOf(ComponentValue.Result.Ok())
private val CLOSED_STREAM_RESULT: List<ComponentValue> = listOf(
    ComponentValue.Result.Error(ComponentValue.Variant(1)),
)

private const val WASI_IO_ERROR = "wasi:io/error@0.2.6"
private const val WASI_IO_STREAMS = "wasi:io/streams@0.2.6"
private const val WASI_CLI_STDOUT = "wasi:cli/stdout@0.2.6"
private const val WASI_RANDOM_RANDOM = "wasi:random/random@0.2.6"
