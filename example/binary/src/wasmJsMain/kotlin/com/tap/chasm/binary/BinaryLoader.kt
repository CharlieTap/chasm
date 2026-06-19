@file:OptIn(ExperimentalWasmJsInterop::class)

package com.tap.chasm.binary

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

internal object WasmJsBinaryLoader : BinaryLoader {
    override fun load(path: String): ByteArray {
        val response = loadResponseText(path)
        return ByteArray(response.length) { index ->
            response[index].code.toByte()
        }
    }
}

actual fun binaryLoaderFactory(): BinaryLoader {
    return WasmJsBinaryLoader
}

private fun loadResponseText(path: String): String = js(
    """
    {
        const base = window.location.href;
        const url = new URL(path, base).toString();
        const xhr = new XMLHttpRequest();
        xhr.open("GET", url, false);
        if (xhr.overrideMimeType !== undefined) {
            xhr.overrideMimeType("text/plain; charset=x-user-defined");
        }
        xhr.send(null);

        const status = xhr.status;
        if (Math.floor(status / 100) !== 2) {
            throw new Error("Failed to load '" + path + "' (HTTP " + status + ") at " + url);
        }

        return xhr.responseText;
    }
    """,
)
