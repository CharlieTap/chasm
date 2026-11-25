package io.github.charlietap.chasm.compiled.script

import io.github.charlietap.chasm.compiled.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.ScriptResult
import io.github.charlietap.sweet.lib.ScriptRunner
import io.github.charlietap.sweet.lib.SemanticPhase

class PlatformScriptRunner(
    private val directory: String,
    private val scriptFilename: String,
    private val phaseSupport: SemanticPhase,
    private val excludedLines: Set<Int> = emptySet(),
) : ScriptRunner {

    override fun run(): ScriptResult {
        ClassFileCompiler.useTestStack()
        return CompiledScriptRunner(
            directory = directory,
            scriptFilename = scriptFilename,
            phaseSupport = phaseSupport,
            excludedLines = excludedLines,
            compiler = ClassFileCompiler::compile,
            files = String::readBytesFromPath,
        ).run()
    }
}
