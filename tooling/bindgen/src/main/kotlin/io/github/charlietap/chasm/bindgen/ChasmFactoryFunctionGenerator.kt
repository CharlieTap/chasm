package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.BYTE_ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy

internal class ChasmFactoryFunctionGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: FactoryVisibility,
        linksWasiPreview1: Boolean,
        config: CodegenConfig,
    ): FunSpec {

        val source = moduleSource(config)
        val visibilityModifier = when (visibility) {
            FactoryVisibility.INTERNAL -> KModifier.INTERNAL
            FactoryVisibility.PUBLIC -> KModifier.PUBLIC
        }

        return FunSpec.builder(interfaceName.replaceFirstChar { character -> character.lowercaseChar() }).apply {

            addModifiers(visibilityModifier, KModifier.SUSPEND)
            addParameters(source.inputs)
            if (linksWasiPreview1) {
                addParameter("wasiHost", WASI_EMBEDDER_HOST_CLASS_NAME)
            }
            addParameter(
                ParameterSpec.builder("imports", List::class.asClassName().parameterizedBy(CHASM_CODEGEN_IMPORT_CLASS_NAME))
                    .defaultValue("emptyList()")
                    .build(),
            )
            addParameters(source.factories)
            returns(ClassName(packageName, interfaceName))

            addCode(source.loadModule())
            if (linksWasiPreview1) {
                addStatement("val explicitImports = %M(store, imports)", CHASM_ALLOCATE_IMPORTS_FUNCTION)
                addStatement(
                    "val wasiImports = %T(store, module) { host = wasiHost }" +
                        ".buildRequired(providedImports = explicitImports)",
                    WASI_PREVIEW1_BUILDER_CLASS_NAME,
                )
                addStatement("val allocatedImports = explicitImports + wasiImports")
            } else {
                addStatement("val allocatedImports = %M(store, imports)", CHASM_ALLOCATE_IMPORTS_FUNCTION)
            }
            addCode(source.instantiate())
            addStatement(
                "return %T(store = store, instance = instance)",
                ClassName(packageName, interfaceName + "Impl"),
            )
        }.build()
    }

    private fun moduleSource(config: CodegenConfig): ModuleSource {
        return when (val tier = config.executionTier) {
            is ExecutionTier.Interpreter -> DecodedModule(config)
            is ExecutionTier.Compiled -> {
                when (val source = tier.module) {
                    is CompiledSource.Generated -> CompiledModule(source)
                    is CompiledSource.Loaded -> LoadedModule()
                }
            }
        }
    }
}

private interface ModuleSource {
    val inputs: List<ParameterSpec>

    val factories: List<ParameterSpec>

    fun loadModule(): CodeBlock

    fun instantiate(): CodeBlock
}

private class DecodedModule(
    private val config: CodegenConfig,
) : ModuleSource {

    override val inputs = listOf(ParameterSpec("binary", BYTE_ARRAY))

    override val factories = listOf(
        ParameterSpec.builder("moduleFactory", MODULE_FACTORY_OVERRIDE_TYPE.copy(nullable = true))
            .defaultValue("null")
            .build(),
        ParameterSpec.builder("instanceFactory", INSTANCE_FACTORY_OVERRIDE_TYPE.copy(nullable = true))
            .defaultValue("null")
            .build(),
    )

    override fun loadModule(): CodeBlock {
        return CodeBlock.builder().apply {

            addStatement("val store = %M(%L)", CHASM_STORE_FUNCTION, runtimeConfig())
            addStatement(
                "val module = moduleFactory?.invoke(binary) ?: %M(binary).%M(%S)",
                CHASM_MODULE_FUNCTION,
                CHASM_EXPECT,
                "Failed to decode binary",
            )
        }.build()
    }

    override fun instantiate(): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "val instance = instanceFactory?.invoke(store, module, allocatedImports) ?: " +
                    "%M(store, module, allocatedImports).%M(%S)",
                CHASM_INSTANCE_FUNCTION,
                CHASM_EXPECT,
                "Failed to instantiate module",
            ).build()
    }

    private fun runtimeConfig(): CodeBlock {

        val runtime = config.runtimeConfig
        val cpu = runtime.limits.cpu
        val memory = runtime.limits.memory

        val memoryArguments = buildList {
            memory.linearMemory?.let { size -> add(bytesArgument("linearMemory", size)) }
            memory.gcHeap?.let { size -> add(bytesArgument("gcHeap", size)) }
            memory.tables?.let { size -> add(bytesArgument("tables", size)) }
            memory.stack?.let { size -> add(bytesArgument("stack", size)) }
            if (memory.onGrowthFailure != GrowthFailureStrategy.Continue) {
                add(CodeBlock.of("onGrowthFailure = %T.%L", CHASM_GROWTH_FAILURE_STRATEGY_CLASS_NAME, memory.onGrowthFailure.name))
            }
        }
        val limitsArguments = buildList {
            if (cpu is CpuLimits.Metered) {
                add(CodeBlock.of("cpu = %T(%T(%LL))", CHASM_METERED_CLASS_NAME, CHASM_FUEL_CLASS_NAME, cpu.fuel.units))
            }
            if (memoryArguments.isNotEmpty()) {
                add(CodeBlock.of("memory = %T(%L)", CHASM_MEMORY_LIMITS_CLASS_NAME, memoryArguments.joinToCode()))
            }
        }
        val arguments = buildList {
            if (runtime.interrupts) {
                add(CodeBlock.of("interrupts = true"))
            }
            if (runtime.stacktraces) {
                add(CodeBlock.of("stacktraces = true"))
            }
            if (limitsArguments.isNotEmpty()) {
                add(CodeBlock.of("limits = %T(%L)", CHASM_LIMITS_CLASS_NAME, limitsArguments.joinToCode()))
            }
        }

        return if (arguments.isEmpty()) {
            CodeBlock.of("")
        } else {
            CodeBlock.of("%T(%L)", CHASM_RUNTIME_CONFIG_CLASS_NAME, arguments.joinToCode())
        }
    }

    private fun bytesArgument(
        name: String,
        size: Bytes,
    ): CodeBlock {
        return CodeBlock.of("%L = %T(%LL)", name, CHASM_BYTES_CLASS_NAME, size.value)
    }
}

