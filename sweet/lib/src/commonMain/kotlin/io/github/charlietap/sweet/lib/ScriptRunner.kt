package io.github.charlietap.sweet.lib

interface ScriptRunner {

    fun readFile(path: String): String

    fun execute(directory: String, script: Script): ScriptResult
}
