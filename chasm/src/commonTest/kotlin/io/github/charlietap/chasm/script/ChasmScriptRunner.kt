package io.github.charlietap.chasm.script

import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.script.command.CommandResult
import io.github.charlietap.chasm.script.command.CommandRunner
import io.github.charlietap.chasm.script.ext.readTextFromPath
import io.github.charlietap.chasm.script.host.HostModuleResolver
import io.github.charlietap.sweet.lib.Script
import io.github.charlietap.sweet.lib.ScriptResult
import io.github.charlietap.sweet.lib.ScriptRunner
import io.github.charlietap.sweet.lib.SemanticPhase
import io.github.charlietap.sweet.lib.command.AssertInvalidCommand
import io.github.charlietap.sweet.lib.command.AssertMalformedCommand
import io.github.charlietap.sweet.lib.command.Command
import io.github.charlietap.sweet.lib.command.ModuleCommand
import io.github.charlietap.sweet.lib.command.ModuleDefinitionCommand

class ChasmScriptRunner(
    private val store: Store = Store(),
    private val instances: MutableMap<String?, Instance> = mutableMapOf(),
    private val modules: MutableMap<String?, Module> = mutableMapOf(),
    private val commandRunner: CommandRunner = ::CommandRunner,
    private val hostModuleResolver: HostModuleResolver = ::HostModuleResolver,
) : ScriptRunner {

    private val phaseCommands = mapOf(
        SemanticPhase.DECODING to setOf(ModuleCommand::class, ModuleDefinitionCommand::class, AssertMalformedCommand::class),
        SemanticPhase.VALIDATION to setOf(ModuleCommand::class, ModuleDefinitionCommand::class, AssertMalformedCommand::class, AssertInvalidCommand::class),
    )

    override fun readFile(path: String): String {
        return path.readTextFromPath()
    }

    override fun execute(directory: String, script: Script, phaseSupport: SemanticPhase): ScriptResult {

        val config = Config()
        val context = ScriptContext(
            config = config,
            binaryDirectory = directory,
            phaseSupport = phaseSupport,
            store = store,
            instances = instances,
            modules = modules,
        )

        val hostModule = hostModuleResolver(store)
        context.registerImports(HOST_MODULE_NAME, hostModule)

        script.commands.forEach { command ->

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

    private fun shouldSkipCommand(command: Command, phaseSupport: SemanticPhase): Boolean {
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
