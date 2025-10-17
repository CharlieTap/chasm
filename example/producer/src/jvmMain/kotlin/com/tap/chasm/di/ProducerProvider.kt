package com.tap.chasm.di

import com.test.chasm.producer.ProducerService
import com.test.chasm.producer.ProducerServiceImpl
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import io.github.charlietap.chasm.vm.FunctionType
import io.github.charlietap.chasm.vm.NumberType
import io.github.charlietap.chasm.vm.ValueType
import io.github.charlietap.chasm.vm.WasmVirtualMachine
import io.github.charlietap.chasm.vm.codegen.CodegenImport
import io.github.charlietap.chasm.vm.codegen.FunctionImport
import java.io.FileNotFoundException
import kotlin.random.Random

@ContributesTo(AppScope::class)
interface ProducerProvider {

    @Provides
    fun provideProducerService(): ProducerService {
        val bytes = ProducerProvider::class.java.classLoader.getResourceAsStream("producer.wasm")?.use {
            it.readBytes()
        } ?: throw FileNotFoundException("Could not find resource 'producer.wasm' on the classpath")
        return ProducerServiceImpl(bytes, imports())
    }

    private fun imports(): List<CodegenImport> {
        return listOf(
            FunctionImport(
                "wasi_snapshot_preview1",
                "random_get",
                FunctionType(
                    listOf(
                        ValueType.Number(NumberType.I32),
                        ValueType.Number(NumberType.I32),
                    ),
                    listOf(
                        ValueType.Number(NumberType.I32),
                    ),
                ),
                { params ->
                    val random = Random(117).nextInt()
                    listOf(WasmVirtualMachine.Value.I32(random))
                }
            )
        )
    }
}
