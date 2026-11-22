package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.BYTE_ARRAY
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.chasm.config.StoreLimits

internal class ChasmFactoryFunctionGenerator {
    operator fun invoke(
        packageName: String,
        interfaceName: String,
        visibility: FactoryVisibility,
        linksWasiPreview1: Boolean,
        config: CodegenConfig,
        api: EmbeddingApi,
    ): FunSpec {

        val source = moduleSource(config, api)
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
                ParameterSpec.builder("imports", List::class.asClassName().parameterizedBy(api.codegenImport))
                    .defaultValue("emptyList()")
                    .build(),
            )
            addParameters(source.factories)
            returns(ClassName(packageName, interfaceName))

            addCode(source.loadModule())
            if (linksWasiPreview1) {
                addStatement("val explicitImports = %M(store, imports)", api.allocateImports)
                addStatement(
                    "val wasiImports = %T(store, module) { host = wasiHost }" +
                        ".buildRequired(providedImports = explicitImports)",
                    WASI_PREVIEW1_BUILDER_CLASS_NAME,
                )
                addStatement("val allocatedImports = explicitImports + wasiImports")
            } else {
                addStatement("val allocatedImports = %M(store, imports)", api.allocateImports)
            }
            addCode(source.instantiate())
            addStatement(
                "return %T(store = store, instance = instance)",
                ClassName(packageName, interfaceName + "Impl"),
            )
        }.build()
    }

    private fun moduleSource(
        config: CodegenConfig,
        api: EmbeddingApi,
    ): ModuleSource {
        return when (val tier = config.executionTier) {
            is ExecutionTier.Aot -> CompiledModule(config, api, tier)
            is ExecutionTier.Interpreter,
            is ExecutionTier.Jit,
            -> DecodedModule(config, api)
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
    private val api: EmbeddingApi,
) : ModuleSource {

    override val inputs = listOf(ParameterSpec("binary", BYTE_ARRAY))

    override val factories = listOf(
        ParameterSpec.builder("moduleFactory", api.moduleFactoryOverrideType.copy(nullable = true))
            .defaultValue("null")
            .build(),
        ParameterSpec.builder("instanceFactory", api.instanceFactoryOverrideType.copy(nullable = true))
            .defaultValue("null")
            .build(),
    )

    override fun loadModule(): CodeBlock {
        return CodeBlock.builder().apply {

            addStatement("val store = %M(%L)", api.storeFactory(config.executionTier), storeConfig())

            val cpu = config.cpu
            if (cpu is CpuLimits.Metered) {
                addStatement("%M(store, %LL)", api.storeFunction(StoreFunction.AddFuel), cpu.fuel.units)
            }

            addStatement(
                "val module = moduleFactory?.invoke(binary) ?: %M(binary).%M(%S)",
                api.moduleFactory,
                CHASM_EXPECT,
                "Failed to decode binary",
            )
        }.build()
    }

    override fun instantiate(): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "val instance = instanceFactory?.invoke(store, module, allocatedImports) ?: " +
                    "%M(store, module, allocatedImports%L).%M(%S)",
                api.instanceFactory,
                runtimeConfig(),
                CHASM_EXPECT,
                "Failed to instantiate module",
            ).build()
    }

    private fun storeConfig(): CodeBlock {

        val memory = config.memory
        val arguments = buildList {
            if (config.cpu is CpuLimits.Metered) {
                add(CodeBlock.of("meterFuel = true"))
            }
            if (config.interrupts) {
                add(CodeBlock.of("interruptible = true"))
            }
            config.gcStrategy?.let { strategy ->
                add(CodeBlock.of("gcStrategy = %T.%L", CHASM_GC_STRATEGY_CLASS_NAME, strategy.name))
            }
            if (memory is MemoryLimits.Constrained) {
                add(CodeBlock.of("limits = %L", storeLimits(memory.toStoreLimits())))
            }
        }

        return if (arguments.isEmpty()) {
            CodeBlock.of("")
        } else {
            CodeBlock.of("%T(%L)", CHASM_STORE_CONFIG_CLASS_NAME, arguments.joinToCode())
        }
    }

    private fun storeLimits(limits: StoreLimits): CodeBlock {
        return CodeBlock.of(
            "%T(gcHeapBytes = %LL, memoryPages = %L, tableElements = %L, trapOnGrowFailure = %L, stackBytes = %LL)",
            CHASM_STORE_LIMITS_CLASS_NAME,
            limits.gcHeapBytes,
            limits.memoryPages,
            limits.tableElements,
            limits.trapOnGrowFailure,
            limits.stackBytes,
        )
    }

    private fun runtimeConfig(): CodeBlock {
        return if (config.stacktraces) {
            CodeBlock.of(", %T(debugInfo = true)", CHASM_RUNTIME_CONFIG_CLASS_NAME)
        } else {
            CodeBlock.of("")
        }
    }
}

