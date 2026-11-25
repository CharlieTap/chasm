package io.github.charlietap.chasm.compiled.internal

import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult

internal class RuntimeLoadedCode {

    fun compiled(
        loader: ClassLoader,
        className: String,
    ): ChasmResult<CompiledModule, ModuleError> {

        val entry = try {
            loader.loadClass(className)
        } catch (_: ClassNotFoundException) {
            return malformed("class $className was not found")
        } catch (failure: LinkageError) {
            return malformed("class $className does not link against this runtime: $failure")
        }

        val compiled = try {
            entry.getField(COMPILED_FIELD).get(null) as? CompiledModule
        } catch (_: ReflectiveOperationException) {
            null
        } catch (failure: LinkageError) {
            return malformed("class $className does not link against this runtime: $failure")
        }

        return if (compiled == null) {
            malformed("class $className is not a compiled module")
        } else {
            ChasmResult.Success(compiled)
        }
    }

    private companion object {
        const val COMPILED_FIELD = "COMPILED"
    }
}
