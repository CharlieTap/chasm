@file:OptIn(ExperimentalWasmJsInterop::class)

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

internal actual fun setRootText(value: String): Unit = js(
    """
    {
        const root = document.getElementById("root");
        if (root != null) {
            root.textContent = value;
        }
    }
    """,
)
