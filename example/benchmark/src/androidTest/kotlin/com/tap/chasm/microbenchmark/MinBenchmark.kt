package com.tap.chasm.microbenchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MinBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val bytes = InstrumentationRegistry.getInstrumentation().context.assets.open("min.wasm").use {
        it.readBytes()
    }
    private val store = store()
    private val instance = module(bytes)
        .flatMap {
            instance(store, it, emptyList())
        }.expect("failed to create instance")

    private val op1 = NumberValue.F32(116f)
    private val op2 = NumberValue.F32(117f)

    @Test
    fun benchmark() {
        benchmarkRule.measureRepeated {
            invoke(store, instance, "min", listOf(op1, op2))
        }
    }
}
