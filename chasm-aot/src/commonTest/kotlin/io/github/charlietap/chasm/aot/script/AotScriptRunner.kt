package io.github.charlietap.chasm.aot.script

import io.github.charlietap.chasm.aot.script.command.CommandResult
import io.github.charlietap.chasm.aot.script.command.CommandRunner
import io.github.charlietap.chasm.aot.script.ext.readTextFromPath
import io.github.charlietap.chasm.aot.script.host.HostModuleResolver
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.sweet.lib.Script
import io.github.charlietap.sweet.lib.ScriptResult
import io.github.charlietap.sweet.lib.ScriptRunner
import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.lib.command.AssertInvalidCommand
import io.github.charlietap.sweet.lib.command.AssertMalformedCommand
import io.github.charlietap.sweet.lib.command.Command
import io.github.charlietap.sweet.lib.command.ModuleCommand
import io.github.charlietap.sweet.lib.command.ModuleDefinitionCommand
import kotlinx.serialization.json.Json

class AotScriptRunner(
    private val directory: String,
    private val scriptFilename: String,
    private val phaseSupport: SemanticPhase,
    private val excludedLines: Set<Int> = emptySet(),
    private val storeConfig: StoreConfig = StoreConfig(),
    private val commandRunner: CommandRunner = ::CommandRunner,
    private val hostModuleResolver: HostModuleResolver = ::HostModuleResolver,
) : ScriptRunner {

    private val phaseCommands = mapOf(
        SemanticPhase.DECODING to setOf(ModuleCommand::class, ModuleDefinitionCommand::class, AssertMalformedCommand::class),
        SemanticPhase.VALIDATION to setOf(ModuleCommand::class, ModuleDefinitionCommand::class, AssertMalformedCommand::class, AssertInvalidCommand::class),
    )

    override fun run(): ScriptResult {
        ScriptModules.useTestStack()
        val file = (directory + "/" + scriptFilename).readTextFromPath()
        val script = Json.decodeFromString<Script>(file)
        return execute(script)
    }

    private fun execute(
        script: Script,
    ): ScriptResult {

        val config = Config(
            runtimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
        )
        val store = store(storeConfig)
        val context = ScriptContext(
            config = config,
            binaryDirectory = directory,
            phaseSupport = phaseSupport,
            storeConfig = storeConfig,
            store = store,
            instances = mutableMapOf(),
            modules = mutableMapOf(),
        )

        try {
            return execute(script, context)
        } finally {
            dropStore(store)
        }
    }

    private fun execute(
        script: Script,
        context: ScriptContext,
    ): ScriptResult {

        val hostModule = hostModuleResolver(context.store, context.storeConfig)
        context.registerImports(HOST_MODULE_NAME, hostModule)

        script.commands.forEach { command ->

            if (command.line in excludedLines) {
                return@forEach
            }

            if (shouldSkipCommand(command, phaseSupport)) {
                return@forEach
            }

            val result = try {
                commandRunner(context, command)
            } catch (e: Exception) {
                println(e.stackTraceToString())
                return ScriptResult.Failure(command, e.toString())
            }

            if (result is CommandResult.Failure) {
                return ScriptResult.Failure(result.command, result.reason)
            }
        }

        return ScriptResult.Success
    }

    private fun shouldSkipCommand(
        command: Command,
        phaseSupport: SemanticPhase,
    ): Boolean {
        val commandSubset = phaseCommands[phaseSupport]
        if (!commandSubset.isNullOrEmpty()) {
            if (commandSubset.none { command::class == it }) {
                return true
            }
        }

        return false
    }

    private companion object {
        const val HOST_MODULE_NAME = "spectest"
    }
}
