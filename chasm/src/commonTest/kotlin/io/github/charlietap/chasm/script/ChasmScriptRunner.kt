package io.github.charlietap.chasm.script

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.script.command.CommandResult
import io.github.charlietap.chasm.script.command.CommandRunner
import io.github.charlietap.chasm.script.host.HostModuleResolver
import io.github.charlietap.sweet.lib.Script
import io.github.charlietap.sweet.lib.ScriptResult
import io.github.charlietap.sweet.lib.ScriptRunner

class ChasmScriptRunner(
    private val store: Store = Store(),
    private val instances: MutableMap<String?, ModuleInstance> = mutableMapOf(),
    private val commandRunner: CommandRunner = ::CommandRunner,
    private val hostModuleResolver: HostModuleResolver = ::HostModuleResolver,
) : ScriptRunner {

    override fun readFile(path: String): String = Resource(path).readText()

    override fun execute(directory: String, script: Script): ScriptResult {

        val context = ScriptContext(
            binaryDirectory = directory,
            store = store,
            instances = instances,
        )

        val hostModule = hostModuleResolver(store)
        context.registerImports(HOST_MODULE_NAME, hostModule.exports)

        script.commands.forEach { command ->

            val result = try {
                commandRunner(context, command)
            } catch (e: Exception) {
                return ScriptResult.Failure(command, e.toString())
            }

            if (result is CommandResult.Failure) {
                return ScriptResult.Failure(result.command, result.reason)
            }
        }

        return ScriptResult.Success
    }

    private companion object {
        const val HOST_MODULE_NAME = "spectest"
    }
}
