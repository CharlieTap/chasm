package io.github.charlietap.chasm.wasi.p2

import io.github.charlietap.chasm.embedding.componentResourceType
import io.github.charlietap.chasm.embedding.resource
import io.github.charlietap.chasm.embedding.resourceValue
import io.github.charlietap.chasm.embedding.shapes.ComponentHostFunction
import io.github.charlietap.chasm.embedding.shapes.ComponentHostFunctionContext
import io.github.charlietap.chasm.embedding.shapes.ComponentImport
import io.github.charlietap.chasm.embedding.shapes.ComponentImportable
import io.github.charlietap.chasm.embedding.shapes.ComponentResourceType
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

fun wasiP2CommandImports(
    store: Store,
    stdout: WasiP2ByteSink,
    stderr: WasiP2ByteSink,
    entropy: WasiP2Entropy,
    config: WasiP2CommandConfig = WasiP2CommandConfig(),
): List<ComponentImport> {
    val errorType = componentResourceType(store) { _ -> }
    val outputStreamType = componentResourceType(store) { _ -> }
    val inputStreamType = componentResourceType(store) { _ -> }
    val descriptorType = componentResourceType(store) { _ -> }
    val standardInput = StandardInput
    val standardOutput = OutputStream(stdout)
    val standardError = OutputStream(stderr)

    return listOf(
        instanceImport(
            name = WASI_IO_ERROR,
            imports = listOf(resourceTypeImport("error", errorType)),
        ),
        instanceImport(
            name = WASI_IO_STREAMS,
            imports = listOf(
                resourceTypeImport("output-stream", outputStreamType),
                resourceTypeImport("error", errorType),
                resourceTypeImport("input-stream", inputStreamType),
                functionImport("[method]output-stream.check-write") { arguments ->
                    val output = resourceValue(outputStreamType, arguments.resource()) as OutputStream
                    if (output.closed) {
                        CLOSED_STREAM_RESULT
                    } else {
                        output.writePermit = config.writePermitBytes
                        checkWriteResult(config.writePermitBytes)
                    }
                },
                functionImport("[method]output-stream.write") { arguments ->
                    val output = resourceValue(outputStreamType, arguments.resource()) as OutputStream
                    if (output.closed) {
                        CLOSED_STREAM_RESULT
                    } else {
                        val bytes = arguments.bytes(1)
                        val permit = output.writePermit
                            ?: throw HostFunctionException("WASI P2 output-stream.write requires a check-write permit")
                        if (bytes.size > permit) {
                            throw HostFunctionException(
                                "WASI P2 output-stream.write received ${bytes.size} bytes with a $permit-byte permit",
                            )
                        }
                        output.writePermit = null
                        streamOperation(output, errorType) {
                            output.sink.write(bytes)
                        }
                    }
                },
                functionImport("[method]output-stream.blocking-write-and-flush") { arguments ->
                    val output = resourceValue(outputStreamType, arguments.resource()) as OutputStream
                    val bytes = arguments.bytes(1)
                    if (bytes.size > MAX_BLOCKING_WRITE_BYTES) {
                        throw HostFunctionException(
                            "WASI P2 output-stream.blocking-write-and-flush is limited to $MAX_BLOCKING_WRITE_BYTES bytes",
                        )
                    }
                    streamOperation(output, errorType) {
                        output.writePermit = null
                        output.sink.write(bytes)
                        output.sink.flush()
                    }
                },
                functionImport("[method]output-stream.blocking-flush") { arguments ->
                    val output = resourceValue(outputStreamType, arguments.resource()) as OutputStream
                    streamOperation(output, errorType) {
                        output.writePermit = null
                        output.sink.flush()
                    }
                },
            ),
        ),
        instanceImport(
            name = WASI_CLI_STDIN,
            imports = listOf(
                resourceTypeImport("input-stream", inputStreamType),
                functionImport("get-stdin") {
                    listOf(resource(inputStreamType, standardInput))
                },
            ),
        ),
        instanceImport(
            name = WASI_CLI_STDOUT,
            imports = listOf(
                resourceTypeImport("output-stream", outputStreamType),
                functionImport("get-stdout") {
                    listOf(resource(outputStreamType, standardOutput))
                },
            ),
        ),
        instanceImport(
            name = WASI_CLI_STDERR,
            imports = listOf(
                resourceTypeImport("output-stream", outputStreamType),
                functionImport("get-stderr") {
                    listOf(resource(outputStreamType, standardError))
                },
            ),
        ),
        instanceImport(
            name = WASI_FILESYSTEM_TYPES,
            imports = listOf(
                resourceTypeImport("error", errorType),
                resourceTypeImport("descriptor", descriptorType),
                resourceTypeImport("output-stream", outputStreamType),
                functionImport("[method]descriptor.write-via-stream") { UNSUPPORTED_RESULT },
                functionImport("[method]descriptor.append-via-stream") { UNSUPPORTED_RESULT },
                functionImport("[method]descriptor.get-type") { UNSUPPORTED_RESULT },
                functionImport("[method]descriptor.stat") { UNSUPPORTED_RESULT },
                functionImport("filesystem-error-code") { FILESYSTEM_ERROR_CODE_RESULT },
            ),
        ),
        instanceImport(
            name = WASI_FILESYSTEM_PREOPENS,
            imports = listOf(
                resourceTypeImport("descriptor", descriptorType),
                functionImport("get-directories") { EMPTY_DIRECTORIES_RESULT },
            ),
        ),
        instanceImport(
            name = WASI_RANDOM_RANDOM,
            imports = listOf(
                functionImport("get-random-bytes") { arguments ->
                    val size = arguments.size(config.maxRandomBytes)
                    val bytes = entropy.bytes(size)
                    if (bytes.size != size) {
                        throw HostFunctionException("WASI P2 entropy returned ${bytes.size} bytes for a $size-byte request")
                    }
                    listOf(bytes.componentList())
                },
            ),
        ),
    )
}

