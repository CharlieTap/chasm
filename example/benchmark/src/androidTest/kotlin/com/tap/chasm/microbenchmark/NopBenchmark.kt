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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NopBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val bytes = InstrumentationRegistry.getInstrumentation().context.assets.open("nop.wasm").use {
        it.readBytes()
    }
    private val store = store()
    private val instance = module(bytes)
        .flatMap {
            instance(store, it, emptyList())
        }.expect("failed to create instance")

    @Test
    fun benchmark() {
        benchmarkRule.measureRepeated {
            invoke(store, instance, "nop", emptyList())
        }
    }
}
