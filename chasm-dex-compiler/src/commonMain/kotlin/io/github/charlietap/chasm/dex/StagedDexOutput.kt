package io.github.charlietap.chasm.dex

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.compiler.backend.dex.DexResources
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

internal class StagedDexOutput(
    private val output: DexOutput,
) : DexResources {

    private val dexFiles = LinkedHashMap<String, ByteArray>()
    private val stagedResources = LinkedHashMap<String, ByteArray>()

    val resources: List<String>
        get() = stagedResources.keys.toList()

    fun dexFile(
        name: String,
        bytes: ByteArray,
    ): String {

        dexFiles[name] = bytes

        return name
    }

    override fun <T> resource(
        path: String,
        write: (OutputStream) -> T,
    ): T {

        val buffer = ByteArrayOutputStream()
        val result = write(buffer)
        stagedResources[path] = buffer.toByteArray()

        return result
    }

    fun commit(): Result<Unit, IOException> {

        val atomic = output as? AtomicDexOutput

        try {
            dexFiles.forEach { (name, bytes) -> output.dexFile(name, bytes) }
            stagedResources.forEach { (path, bytes) -> output.resource(path, bytes) }
            atomic?.commit()
        } catch (failure: IOException) {
            atomic?.discard()
            return Err(failure)
        } catch (failure: Throwable) {
            atomic?.discard()
            throw failure
        }

        return Ok(Unit)
    }
}
