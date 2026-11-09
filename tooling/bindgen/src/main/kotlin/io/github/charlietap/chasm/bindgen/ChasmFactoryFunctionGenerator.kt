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
import com.squareup.kotlinpoet.TypeName
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
    ): FunSpec {
        val source = moduleSource(config)
        val visibilityModifier = when (visibility) {
            FactoryVisibility.INTERNAL -> KModifier.INTERNAL
            FactoryVisibility.PUBLIC -> KModifier.PUBLIC
        }

        return FunSpec.builder(interfaceName.replaceFirstChar { character -> character.lowercaseChar() }).apply {
            addModifiers(visibilityModifier)
            if (config.generateSuspendingFactories) addModifiers(KModifier.SUSPEND)
            addParameters(source.inputs)
            if (linksWasiPreview1) {
                addParameter("wasiHost", WASI_EMBEDDER_HOST_CLASS_NAME)
            }
            addParameter(
                ParameterSpec.builder("imports", CHASM_CODEGEN_IMPORT_LIST_CLASS_NAME)
                    .defaultValue("emptyList()")
                    .build(),
            )
            addParameters(source.factories)
            returns(ClassName(packageName, interfaceName))

            addCode(source.loadModule())
            if (linksWasiPreview1) {
                addStatement("val explicitImports = %M(store, imports)", CHASM_ALLOCATE_IMPORTS)
                addStatement(
                    "val wasiImports = %T(store, %M(module)) { host = wasiHost }" +
                        ".buildRequired(providedImports = explicitImports)",
                    WASI_PREVIEW1_BUILDER_CLASS_NAME,
                    CHASM_MODULE_INFO,
                )
                addStatement("val allocatedImports = explicitImports + wasiImports")
            } else {
                addStatement("val allocatedImports = %M(store, imports)", CHASM_ALLOCATE_IMPORTS)
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
    ): ModuleSource = when (val tier = config.executionTier) {
        is ExecutionTier.Interpreter -> if (config.generateSuspendingFactories) {
            DecodedModule(
                config = config,
                moduleFactory = CHASM_SUSPEND_MODULE_FACTORY_CLASS_NAME,
                instanceFactory = CHASM_SUSPEND_INSTANCE_FACTORY_CLASS_NAME,
                module = CHASM_SUSPENDING_MODULE,
                instance = CHASM_SUSPENDING_INSTANCE,
            )
        } else {
            DecodedModule(
                config = config,
                moduleFactory = CHASM_MODULE_FACTORY_CLASS_NAME,
                instanceFactory = CHASM_INSTANCE_FACTORY_CLASS_NAME,
                module = CHASM_MODULE,
                instance = CHASM_INSTANCE,
            )
        }
        is ExecutionTier.Jit -> DecodedModule(
            config = config,
            moduleFactory = CHASM_JIT_MODULE_FACTORY_TYPE,
            instanceFactory = CHASM_JIT_INSTANCE_FACTORY_TYPE,
            module = CHASM_JIT_MODULE,
            instance = CHASM_JIT_INSTANCE,
        )
        is ExecutionTier.Aot -> CompiledModule(config, tier)
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
    moduleFactory: TypeName,
    instanceFactory: TypeName,
    private val module: MemberName,
    private val instance: MemberName,
) : ModuleSource {

    override val inputs = listOf(ParameterSpec("binary", BYTE_ARRAY))

    override val factories = listOf(
        ParameterSpec.builder("moduleFactory", moduleFactory.copy(nullable = true)).defaultValue("null").build(),
        ParameterSpec.builder("instanceFactory", instanceFactory.copy(nullable = true)).defaultValue("null").build(),
    )

    override fun loadModule(): CodeBlock = CodeBlock.builder().apply {
        addStatement("val store = %M(%L)", CHASM_STORE, storeConfig())
        val cpu = config.cpu
        if (cpu is CpuLimits.Metered) {
            addStatement("%M(store, %LL).%M(%S)", CHASM_ADD_FUEL, cpu.fuel.units, CHASM_EXPECT, "Failed to add fuel")
        }
        addStatement(
            "val module = moduleFactory?.invoke(binary) ?: %M(binary).%M(%S)",
            module,
            CHASM_EXPECT,
            "Failed to decode binary",
        )
    }.build()

    override fun instantiate(): CodeBlock = CodeBlock.builder()
        .addStatement(
            "val instance = instanceFactory?.invoke(store, module, allocatedImports) ?: " +
                "%M(store, module, allocatedImports%L).%M(%S)",
            instance,
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
    private val tier: ExecutionTier.Aot,
) : ModuleSource {

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
        addStatement("val store = %M(module.metadata.storeConfig)", CHASM_STORE)
        addStatement(
            "module.metadata.initialFuel?.let { fuel -> %M(store, fuel).%M(%S) }",
            CHASM_ADD_FUEL,
            CHASM_EXPECT,
            "Failed to add fuel",
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
internal val CHASM_STORE_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Store")
internal val CHASM_MODULE_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Module")
internal val CHASM_IMPORT_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Import")
internal val CHASM_INSTANCE_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Instance")
internal val CHASM_MEMORY_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Memory")
internal val CHASM_GLOBAL_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "Global")
internal val CHASM_PREPARED_FUNCTION_CLASS_NAME = ClassName("io.github.charlietap.chasm.embedding.shapes", "PreparedFunction")
internal val CHASM_EXECUTION_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "ExecutionValue")
internal val CHASM_I32_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "I32")
internal val CHASM_I64_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "I64")
internal val CHASM_F32_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "F32")
internal val CHASM_F64_VALUE_CLASS_NAME = ClassName("io.github.charlietap.chasm.runtime.value", "NumberValue", "F64")
internal val CHASM_CODEGEN_IMPORT_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "CodegenImport")
internal val CHASM_CODEGEN_IMPORT_LIST_CLASS_NAME =
    List::class.asClassName().parameterizedBy(CHASM_CODEGEN_IMPORT_CLASS_NAME)
