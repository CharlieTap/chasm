The IR module is designed to be a superset of the ast modules wasm encoding. It adds new instructions and other transformations
that optimise the original encoding and ultimately allow chasm to run the program faster. IR is designed to serialised, with the
intention that at some point in the future we will be able to perform these transformations at compile time and ship chasms own
bytecode alongside wasm modules for faster initialisation of modules. This is the reason we perform instruction linking and
predecoding at a later phase, because we wouldn't be able to serialise the output of these steps.