private class CompiledModule(
    private val config: CodegenConfig,
    private val api: EmbeddingApi,
    private val tier: ExecutionTier.Aot,
) : ModuleSource {

    override val inputs = emptyList<ParameterSpec>()

    override val factories = emptyList<ParameterSpec>()

    override fun loadModule(): CodeBlock {

        val moduleClass = ClassName(tier.compiledModule.substringBeforeLast('.', ""), tier.compiledModule.substringAfterLast('.'))

        return CodeBlock.builder().apply {

            addStatement(
                "val module = %M(%T.INSTANCE).%M(%S)",
                api.compiledModuleFactory,
                moduleClass,
                CHASM_EXPECT,
                "Failed to load ${tier.compiledModule}",
            )
            addStatement("val metadata = %M(module)", api.compiledModuleMetadata)

            if (config.stacktraces) {
                addStatement("check(metadata.capabilities.stacktraces) { %S }", "${tier.compiledModule} was compiled without stack traces")
            } else {
                addStatement("check(!metadata.capabilities.stacktraces) { %S }", "${tier.compiledModule} was compiled with stack traces")
            }

            addStatement("val store = %M(metadata.storeConfig)", api.storeFactory(tier))
            addStatement("val initialFuel = metadata.initialFuel")
            beginControlFlow("if (initialFuel != null)")
            addStatement("%M(store, initialFuel)", api.storeFunction(StoreFunction.AddFuel))
            endControlFlow()
        }.build()
    }

    override fun instantiate(): CodeBlock {
        return CodeBlock.builder()
            .addStatement(
                "val instance = %M(store, module, allocatedImports).%M(%S)",
                api.instanceFactory,
                CHASM_EXPECT,
                "Failed to instantiate module",
            ).build()
    }
}

internal val CHASM_RUNTIME_CONFIG_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "RuntimeConfig")
internal val CHASM_STORE_CONFIG_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "StoreConfig")
internal val CHASM_STORE_LIMITS_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "StoreLimits")
internal val CHASM_GC_STRATEGY_CLASS_NAME = ClassName("io.github.charlietap.chasm.config", "GCStrategy")
internal val CHASM_EXPERIMENTAL_API_CLASS_NAME = ClassName("io.github.charlietap.chasm", "ExperimentalChasmApi")
internal val OPT_IN_CLASS_NAME = ClassName("kotlin", "OptIn")
internal val UNSAFE_HOST_API_CLASS_NAME = ClassName("io.github.charlietap.chasm.host", "UnsafeHostApi")
internal val WASI_EMBEDDER_HOST_CLASS_NAME = ClassName("at.released.weh.host", "EmbedderHost")
internal val WASI_PREVIEW1_BUILDER_CLASS_NAME =
    ClassName("at.released.weh.bindings.chasm.wasip1", "ChasmWasiPreview1Builder")

internal val CHASM_EXPECT = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")
