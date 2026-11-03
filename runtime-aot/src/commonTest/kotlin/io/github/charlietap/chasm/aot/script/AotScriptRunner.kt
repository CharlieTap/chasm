package io.github.charlietap.chasm.aot.script

import io.github.charlietap.chasm.compiled.script.CompiledScriptRunner
import io.github.charlietap.chasm.compiled.script.ext.readBytesFromPath
import io.github.charlietap.sweet.lib.ScriptResult
import io.github.charlietap.sweet.lib.ScriptRunner
import io.github.charlietap.sweet.lib.SemanticPhase

class AotScriptRunner(
    private val directory: String,
    private val scriptFilename: String,
    private val phaseSupport: SemanticPhase,
    private val excludedLines: Set<Int> = emptySet(),
) : ScriptRunner {

    override fun run(): ScriptResult {
        AotCompiler.useTestStack()
        return CompiledScriptRunner(
            directory = directory,
            scriptFilename = scriptFilename,
            phaseSupport = phaseSupport,
            excludedLines = excludedLines,
            compiler = AotCompiler::compile,
            files = String::readBytesFromPath,
        ).run()
    }
}
