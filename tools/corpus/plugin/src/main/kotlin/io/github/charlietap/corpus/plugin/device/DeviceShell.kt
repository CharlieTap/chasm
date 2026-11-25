package io.github.charlietap.corpus.plugin.device

import java.io.File

interface DeviceShell {

    fun run(vararg arguments: String): AdbOutput

    fun install(apk: File)

    fun instrument(
        testClass: String,
        applicationId: String,
        runner: String,
        line: (String) -> Unit,
    ): Int
}