private class CompiledModule(
    private val source: CompiledSource.Generated,
) : ModuleSource {

    override val inputs = emptyList<ParameterSpec>()

    override val factories = emptyList<ParameterSpec>()

    override fun loadModule(): CodeBlock {

        val moduleClass = ClassName(source.className.substringBeforeLast('.', ""), source.className.substringAfterLast('.'))

        return CodeBlock.builder().apply {

            addStatement(
                "val module = %M(%T.COMPILED).%M(%S)",
                CHASM_COMPILED_MODULE_FUNCTION,
                moduleClass,
                CHASM_EXPECT,
                "Failed to load ${source.className}",
            )
            addStatement("val store = %M(module)", CHASM_COMPILED_STORE_FUNCTION)
        }.build()
    }

    override fun instantiate(): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "val instance = %M(store, module, allocatedImports).%M(%S)",
                CHASM_INSTANCE_FUNCTION,
                CHASM_EXPECT,
                "Failed to instantiate module",
            ).build()
    }
}

private class LoadedModule : ModuleSource {

    override val inputs = listOf(ParameterSpec("module", CHASM_MODULE_CLASS_NAME))

    override val factories = emptyList<ParameterSpec>()

    override fun loadModule(): CodeBlock {
        return CodeBlock.builder()
            .addStatement("val store = %M(module)", CHASM_COMPILED_STORE_FUNCTION)
            .build()
    }

    override fun instantiate(): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "val instance = %M(store, module, allocatedImports).%M(%S)",
                CHASM_INSTANCE_FUNCTION,
                CHASM_EXPECT,
                "Failed to instantiate module",
            ).build()
    }
}

internal val CHASM_RUNTIME_CONFIG_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "RuntimeConfig")
internal val CHASM_LIMITS_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "Limits")
internal val CHASM_CPU_LIMITS_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "CpuLimits")
internal val CHASM_METERED_CLASS_NAME = CHASM_CPU_LIMITS_CLASS_NAME.nestedClass("Metered")
internal val CHASM_MEMORY_LIMITS_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "MemoryLimits")
internal val CHASM_FUEL_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "Fuel")
internal val CHASM_BYTES_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "Bytes")
internal val CHASM_GROWTH_FAILURE_STRATEGY_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "GrowthFailureStrategy")
internal val CHASM_EXPERIMENTAL_API_CLASS_NAME = ClassName("io.github.charlietap.chasm", "ExperimentalChasmApi")
internal val CHASM_INTERNAL_API_CLASS_NAME = ClassName("io.github.charlietap.chasm", "InternalChasmApi")
internal val OPT_IN_CLASS_NAME = ClassName("kotlin", "OptIn")
internal val UNSAFE_HOST_API_CLASS_NAME = ClassName("io.github.charlietap.chasm.host", "UnsafeHostApi")
internal val WASI_EMBEDDER_HOST_CLASS_NAME = ClassName("at.released.weh.host", "EmbedderHost")
internal val WASI_PREVIEW1_BUILDER_CLASS_NAME =
    ClassName("at.released.weh.bindings.chasm.wasip1", "ChasmWasiPreview1Builder")

internal val CHASM_EXPECT = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")

private val MODULE_FACTORY_OVERRIDE_TYPE = LambdaTypeName.get(null, BYTE_ARRAY, returnType = CHASM_MODULE_CLASS_NAME)
    .copy(suspending = true)
private val INSTANCE_FACTORY_OVERRIDE_TYPE = LambdaTypeName.get(
    null,
    CHASM_STORE_CLASS_NAME,
    CHASM_MODULE_CLASS_NAME,
    List::class.asClassName().parameterizedBy(CHASM_IMPORT_CLASS_NAME),
    returnType = CHASM_INSTANCE_CLASS_NAME,
).copy(suspending = true)
