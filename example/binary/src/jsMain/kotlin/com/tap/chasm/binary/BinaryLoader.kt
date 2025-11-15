package com.tap.chasm.binary

import kotlinx.browser.window
import org.w3c.dom.url.URL

internal object JsBinaryLoader : BinaryLoader {
    override fun load(path: String): ByteArray {
        val base = window.location.href
        val url = URL(path, base).toString()

        val xhr = js("new XMLHttpRequest()")
        xhr.open("GET", url, false) // synchronous (demo-only; blocks UI)
        if (xhr.overrideMimeType != undefined) {
            xhr.overrideMimeType("text/plain; charset=x-user-defined")
        }
        xhr.send(null)

        val status = xhr.status as Int
        if (status / 100 != 2) {
            throw IllegalStateException("Failed to load '$path' (HTTP $status) at $url")
        }

        val response = xhr.responseText as String
        return ByteArray(response.length) { i ->
            response[i].code.toUByte().toByte()
        }
    }
}

actual fun binaryLoaderFactory(): BinaryLoader {
    return JsBinaryLoader
}
