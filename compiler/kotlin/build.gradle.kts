plugins {
    alias(libs.plugins.conventions.kmp)
    alias(libs.plugins.conventions.linting)
}

// Derive the binding catalogue from the actual executor signatures.
// The catalogue names functions; it does not duplicate their implementations.
val executorSources = layout.projectDirectory.dir("../../executor/invoker/src/commonMain/kotlin/io/github/charlietap/chasm/executor/invoker/instruction")
val generatedCatalogue = layout.buildDirectory.dir("generated/executor-catalogue")
val generateExecutorCatalogue = tasks.register("generateExecutorCatalogue") {
    inputs.dir(executorSources)
    outputs.dir(generatedCatalogue)
    doLast {
        val signature = Regex("internal\\s+(?:inline\\s+)?fun\\s+(\\w+)\\(\\s*vstack:\\s*ValueStack,\\s*context:\\s*ExecutionContext,\\s*instruction:\\s*((?:Numeric|Memory|Parametric|Variable|Admin)Instruction\\.\\w+),", RegexOption.MULTILINE)
        val entries = sortedMapOf<String, String>()
        executorSources.asFile.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
            val source = file.readText()
            val packageName = Regex("(?m)^package (.+)$").find(source)!!.groupValues[1]
            signature.findAll(source).forEach { match ->
                val function = "$packageName.${match.groupValues[1]}"
                val previous = entries.put(match.groupValues[2], function)
                check(previous == null || previous == function) { "Duplicate executor for ${match.groupValues[2]}" }
            }
        }
        check(entries.size > 300) { "Missing executor signatures: ${entries.size}" }
        val output = generatedCatalogue.get().file("io/github/charlietap/chasm/compiler/kotlin/ExecutorCatalogue.kt").asFile
        output.parentFile.mkdirs()
        output.writeText(buildString {
            appendLine("// Generated from executor declarations. Do not edit.")
            appendLine("package io.github.charlietap.chasm.compiler.kotlin")
            appendLine("import io.github.charlietap.chasm.runtime.instruction.*")
            appendLine("internal fun executorCall(instruction: LinkedInstruction): ExecutorCall? = when (instruction) {")
            entries.forEach { (type, function) ->
                appendLine("    is $type -> ExecutorCall(\"$type\", \"$function\")")
            }
            appendLine("    else -> null")
            appendLine("}")
        })

        // The scalar adapters declare their operand/result types and refer to
        // canonical value operations. Derive slot effects from those adapters,
        // without copying arithmetic or conversion semantics into this module.
        val scalarTypes = mapOf("Int" to "I32", "Long" to "I64", "Float" to "F32", "Double" to "F64", "Boolean" to "BOOL")
        val adapterTypes = mutableMapOf<String, Pair<List<String>, String>>()
        val supportSignature = Regex("internal inline fun (execute\\w+)\\((.*?)\\) \\{", RegexOption.DOT_MATCHES_ALL)
        val operationSignature = Regex("operation: \\(([^)]+)\\) -> (\\w+)")
        executorSources.asFile.walkTopDown().filter { it.name.endsWith("Support.kt") }.forEach { file ->
            supportSignature.findAll(file.readText()).forEach { match ->
                operationSignature.find(match.groupValues[2])?.let { operation ->
                    adapterTypes[match.groupValues[1]] = operation.groupValues[1].split(", ") to operation.groupValues[2]
                }
            }
        }
        val scalarEntries = sortedMapOf<String, String>()
        val scalarAdapter = Regex("internal\\s+inline\\s+fun\\s+\\w+\\(\\s*vstack:\\s*ValueStack,\\s*context:\\s*ExecutionContext,\\s*instruction:\\s*(NumericInstruction\\.\\w+),\\s*\\)\\s*=\\s*(execute\\w+)\\(([^\\n]+)\\)")
        executorSources.asFile.walkTopDown().filter { it.extension == "kt" }.forEach { file ->
            val source = file.readText()
            val packageName = Regex("(?m)^package (.+)$").find(source)!!.groupValues[1]
            val imports = Regex("(?m)^import (.+)$").findAll(source).associate { it.groupValues[1].substringAfterLast('.') to it.groupValues[1] }
            scalarAdapter.findAll(source).forEach adapter@{ match ->
                val (inputTypes, resultType) = adapterTypes[match.groupValues[2]] ?: return@adapter
                val arguments = match.groupValues[3].split(", ")
                val operation = arguments.last()
                if (!operation.contains("::")) return@adapter
                check(arguments.size == inputTypes.size + 3 && arguments[0] == "vstack" && arguments[1] == "instruction.destinationSlot")
                val operands = arguments.subList(2, arguments.lastIndex).zip(inputTypes).map { (argument, type) ->
                    val field = argument.removePrefix("instruction.")
                    check(argument == "instruction.$field" && field.all { it.isLetterOrDigit() })
                    val kind = scalarTypes.getValue(type)
                    if (field.endsWith("Slot")) "KotlinValueInput.Slot(instruction.$field, KotlinValueType.$kind)" else "KotlinValueInput.Field(\"$field\", KotlinValueType.$kind)"
                }
                val expression = if (operation.startsWith("::")) {
                    val name = operation.removePrefix("::")
                    "${imports[name] ?: "$packageName.$name"}(${inputTypes.indices.joinToString { "@$it@" }})"
                } else {
                    // Existing floating-point extensions retain their Wasm
                    // NaN and signed-zero behavior through the same functions.
                    val name = operation.substringAfter("::")
                    "(@0@).$name(${inputTypes.indices.drop(1).joinToString { "@$it@" }})"
                }
                scalarEntries[match.groupValues[1]] = "KotlinValueInstruction(instruction.destinationSlot, listOf(${operands.joinToString()}), \"$expression\", KotlinValueType.${scalarTypes.getValue(resultType)})"
            }
        }
        check(scalarEntries.size > 250) { "Missing scalar adapters: ${scalarEntries.size}" }
        output.resolveSibling("ValueCatalogue.kt").writeText(buildString {
            appendLine("// Generated from scalar adapter declarations. Do not edit.")
            appendLine("package io.github.charlietap.chasm.compiler.kotlin")
            appendLine("import io.github.charlietap.chasm.runtime.instruction.*")
            appendLine("internal fun numericValueInstruction(instruction: LinkedInstruction): KotlinValueInstruction? = when (instruction) {")
            scalarEntries.forEach { (type, expression) -> appendLine("    is $type -> $expression") }
            appendLine("    else -> null")
            appendLine("}")
        })
    }
}

kotlin {
    android { namespace = "io.github.charlietap.chasm.compiler.kotlin" }

    sourceSets {
        commonMain { kotlin.srcDir(generateExecutorCatalogue.map { generatedCatalogue }) }
        commonMain.dependencies {
            api(projects.runtime.core)
        }
        jvmMain.dependencies {
            implementation(projects.executor.invoker)
            implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:${libs.versions.kotlin.get()}")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(projects.chasm)
            implementation(projects.compiler)
            implementation(projects.test.fixture.ast)
            implementation(libs.kotlin.test)
        }
    }
}
