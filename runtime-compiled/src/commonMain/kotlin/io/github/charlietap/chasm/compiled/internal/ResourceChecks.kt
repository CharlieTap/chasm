package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.format.dataResource
import io.github.charlietap.chasm.compiled.format.trapSitesResource
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import java.io.IOException

internal class ResourceChecks {

    fun failure(
        resources: ModuleResources,
        root: String,
        declarations: ModuleDeclarations,
        stacktraces: Boolean,
    ): String? {

        val payloads = declarations.dataSegments.mapNotNull { segment -> segment.payload as? DataPayload.External }
        if (payloads.isNotEmpty()) {
            val path = dataResource(root)
            val length = try {
                resources.length(path)
            } catch (_: IOException) {
                return unreadable(path)
            } ?: return "resource $path is missing"
            val outside = payloads.firstOrNull { payload ->
                payload.offset < 0 || payload.size < 0 || payload.offset > length - payload.size
            }
            if (outside != null) {
                return "resource $path holds $length bytes but a data segment needs ${outside.size} bytes at ${outside.offset}"
            }
        }

        if (stacktraces) {
            val sites = trapSitesResource(root)
            val present = try {
                resources.contains(sites)
            } catch (_: IOException) {
                return unreadable(sites)
            }
            if (!present) {
                return "resource $sites is missing"
            }
        }

        return null
    }

    private fun unreadable(path: String): String {
        return "resource $path cannot be read"
    }
}
