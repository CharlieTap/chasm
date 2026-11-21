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
            addModifiers(visibilityModifier)
            if (source.suspending) addModifiers(KModifier.SUSPEND)
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
                    "val wasiImports = %T(store, %M(module)) { host = wasiHost }" +
                        ".buildRequired(providedImports = explicitImports)",
                    WASI_PREVIEW1_BUILDER_CLASS_NAME,
                    CHASM_MODULE_INFO,
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
    val suspending: Boolean

    val inputs: List<ParameterSpec>

    val factories: List<ParameterSpec>

    fun loadModule(): CodeBlock

    fun instantiate(): CodeBlock
}

private class DecodedModule(
    private val config: CodegenConfig,
    private val api: EmbeddingApi,
) : ModuleSource {

    override val suspending = api.suspendingFactories(config)

    override val inputs = listOf(ParameterSpec("binary", BYTE_ARRAY))

    override val factories = listOf(
        ParameterSpec.builder("moduleFactory", api.moduleFactoryOverrideType(suspending).copy(nullable = true))
            .defaultValue("null")
            .build(),
        ParameterSpec.builder("instanceFactory", api.instanceFactoryOverrideType(suspending).copy(nullable = true))
            .defaultValue("null")
            .build(),
    )

    override fun loadModule(): CodeBlock = CodeBlock.builder().apply {
        addStatement("val store = %M(%L)", api.storeFactory, storeConfig())
        val cpu = config.cpu
        if (cpu is CpuLimits.Metered) {
            val call = CodeBlock.of("%M(store, %LL)", api.storeFunction(StoreFunction.AddFuel), cpu.fuel.units)
            addStatement("%L", api.storeControlCall(call, "Failed to add fuel"))
        }
        addStatement(
            "val module = moduleFactory?.invoke(binary) ?: %M(binary%L).%M(%S)",
            api.moduleFactory(suspending),
            api.moduleFactoryArguments,
            CHASM_EXPECT,
            "Failed to decode binary",
        )
    }.build()

    override fun instantiate(): CodeBlock = CodeBlock.builder()
        .addStatement(
            "val instance = instanceFactory?.invoke(store, module, allocatedImports) ?: " +
                "%M(store, module, allocatedImports%L).%M(%S)",
            api.instanceFactory(suspending),
            runtimeConfig(),
            CHASM_EXPECT,
            "Failed to instantiate module",
        ).build()

    private fun storeConfig(): CodeBlock {
        val memory = config.memory
        val arguments = buildList {
            if (config.cpu is CpuLimits.Metered) add(CodeBlock.of("meterFuel = true"))
            if (config.interrupts) add(CodeBlock.of("interruptible = true"))
            config.gcStrategy?.let { strategy -> add(CodeBlock.of("gcStrategy = %T.%L", CHASM_GC_STRATEGY_CLASS_NAME, strategy.name)) }
            if (memory is MemoryLimits.Constrained) add(CodeBlock.of("limits = %L", storeLimits(memory.toStoreLimits())))
        }
        return if (arguments.isEmpty()) CodeBlock.of("") else CodeBlock.of("%T(%L)", CHASM_STORE_CONFIG_CLASS_NAME, arguments.joinToCode())
    }

    private fun storeLimits(
        limits: StoreLimits,
    ): CodeBlock = CodeBlock.of(
        "%T(gcHeapBytes = %LL, memoryPages = %L, tableElements = %L, trapOnGrowFailure = %L, stackBytes = %LL)",
        CHASM_STORE_LIMITS_CLASS_NAME,
        limits.gcHeapBytes,
        limits.memoryPages,
        limits.tableElements,
        limits.trapOnGrowFailure,
        limits.stackBytes,
    )

    private fun runtimeConfig(): CodeBlock =
        if (config.stacktraces) CodeBlock.of(", %T(debugInfo = true)", CHASM_RUNTIME_CONFIG_CLASS_NAME) else CodeBlock.of("")
}

private class CompiledModule(
    private val config: CodegenConfig,
    private val api: EmbeddingApi,
    private val tier: ExecutionTier.Aot,
) : ModuleSource {

    override val suspending = false

    override val inputs = emptyList<ParameterSpec>()

    override val factories = emptyList<ParameterSpec>()

    override fun loadModule(): CodeBlock = CodeBlock.builder().apply {
        val compiledModule = ClassName(tier.compiledModule.substringBeforeLast('.', ""), tier.compiledModule.substringAfterLast('.'))
        addStatement("val module = %T.INSTANCE", compiledModule)
        if (config.stacktraces) {
            addStatement("check(module.metadata.capabilities.stacktraces) { %S }", "${tier.compiledModule} was compiled without stack traces")
        } else {
            addStatement("check(!module.metadata.capabilities.stacktraces) { %S }", "${tier.compiledModule} was compiled with stack traces")
        }
        addStatement("val store = %M(module.metadata.storeConfig)", api.storeFactory)
        addStatement(
            "module.metadata.initialFuel?.let { fuel -> %L }",
            api.storeControlCall(CodeBlock.of("%M(store, fuel)", api.storeFunction(StoreFunction.AddFuel)), "Failed to add fuel"),
        )
    }.build()

    override fun instantiate(): CodeBlock = CodeBlock.builder()
        .addStatement(
            "val instance = %M(store, module, allocatedImports).%M(%S)",
            CHASM_AOT_INSTANCE,
            CHASM_EXPECT,
            "Failed to instantiate module",
        ).build()
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

internal val CHASM_MODULE_INFO = MemberName("io.github.charlietap.chasm.embedding", "moduleInfo")
internal val CHASM_AOT_INSTANCE = MemberName("io.github.charlietap.chasm.aot", "instance")
internal val CHASM_EXPECT = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")
