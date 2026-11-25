package io.github.charlietap.corpus.plugin.spec

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.joinToCode
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.CorpusSettings

data class GeneratedFixture(
    val name: String,
    val version: String,
    val path: String,
)

fun corpusTestFileSpec(
    testPackage: String,
    runner: String,
    settings: CorpusSettings,
    fixtures: List<GeneratedFixture>,
): FileSpec {

    val fixturesByMethod = fixtures.groupBy { fixture -> methodName(fixture.name) }
    val collisions = fixturesByMethod.filterValues { methodFixtures -> methodFixtures.size > 1 }
    require(collisions.isEmpty()) {
        collisions.entries.joinToString(prefix = "Corpus fixtures generate the same test method: ") { (method, methodFixtures) ->
            methodFixtures.joinToString(postfix = " -> $method") { fixture -> fixture.name }
        }
    }

    val runnerClassName = ClassName(runner.substringBeforeLast('.'), runner.substringAfterLast('.'))
    val assertTrue = MemberName("kotlin.test", "assertTrue")
    val testAnnotation = ClassName("kotlin.test", "Test")

    val engines = settings.engines.map { engine -> CodeBlock.of("%S", engine) }.joinToCode()
    val settingsInitializer = buildCodeBlock {
        add("%T(\n", CorpusSettings::class.asClassName())
        add("phase = %T.%L,\n", CorpusPhase::class.asClassName(), settings.phase.name)
        add("engines = setOf(%L),\n", engines)
        add("warmups = %L,\n", settings.warmups)
        add("rounds = %L,\n", settings.rounds)
        add("pin = %L,\n", settings.pin)
        add(")")
    }
    val settingsProperty = PropertySpec.builder("settings", CorpusSettings::class)
        .initializer(settingsInitializer)
        .build()

    val runFunction = FunSpec.builder("run")
        .addModifiers(KModifier.PRIVATE)
        .addParameter("version", String::class)
        .addParameter("path", String::class)
        .addStatement("val result = %T(version, path, %N).run()", runnerClassName, settingsProperty)
        .addStatement("%M(result !is %T, result.toString())", assertTrue, CorpusResult.Failure::class.asClassName())
        .build()

    val tests = fixturesByMethod.map { (method, methodFixtures) ->
        val fixture = methodFixtures.single()
        FunSpec.builder(method)
            .addAnnotation(testAnnotation)
            .addStatement("%N(%S, %S)", runFunction, fixture.version, fixture.path)
            .build()
    }

    val testClass = TypeSpec.classBuilder(TEST_CLASS)
        .addFunctions(tests)
        .addFunction(runFunction)
        .addType(
            TypeSpec.companionObjectBuilder()
                .addModifiers(KModifier.PRIVATE)
                .addProperty(settingsProperty)
                .build(),
        )
        .build()

    return FileSpec.builder(testPackage, TEST_CLASS)
        .indent("    ")
        .addType(testClass)
        .build()
}

private fun methodName(fixture: String): String {

    val sanitised = fixture.replace(Regex("[^A-Za-z0-9_]"), "_")

    return if (sanitised.first().isDigit()) "_$sanitised" else sanitised
}

private const val TEST_CLASS = "CorpusTest"
