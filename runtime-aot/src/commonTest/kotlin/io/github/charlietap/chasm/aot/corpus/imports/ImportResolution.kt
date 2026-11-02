package io.github.charlietap.chasm.aot.corpus.imports

import at.released.weh.host.EmbedderHost
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Memory

internal data class ImportResolution(
    val imports: List<Import>,
    val memories: MutableMap<String, Memory>,
    val globals: Map<String, Global>,
    val hosts: List<EmbedderHost>,
    val stdout: MutableList<Byte>,
    val stderr: MutableList<Byte>,
    val captures: Map<String, ImportCapture>,
)
