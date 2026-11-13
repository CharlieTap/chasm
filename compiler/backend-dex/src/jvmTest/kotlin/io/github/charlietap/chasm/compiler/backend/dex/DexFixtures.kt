package io.github.charlietap.chasm.compiler.backend.dex

import com.android.tools.smali.baksmali.Adaptors.ClassDefinition
import com.android.tools.smali.baksmali.BaksmaliOptions
import com.android.tools.smali.baksmali.formatter.BaksmaliWriter
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.frontend.ModuleCompiler
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.validator.WasmModuleValidator
import java.io.File
import java.io.StringWriter

internal fun decodeModule(
    bytes: ByteArray,
): Module {
    val config = ModuleConfig()
    val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
    return WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
}

internal fun generate(
    module: Module,
    options: DexOptions = DexOptions(stacktraces = true),
): DexModule {
    val code = ModuleCompiler(module).getOrThrow { IllegalStateException(it.toString()) }
    return ModuleLowerer(module, code, options)
}

internal fun fixture(
    name: String,
): ByteArray =
    checkNotNull(DexFixtures::class.java.classLoader.getResourceAsStream("fixtures/$name.wasm")) {
        "missing fixture $name"
    }.use { it.readBytes() }

internal fun renderSmali(
    dex: List<ByteArray>,
): String {
    val options = BaksmaliOptions()
    val classes = dex.flatMap { DexBackedDexFile(Opcodes.forApi(MIN_API), it).classes }
    return classes.sortedBy { it.type }.joinToString("\n") { classDef ->
        val writer = StringWriter()
        BaksmaliWriter(writer).use { ClassDefinition(options, classDef).writeTo(it) }
        writer.toString()
    }
}

internal data class RuntimeMember(
    val owner: String,
    val name: String,
    val descriptor: String,
) : Comparable<RuntimeMember> {
    override fun compareTo(
        other: RuntimeMember,
    ): Int = toString().compareTo(other.toString())

    override fun toString(): String = "$owner->$name$descriptor"
}

internal fun runtimeMethods(
    dex: List<ByteArray>,
): Set<RuntimeMember> {
    val files = dex.map { DexBackedDexFile(Opcodes.forApi(MIN_API), it) }
    val generated = files.flatMap { file -> file.classes.map { it.type } }.toSet()
    return files.flatMap { file -> file.getReferences(ReferenceType.METHOD) }
        .map { it as MethodReference }
        .filter { it.definingClass !in generated }
        .map { RuntimeMember(it.definingClass, it.name, "(${it.parameterTypes.joinToString("")})${it.returnType}") }
        .toSortedSet()
}

internal fun runtimeFields(
    dex: List<ByteArray>,
): Set<RuntimeMember> {
    val files = dex.map { DexBackedDexFile(Opcodes.forApi(MIN_API), it) }
    val generated = files.flatMap { file -> file.classes.map { it.type } }.toSet()
    return files.flatMap { file -> file.getReferences(ReferenceType.FIELD) }
        .map { it as FieldReference }
        .filter { it.definingClass !in generated }
        .map { RuntimeMember(it.definingClass, it.name, it.type) }
        .toSortedSet()
}

internal fun zeroOffsetBranches(
    dex: List<ByteArray>,
): List<String> = dex.flatMap { bytes ->
    DexBackedDexFile(Opcodes.forApi(MIN_API), bytes).classes.flatMap { classDef ->
        classDef.methods.flatMap { method ->
            method.implementation?.instructions?.toList().orEmpty()
                .filter { it is OffsetInstruction && it.codeOffset == 0 && it.opcode != Opcode.GOTO_32 }
                .map { "${classDef.type}->${method.name}: ${it.opcode}" }
        }
    }
}

internal fun assertGolden(
    name: String,
    actual: String,
) {
    val file = File(GOLDEN_DIRECTORY, name)
    if (System.getProperty("codegen.dex.updateGolden") == "true") {
        file.parentFile.mkdirs()
        file.writeText(actual)
    }
    kotlin.test.assertTrue(file.exists(), "no golden $name; run with -Dcodegen.dex.updateGolden=true to write it")
    kotlin.test.assertEquals(file.readText(), actual, "golden $name differs; rerun with -Dcodegen.dex.updateGolden=true after reviewing")
}

private val GOLDEN_DIRECTORY = File(System.getProperty("user.dir"), "src/jvmTest/golden")

internal const val MIN_API = 26

private object DexFixtures
