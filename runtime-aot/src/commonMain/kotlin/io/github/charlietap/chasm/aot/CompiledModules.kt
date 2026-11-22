@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.AotModule
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._module
import io.github.charlietap.chasm.embedding.shapes._payload
import java.lang.reflect.Modifier

@ExperimentalChasmApi
fun compiledModule(moduleClass: Class<*>): ChasmResult<Module, ModuleError> {

    val instance = try {
        val field = moduleClass.getField(INSTANCE_FIELD)
        if (Modifier.isStatic(field.modifiers)) {
            field.get(null)
        } else {
            null
        }
    } catch (_: ReflectiveOperationException) {
        null
    } catch (failure: LinkageError) {
        return malformed("${moduleClass.name} could not be initialised: $failure")
    }

    val compiled = instance as? CompiledModule ?: return malformed("${moduleClass.name} is not a compiled module class")

    return compiledModule(compiled)
}

@ExperimentalChasmApi
fun compiledModule(module: CompiledModule): ChasmResult<Module, ModuleError> {

    val metadata = module.metadataResult.getOrElse { failure -> return malformed(failure.message ?: failure.toString()) }
    val declarations = declarations(module).getOrElse { failure -> return malformed(failure.message ?: failure.toString()) }
    val info = moduleInfo(declarations)

    return Success(_module(info.imports, info.exports, AotModule(module, metadata, declarations)))
}

@ExperimentalChasmApi
fun compiledModule(
    className: String,
    loader: ClassLoader,
): ChasmResult<Module, ModuleError> {

    val moduleClass = try {
        Class.forName(className, true, loader)
    } catch (_: ClassNotFoundException) {
        return malformed("$className is not on the class path")
    } catch (failure: LinkageError) {
        return malformed("$className could not be initialised: $failure")
    }

    return compiledModule(moduleClass)
}

@ExperimentalChasmApi
fun compiledModuleMetadata(module: Module): CompiledModuleMetadata {

    val payload = requireNotNull(module._payload() as? AotModule) { "the module was not loaded by compiledModule" }

    return payload.metadata
}

@Suppress("TooGenericExceptionCaught")
private fun declarations(compiled: CompiledModule): Result<ModuleDeclarations> {
    return try {
        Result.success(compiled.code.buildDeclarations())
    } catch (exception: Exception) {
        Result.failure(exception)
    }
}

private fun malformed(reason: String): Error<ModuleError.Malformed> {
    return Error(ModuleError.Malformed("Compiled module rejected: $reason"))
}

private const val INSTANCE_FIELD = "INSTANCE"
