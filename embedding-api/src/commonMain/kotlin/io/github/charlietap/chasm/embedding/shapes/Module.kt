package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.InternalChasmApi

class Module internal constructor(
    val imports: List<ImportDefinition>,
    val exports: List<ExportDefinition>,
    @PublishedApi internal val payload: Any,
)

@InternalChasmApi
fun _module(
    imports: List<ImportDefinition>,
    exports: List<ExportDefinition>,
    payload: Any,
): Module {
    return Module(imports, exports, payload)
}

@InternalChasmApi
inline fun Module._payload(): Any {
    return payload
}
