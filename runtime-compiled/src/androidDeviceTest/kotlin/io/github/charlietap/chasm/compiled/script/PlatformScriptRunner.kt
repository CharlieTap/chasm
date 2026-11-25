package io.github.charlietap.chasm.compiled.script

import io.github.charlietap.chasm.compiled.onLargeStack
import io.github.charlietap.chasm.compiled.resourceBytes
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
        return onLargeStack {
            CompiledScriptRunner(
                directory = directory,
                scriptFilename = scriptFilename,
                phaseSupport = phaseSupport,
                excludedLines = excludedLines,
                compiler = ::compileDexModule,
                files = ::sweetResource,
            ).run()
        }
    }

    private fun sweetResource(path: String): ByteArray {
        return resourceBytes(SWEET_RESOURCES + path.replace('\\', '/').substringAfter(SWEET_INTERMEDIATES))
    }

    private companion object {
        const val SWEET_RESOURCES = "sweet/"
        const val SWEET_INTERMEDIATES = "/sweet/intermediates/"
    }
}
