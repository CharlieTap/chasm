package io.github.charlietap.chasm.jit.script

import io.github.charlietap.chasm.compiled.script.CompiledScriptRunner
import io.github.charlietap.chasm.jit.onLargeStack
import io.github.charlietap.chasm.jit.resourceBytes
import io.github.charlietap.sweet.lib.ScriptResult
import io.github.charlietap.sweet.lib.ScriptRunner
import io.github.charlietap.sweet.lib.SemanticPhase

class DexScriptRunner(
    private val directory: String,
    private val scriptFilename: String,
    private val phaseSupport: SemanticPhase,
    private val excludedLines: Set<Int> = emptySet(),
) : ScriptRunner {

    override fun run(): ScriptResult {
        val runner = CompiledScriptRunner(
            directory = "sweet/" + directory.replace('\\', '/').substringAfter(INTERMEDIATES),
            scriptFilename = scriptFilename,
            phaseSupport = phaseSupport,
            excludedLines = excludedLines,
            compiler = ::compileDex,
            files = ::resourceBytes,
        )
        return onLargeStack { runner.run() }
    }

    private companion object {
        const val INTERMEDIATES = "/sweet/intermediates/"
    }
}
