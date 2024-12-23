package com.tap.chasm.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.embedding.shapes.Value.Number

import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MinBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    val bytes = InstrumentationRegistry.getInstrumentation().context.assets.open("min.wasm").use {
        it.readBytes()
    }
    val store = store()
    val instance = module(bytes)
        .flatMap {
            instance(store, it, emptyList())
        }.expect("failed to create instance")

    val op1 = Value.Number.F32(116f)
    val op2 = Value.Number.F32(117f)

    @Test
    fun benchmark() {
        benchmarkRule.measureRepeated {
            val res = invoke(store, instance, "min", listOf(op1, op2))
        }
    }
}