private fun instanceImport(
    name: String,
    imports: List<ComponentImport>,
) = ComponentImport(name, ComponentImportable.Instance(imports))

private fun resourceTypeImport(
    name: String,
    type: ComponentResourceType,
) = ComponentImport(name, ComponentImportable.ResourceType(type))

private fun functionImport(
    name: String,
    function: ComponentHostFunction,
) = ComponentImport(name, ComponentImportable.Function(function))

private fun List<ComponentValue>.resource(index: Int = 0): ComponentValue.Resource =
    this[index] as ComponentValue.Resource

private fun List<ComponentValue>.bytes(index: Int): ByteArray =
    (this[index] as ComponentValue.ByteList).bytes

private fun List<ComponentValue>.size(limit: Int): Int {
    val size = (single() as ComponentValue.U64).value
    if (size > limit.toULong()) {
        throw HostFunctionException(
            "WASI P2 random byte request of $size exceeds the configured $limit-byte limit",
        )
    }
    return size.toInt()
}

private fun ByteArray.componentList(): ComponentValue.ByteList = ComponentValue.ByteList(this)

private data object StandardInput

private class OutputStream(
    val sink: WasiP2ByteSink,
) {
    var writePermit: Int? = null
    var closed: Boolean = false
}

private inline fun ComponentHostFunctionContext.streamOperation(
    output: OutputStream,
    errorType: ComponentResourceType,
    operation: () -> Unit,
): List<ComponentValue> {
    if (output.closed) return CLOSED_STREAM_RESULT

    return try {
        operation()
        OK_RESULT
    } catch (failure: WasiP2ByteSinkFailure) {
        output.closed = true
        output.writePermit = null
        listOf(
            ComponentValue.Result.Error(
                ComponentValue.Variant(
                    caseIndex = LAST_OPERATION_FAILED_CASE,
                    value = resource(errorType, failure),
                ),
            ),
        )
    }
}

private fun checkWriteResult(permit: Int): List<ComponentValue> = listOf(
    ComponentValue.Result.Ok(ComponentValue.U64(permit.toULong())),
)

private val OK_RESULT: List<ComponentValue> = listOf(ComponentValue.Result.Ok())
private val CLOSED_STREAM_RESULT: List<ComponentValue> = listOf(
    ComponentValue.Result.Error(ComponentValue.Variant(CLOSED_CASE)),
)
private val UNSUPPORTED_RESULT: List<ComponentValue> = listOf(
    ComponentValue.Result.Error(ComponentValue.Enum(UNSUPPORTED_ERROR_CODE)),
)
private val FILESYSTEM_ERROR_CODE_RESULT: List<ComponentValue> = listOf(ComponentValue.Option.None)
private val EMPTY_DIRECTORIES_RESULT: List<ComponentValue> = listOf(ComponentValue.ListValue(emptyList()))

private const val MAX_BLOCKING_WRITE_BYTES = 4096
private const val LAST_OPERATION_FAILED_CASE = 0
private const val CLOSED_CASE = 1
private const val UNSUPPORTED_ERROR_CODE = 27

private const val WASI_IO_ERROR = "wasi:io/error@0.2.6"
private const val WASI_IO_STREAMS = "wasi:io/streams@0.2.6"
private const val WASI_CLI_STDIN = "wasi:cli/stdin@0.2.6"
private const val WASI_CLI_STDOUT = "wasi:cli/stdout@0.2.6"
private const val WASI_CLI_STDERR = "wasi:cli/stderr@0.2.6"
private const val WASI_FILESYSTEM_TYPES = "wasi:filesystem/types@0.2.6"
private const val WASI_FILESYSTEM_PREOPENS = "wasi:filesystem/preopens@0.2.6"
private const val WASI_RANDOM_RANDOM = "wasi:random/random@0.2.6"