internal val CHASM_MODULE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "ModuleFactory")
internal val CHASM_SUSPEND_MODULE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "SuspendModuleFactory")
internal val CHASM_INSTANCE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "InstanceFactory")
internal val CHASM_SUSPEND_INSTANCE_FACTORY_CLASS_NAME =
    ClassName("io.github.charlietap.chasm.embedding.codegen", "SuspendInstanceFactory")
internal val CHASM_EXPERIMENTAL_API_CLASS_NAME = ClassName("io.github.charlietap.chasm", "ExperimentalChasmApi")
internal val OPT_IN_CLASS_NAME = ClassName("kotlin", "OptIn")
internal val WASI_EMBEDDER_HOST_CLASS_NAME = ClassName("at.released.weh.host", "EmbedderHost")
internal val WASI_PREVIEW1_BUILDER_CLASS_NAME =
    ClassName("at.released.weh.bindings.chasm.wasip1", "ChasmWasiPreview1Builder")

internal val CHASM_STORE = MemberName("io.github.charlietap.chasm.embedding", "store")
internal val CHASM_MODULE = MemberName("io.github.charlietap.chasm.embedding", "module")
internal val CHASM_MODULE_INFO = MemberName("io.github.charlietap.chasm.embedding", "moduleInfo")
internal val CHASM_INSTANCE = MemberName("io.github.charlietap.chasm.embedding", "instance")
internal val CHASM_JIT_MODULE = MemberName("io.github.charlietap.chasm.jit", "module")
internal val CHASM_JIT_INSTANCE = MemberName("io.github.charlietap.chasm.jit", "instance")
internal val CHASM_AOT_INSTANCE = MemberName("io.github.charlietap.chasm.aot", "instance")
internal val CHASM_SUSPENDING_MODULE = MemberName("io.github.charlietap.chasm.coroutines", "module")
internal val CHASM_SUSPENDING_INSTANCE = MemberName("io.github.charlietap.chasm.coroutines", "instance")
internal val CHASM_ALLOCATE_IMPORTS = MemberName("io.github.charlietap.chasm.embedding.codegen", "allocateImports")
internal val CHASM_EXPECT = MemberName("io.github.charlietap.chasm.embedding.shapes", "expect")
internal val CHASM_ADD_FUEL = MemberName("io.github.charlietap.chasm.embedding", "addFuel")
internal val CHASM_REMAINING_FUEL = MemberName("io.github.charlietap.chasm.embedding", "remainingFuel")
internal val CHASM_INTERRUPT = MemberName("io.github.charlietap.chasm.embedding", "interrupt")
internal const val CHASM_ADD_FUEL_ALIAS = "chasmAddFuel"
internal const val CHASM_REMAINING_FUEL_ALIAS = "chasmRemainingFuel"
internal const val CHASM_INTERRUPT_ALIAS = "chasmInterrupt"

private val CHASM_JIT_MODULE_FACTORY_TYPE = LambdaTypeName.get(null, BYTE_ARRAY, returnType = CHASM_MODULE_CLASS_NAME)
private val CHASM_JIT_INSTANCE_FACTORY_TYPE = LambdaTypeName.get(
    null,
    CHASM_STORE_CLASS_NAME,
    CHASM_MODULE_CLASS_NAME,
    List::class.asClassName().parameterizedBy(CHASM_IMPORT_CLASS_NAME),
    returnType = CHASM_INSTANCE_CLASS_NAME,
)
