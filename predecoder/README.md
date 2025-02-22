## Predecoder

Predecoding is an interpreter optimisation that transforms instructions into function references prior to executing the program.

It can be best understood by looking at an implementation of a naive interpreter

```kotlin
val instructions: List<Instruction>

instructions.forEach { instruction ->
    when(instruction) {
        is Instruction.Add -> executeAdd()
        is Instruction.Sub -> executeSub()
        ...
    }
}
```

In order to dispatch an instruction the CPU must:

- Load the instruction
- Compare and branch to the correct instruction implementation
- Execute the instruction
- Jump to the top of the loop

The problem here is that the CPUs branch predictor will select the correct branch very very rarely, after all the probability is
dependent on the amount of instructions supported and the distribution of those instructions across a given program. Wrongly predicting
a branch causes the CPU to throw away its instruction pipeline, invalidates any speculative execution and ultimately results in tons of wasted
cycles. This becomes prohibitively expensive when every iteration of the dispatch loop is paying this cost.

Instead, we 'predecode' instructions when the module is instantiated, replacing each instruction with a self-contained lambda. Resulting in a
dispatch loop like below

```kotlin
typealias DispatchableInstruction = () -> Unit

val instructions: List<DispatchableInstruction>

instructions.forEach { instruction ->
    instruction()
}
```

### Predecoding in Chasm

More generally, chasm uses predecoding as an opportunity to precompute any work that can be done ahead of time.

For example, wasm instructions often contain indexes which point to different pieces of state inside a module instance.

```kotlin
data class I32Load(
    override val memoryIndex: Index.MemoryIndex,
    override val memArg: MemArg,
) : MemoryInstruction
```

At runtime the wasm specification defines that you should use the index to resolve a MemoryInstance and then perform the
load on this instance. However, Wasm modules are validated and instantiated before execution, thie means we can safely resolve
memory indices AOT. This eliminates the need for per-instruction lookups during execution.

```kotlin
data class I32Load(
    override val memory: MemoryInstance,
    override val memArg: MemArg,
) : LinkedInstruction
```

Predecoders transform IRInstructions into LinkedInstructions, which are then wrapped in dispatchable lambdas.

