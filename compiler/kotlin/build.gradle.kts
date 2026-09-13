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
