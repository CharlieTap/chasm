package io.github.charlietap.chasm.compiler.kotlin

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.program.ProgramCompiler
import io.github.charlietap.chasm.runtime.stack.ValueStack
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.lang.reflect.Constructor
import java.net.URLClassLoader
import java.security.MessageDigest

enum class KotlinCompilationMode {
    /** Compile missing artifacts during an explicit preparation run. */
    PREPARE,

    /** Load precompiled code only. A missing or stale artifact is an error. */
    CACHED,
}

data class KotlinCompilationReport(
    val key: String,
    val instructionCount: Int,
    val generatedInstructionCount: Int,
    val controlInstructionCount: Int,
    val blockCount: Int,
    val classCount: Int,
    val sourceBytes: Long,
    val cacheHit: Boolean,
    val compilationNanos: Long,
    val promotedInstructionCount: Int = 0,
)

/**
 * JVM driver for the Kotlin source backend. Preparation is deliberately separate
 * from cache-only execution. Artifacts contain code, never another store's data.
 *
 * This prototype compiles generated code as a friend of the invoker module so
 * its existing internal inline executors remain internal. No executor bodies
 * are copied into the generator and no public helper ABI is introduced.
 */
class JvmKotlinProgramCompiler(
    private val directory: File,
    private val mode: KotlinCompilationMode = KotlinCompilationMode.CACHED,
    private val generator: KotlinSourceGenerator = KotlinSourceGenerator(),
    private val classpath: List<File> = runtimeClasspath(),
    private val countExecutions: Boolean = false,
    private val onCompilation: (KotlinCompilationReport) -> Unit = {},
) : ProgramCompiler, AutoCloseable {
    private val artifacts = mutableMapOf<String, LoadedArtifact>()
    private val classpathIdentity by lazy { fingerprint(classpath) }
    private var closed = false

    var generatedBlockExecutions: Long = 0
        private set
    var generatedInstructionExecutions: Long = 0
        private set

    override fun compile(
        program: Program,
        firstIp: Int,
        instructions: List<LinkedInstruction>,
        functionEntryIps: IntArray,
    ): ModuleTrapError? {
        if (closed) return InstantiationError.ProgramCompilationFailed("Kotlin compiler is closed")
        return try {
            val source = generator.generate(firstIp, instructions, functionEntryIps)
            val digest = MessageDigest.getInstance("SHA-256")
            digest.update("chasm-kotlin-source-v1\n$classpathIdentity\n".toByteArray())
            source.groups.forEach { digest.update(it.source.toByteArray()) }
            val key = digest.digest().toHex()
            val target = File(directory, key)
            val completion = File(target, "complete")
            val cacheHit = completion.isFile
            var compilationNanos = 0L
            val artifact = artifacts.getOrPut(key) {
                if (!cacheHit) {
                    check(mode == KotlinCompilationMode.PREPARE) { "No prepared Kotlin artifact for $key" }
                    val start = System.nanoTime()
                    compileSource(source, target)
                    compilationNanos = System.nanoTime() - start
                    completion.writeText(key)
                }
                load(source, target)
            }

            val bindings = instructions.toTypedArray()
            // Construct and check every replacement before changing the program.
            val replacements = source.groups.mapIndexed { index, group ->
                val body = artifact.constructors[index].newInstance(bindings, firstIp)
                val dispatcher = if (countExecutions) counted(body, group, firstIp) else body
                group to dispatcher
            }
            replacements.forEach { (group, dispatcher) ->
                group.blocks.forEach { block -> program.replace(firstIp + block.startOffset, dispatcher) }
            }
            onCompilation(
                KotlinCompilationReport(
                    key = key,
                    instructionCount = source.instructionCount,
                    generatedInstructionCount = source.generatedInstructionCount,
                    controlInstructionCount = source.controlInstructionCount,
                    blockCount = source.blockCount,
                    classCount = source.groups.size,
                    sourceBytes = source.groups.sumOf { it.source.toByteArray().size.toLong() },
                    cacheHit = cacheHit,
                    compilationNanos = compilationNanos,
                    promotedInstructionCount = source.promotedInstructionCount,
                ),
            )
            null
        } catch (exception: Exception) {
            InstantiationError.ProgramCompilationFailed(exception.message ?: exception.toString())
        }
    }

    private fun counted(body: DispatchableInstruction, group: KotlinSourceGroup, firstIp: Int): DispatchableInstruction {
        val sizes = group.blocks.associate { firstIp + it.startOffset to it.size }
        val onBlock: (Int) -> Unit = { size ->
            generatedBlockExecutions++
            generatedInstructionExecutions += size
        }
        return DispatchableInstruction { vstack, context, nextIp ->
            if (body is KotlinGeneratedInstruction) {
                body.invokeCounted(vstack, context, nextIp, onBlock)
            } else {
                generatedBlockExecutions++
                generatedInstructionExecutions += checkNotNull(sizes[nextIp - 1])
                body(vstack, context, nextIp)
            }
        }
    }

    private fun compileSource(source: KotlinProgramSource, target: File) {
        val sources = File(target, "source").apply { mkdirs() }
        val classes = File(target, "classes").apply { mkdirs() }
        val files = source.groups.map { group ->
            File(sources, "${group.className}.kt").apply { writeText(group.source) }
        }
        if (files.isEmpty()) return
        val invoker = Class.forName("io.github.charlietap.chasm.executor.invoker.FunctionInvokerKt")
            .protectionDomain.codeSource.location.toURI().let(::File)
        val output = ByteArrayOutputStream()
        val arguments = listOf(
            "-no-stdlib",
            "-no-reflect",
            "-classpath",
            classpath.joinToString(File.pathSeparator) { it.absolutePath },
            "-jvm-target",
            "17",
            "-Xfriend-paths=${invoker.absolutePath}",
            "-Xno-call-assertions",
            "-Xno-param-assertions",
            "-Xno-receiver-assertions",
            "-opt-in=kotlin.ExperimentalUnsignedTypes",
            "-Xwarning-level=NOTHING_TO_INLINE:disabled",
            "-d",
            classes.absolutePath,
        ) + files.map { it.absolutePath }
        val exitCode = PrintStream(output).use { K2JVMCompiler().exec(it, *arguments.toTypedArray()) }
        File(target, "compiler.log").writeText(output.toString(Charsets.UTF_8))
        check(exitCode == ExitCode.OK) { "Kotlin compilation failed in $target:\n${output.toString(Charsets.UTF_8).takeLast(12000)}" }
    }

    private fun load(source: KotlinProgramSource, target: File): LoadedArtifact {
        val loader = URLClassLoader(arrayOf(File(target, "classes").toURI().toURL()), javaClass.classLoader)
        try {
            val constructors = source.groups.map { group ->
                loader.loadClass("$GENERATED_PACKAGE.${group.className}")
                    .asSubclass(DispatchableInstruction::class.java)
                    .getConstructor(Array<LinkedInstruction>::class.java, Int::class.javaPrimitiveType)
            }
            return LoadedArtifact(loader, constructors)
        } catch (exception: Exception) {
            loader.close()
            throw exception
        }
    }

    override fun close() {
        artifacts.values.forEach { it.loader.close() }
        artifacts.clear()
        closed = true
    }

    private class LoadedArtifact(
        val loader: URLClassLoader,
        val constructors: List<Constructor<out DispatchableInstruction>>,
    )
}

private fun runtimeClasspath(): List<File> {
    val entries = linkedSetOf<File>()
    System.getProperty("java.class.path").split(File.pathSeparator).mapTo(entries, ::File)
    var loader: ClassLoader? = JvmKotlinProgramCompiler::class.java.classLoader
    while (loader != null) {
        if (loader is URLClassLoader) loader.urLs.filter { it.protocol == "file" }.mapTo(entries) { File(it.toURI()) }
        loader = loader.parent
    }
    return entries.filter { it.exists() }.map { it.canonicalFile }.distinct()
}

private fun fingerprint(classpath: List<File>): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val buffer = ByteArray(64 * 1024)
    for (entry in classpath) {
        val files = if (entry.isDirectory) entry.walkTopDown().filter { it.isFile }.sortedBy { it.relativeTo(entry).path }.toList() else listOf(entry)
        digest.update(entry.name.toByteArray())
        for (file in files) {
            digest.update((if (entry.isDirectory) file.relativeTo(entry).path else file.name).toByteArray())
            file.inputStream().use { input ->
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    digest.update(buffer, 0, count)
                }
            }
        }
    }
    return digest.digest().toHex()
}

private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it) }
