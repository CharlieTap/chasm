(module $<kotlin210inlinebug:wasmAssertk>
  (type (;0;) (func (param i32 i32) (result i32)))
  (type (;1;) (func (param i32 i32)))
  (type (;2;) (func (param i32) (result i32)))
  (type (;3;) (func (param i64) (result i64)))
  (type (;4;) (func (param i32) (result i64)))
  (type (;5;) (func (param i64) (result i32)))
  (type (;6;) (func))
  (type (;7;) (func (param i64 i64) (result i32)))
  (type (;8;) (func (param i32)))
  (type (;9;) (func (result i64)))
  (type (;10;) (func))
  (type (;11;) (func (param externref)))
  (rec
    (type $kotlin.CharSequence.itable (;12;) (struct (field $<get-length> (ref null $"#type153 ")) (field $get (ref null $"#type154 "))))
    (type $kotlin.Comparable.itable (;13;) (struct))
    (type $kotlin.collections.Collection.itable (;14;) (struct (field $<get-size> (ref null $"#type153 ")) (field $isEmpty (ref null $"#type153 "))))
    (type $kotlin.collections.MutableCollection.itable (;15;) (struct (field $iterator (ref null $"#type161 "))))
    (type $kotlin.collections.Iterable.itable (;16;) (struct (field $iterator (ref null $"#type161 "))))
    (type $kotlin.collections.MutableIterable.itable (;17;) (struct))
    (type $kotlin.collections.Iterator.itable (;18;) (struct (field $next (ref null $"#type161 ")) (field $hasNext (ref null $"#type153 "))))
    (type $kotlin.collections.ListIterator.itable (;19;) (struct))
    (type $kotlin.collections.MutableListIterator.itable (;20;) (struct))
    (type $kotlin.collections.MutableIterator.itable (;21;) (struct))
    (type $kotlin.collections.List.itable (;22;) (struct (field $<get-size> (ref null $"#type153 ")) (field $isEmpty (ref null $"#type153 ")) (field $iterator (ref null $"#type161 ")) (field $get (ref null $"#type169 "))))
    (type $kotlin.collections.MutableList.itable (;23;) (struct))
    (type $kotlin.collections.RandomAccess.itable (;24;) (struct))
    (type $kotlin.text.Appendable.itable (;25;) (struct (field $append (ref null $"#type169 ")) (field $"#field1 append" (ref null $"#type171 "))))
    (type $kotlin.Function.itable (;26;) (struct))
    (type $kotlin.enums.EnumEntries.itable (;27;) (struct))
    (type $kotlin.Any.vtable (;28;) (sub (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.io.Serializable.itable (;29;) (struct))
    (type $kotlin.Function1.itable (;30;) (struct (field $invoke (ref null $"#type171 "))))
    (type $kotlin.Function0.itable (;31;) (struct (field $invoke (ref null $"#type161 "))))
    (type $kotlin.Any (;32;) (sub (struct (field $vtable (ref $kotlin.Any.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.wasm.internal.WasmAnyArray (;33;) (array (mut (ref null $kotlin.Any))))
    (type $kotlin.wasm.internal.WasmLongArray (;34;) (array (mut i64)))
    (type $kotlin.wasm.internal.WasmCharArray (;35;) (array (mut i16)))
    (type $kotlin.wasm.internal.WasmShortArray (;36;) (array (mut i16)))
    (type $classITable (;37;) (struct (field $kotlin.itable (ref null $kotlin.CharSequence.itable)) (field $kotlin.text.itable (ref null $kotlin.text.Appendable.itable)) (field $"#field2 kotlin.itable" (ref null $kotlin.Comparable.itable))))
    (type $"#type38 classITable" (@name "classITable") (;38;) (struct (field $kotlin.collections.itable (ref null $kotlin.collections.Iterable.itable)) (field $"#field1 kotlin.collections.itable" (ref null $kotlin.collections.Collection.itable)) (field $"#field2 kotlin.collections.itable" (ref null $kotlin.collections.List.itable)) (field $"#field3 kotlin.collections.itable" (ref null $kotlin.collections.MutableIterable.itable)) (field $"#field4 kotlin.collections.itable" (ref null $kotlin.collections.MutableCollection.itable)) (field $"#field5 kotlin.collections.itable" (ref null $kotlin.collections.MutableList.itable)) (field $"#field6 kotlin.collections.itable" (ref null $kotlin.collections.RandomAccess.itable)) (field $kotlin.enums.itable (ref null $kotlin.enums.EnumEntries.itable)) (field $kotlin.io.itable (ref null $kotlin.io.Serializable.itable))))
    (type $"#type39 classITable" (@name "classITable") (;39;) (struct (field $kotlin.collections.itable (ref null $kotlin.collections.Iterator.itable)) (field $"#field1 kotlin.collections.itable" (ref null $kotlin.collections.MutableIterator.itable)) (field $"#field2 kotlin.collections.itable" (ref null $kotlin.collections.ListIterator.itable)) (field $"#field3 kotlin.collections.itable" (ref null $kotlin.collections.MutableListIterator.itable))))
    (type $"#type40 classITable" (@name "classITable") (;40;) (struct (field $kotlin.itable (ref null $kotlin.Function.itable)) (field $"#field1 kotlin.itable" (ref null $kotlin.Function1.itable)) (field $"#field2 kotlin.itable" (ref null $kotlin.Function0.itable))))
    (type $Failure.itable (;41;) (struct))
    (type $"#type42 classITable" (@name "classITable") (;42;) (struct (field $.itable (ref null $Failure.itable))))
    (type $kotlin.Number.vtable (;43;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.collections.IteratorImpl.vtable (;44;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $hasNext (ref null $"#type153 ")) (field $next (ref null $"#type161 ")))))
    (type $kotlin.collections.Companion.vtable (;45;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.collections.Itr.vtable (;46;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $hasNext (ref null $"#type153 ")) (field $next (ref null $"#type161 ")))))
    (type $kotlin.text.StringBuilder.vtable (;47;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-length> (ref null $"#type153 ")) (field $get (ref null $"#type154 ")) (field $append (ref null $"#type175 ")) (field $"#field4 append" (ref null $"#type169 ")) (field $"#field5 append" (ref null $"#type176 ")) (field $"#field6 append" (ref null $"#type171 ")) (field $"#field7 append" (ref null $"#type177 ")))))
    (type $kotlin.collections.AbstractCollection$toString$lambda.vtable (;48;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $invoke (ref null $"#type171 ")) (field $"#field2 invoke" (ref null $"#type171 ")))))
    (type $kotlin.collections.AbstractCollection.vtable (;49;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-size> (ref null $"#type153 ")) (field $iterator (ref null $"#type161 ")) (field $isEmpty (ref null $"#type153 ")))))
    (type $"#type50 kotlin.collections.IteratorImpl.vtable" (@name "kotlin.collections.IteratorImpl.vtable") (;50;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $hasNext (ref null $"#type153 ")) (field $next (ref null $"#type161 ")))))
    (type $"#type51 kotlin.collections.Companion.vtable" (@name "kotlin.collections.Companion.vtable") (;51;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.random.Random.vtable (;52;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $nextBits (ref null $"#type154 ")) (field $nextInt (ref null $"#type153 ")) (field $"#field3 nextInt" (ref null $"#type195 ")))))
    (type $kotlin.random.Companion.vtable (;53;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.Companion.vtable (;54;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.UInt.vtable (;55;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $"#type56 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;56;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.ULong.vtable (;57;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.Array.vtable (;58;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.LongArray.vtable (;59;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.ShortArray.vtable (;60;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.CharArray.vtable (;61;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $"#type62 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;62;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.Char.vtable (;63;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $"#type64 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;64;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.Enum.vtable (;65;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $"#type66 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;66;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $"#type67 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;67;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.String.vtable (;68;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-length> (ref null $"#type153 ")) (field $get (ref null $"#type154 ")))))
    (type $kotlin.assert$lambda.vtable (;69;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $invoke (ref null $"#type161 ")) (field $"#field2 invoke" (ref null $"#type161 ")))))
    (type $kotlin.Unit.vtable (;70;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.wasm.unsafe.Pointer.vtable (;71;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.wasm.unsafe.MemoryAllocator.vtable (;72;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $allocate (ref null $"#type154 ")))))
    (type $kotlin.Throwable.vtable (;73;) (sub $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.Number (;74;) (sub $kotlin.Any (struct (field $vtable (ref $kotlin.Number.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.collections.IteratorImpl (;75;) (sub $kotlin.Any (struct (field $vtable (ref $kotlin.collections.IteratorImpl.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $index (mut i32)) (field $last (mut i32)) (field $expectedModCount (mut i32)) (field $$this (mut (ref null $kotlin.collections.AbstractMutableList))))))
    (type $kotlin.collections.Companion (;76;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.collections.Companion.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $Empty (mut (ref null $kotlin.collections.ArrayList))))))
    (type $kotlin.collections.Itr (;77;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.collections.Itr.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $list (mut (ref null $kotlin.collections.ArrayList))) (field $index (mut i32)) (field $lastIndex (mut i32)) (field $expectedModCount (mut i32)))))
    (type $kotlin.text.StringBuilder (;78;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.text.StringBuilder.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $array (mut (ref null $kotlin.CharArray))) (field $_length (mut i32)))))
    (type $kotlin.collections.AbstractCollection$toString$lambda (;79;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.collections.AbstractCollection$toString$lambda.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $this$0 (mut (ref null $kotlin.collections.AbstractCollection))))))
    (type $kotlin.collections.AbstractCollection (;80;) (sub $kotlin.Any (struct (field $vtable (ref $kotlin.collections.AbstractCollection.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $"#type81 kotlin.collections.IteratorImpl" (@name "kotlin.collections.IteratorImpl") (;81;) (sub $kotlin.Any (struct (field $vtable (ref $"#type50 kotlin.collections.IteratorImpl.vtable")) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $index (mut i32)) (field $$this (mut (ref null $kotlin.collections.AbstractList))))))
    (type $"#type82 kotlin.collections.Companion" (@name "kotlin.collections.Companion") (;82;) (sub final $kotlin.Any (struct (field $vtable (ref $"#type51 kotlin.collections.Companion.vtable")) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $maxArraySize (mut i32)))))
    (type $kotlin.random.Random (;83;) (sub $kotlin.Any (struct (field $vtable (ref $kotlin.random.Random.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.random.Companion (;84;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.random.Companion.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $serialVersionUID (mut i64)))))
    (type $kotlin.Companion (;85;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.Companion.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $MIN_VALUE (mut i32)) (field $MAX_VALUE (mut i32)) (field $SIZE_BYTES (mut i32)) (field $SIZE_BITS (mut i32)))))
    (type $kotlin.UInt (;86;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.UInt.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $data (mut i32)))))
    (type $"#type87 kotlin.Companion" (@name "kotlin.Companion") (;87;) (sub final $kotlin.Any (struct (field $vtable (ref $"#type56 kotlin.Companion.vtable")) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $MIN_VALUE (mut i64)) (field $MAX_VALUE (mut i64)) (field $SIZE_BYTES (mut i32)) (field $SIZE_BITS (mut i32)))))
    (type $kotlin.ULong (;88;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.ULong.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $data (mut i64)))))
    (type $kotlin.Array (;89;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.Array.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $storage (mut (ref null $kotlin.wasm.internal.WasmAnyArray))))))
    (type $kotlin.LongArray (;90;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.LongArray.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $storage (mut (ref null $kotlin.wasm.internal.WasmLongArray))))))
    (type $kotlin.ShortArray (;91;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.ShortArray.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $storage (mut (ref null $kotlin.wasm.internal.WasmShortArray))))))
    (type $kotlin.CharArray (;92;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.CharArray.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $storage (mut (ref null $kotlin.wasm.internal.WasmCharArray))))))
    (type $"#type93 kotlin.Companion" (@name "kotlin.Companion") (;93;) (sub final $kotlin.Any (struct (field $vtable (ref $"#type62 kotlin.Companion.vtable")) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $MIN_VALUE (mut i16)) (field $MAX_VALUE (mut i16)) (field $MIN_HIGH_SURROGATE (mut i16)) (field $MAX_HIGH_SURROGATE (mut i16)) (field $MIN_LOW_SURROGATE (mut i16)) (field $MAX_LOW_SURROGATE (mut i16)) (field $MIN_SURROGATE (mut i16)) (field $MAX_SURROGATE (mut i16)) (field $SIZE_BYTES (mut i32)) (field $SIZE_BITS (mut i32)) (field $MIN_SUPPLEMENTARY_CODE_POINT (mut i32)) (field $MIN_CODE_POINT (mut i32)) (field $MAX_CODE_POINT (mut i32)) (field $MIN_RADIX (mut i32)) (field $MAX_RADIX (mut i32)))))
    (type $kotlin.Char (;94;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.Char.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $value (mut i16)))))
    (type $"#type95 kotlin.Companion" (@name "kotlin.Companion") (;95;) (sub final $kotlin.Any (struct (field $vtable (ref $"#type64 kotlin.Companion.vtable")) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.Enum (;96;) (sub $kotlin.Any (struct (field $vtable (ref $kotlin.Enum.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $name (mut (ref null $kotlin.String))) (field $ordinal (mut i32)))))
    (type $"#type97 kotlin.Companion" (@name "kotlin.Companion") (;97;) (sub final $kotlin.Any (struct (field $vtable (ref $"#type66 kotlin.Companion.vtable")) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $MIN_VALUE (mut i32)) (field $MAX_VALUE (mut i32)) (field $SIZE_BYTES (mut i32)) (field $SIZE_BITS (mut i32)))))
    (type $"#type98 kotlin.Companion" (@name "kotlin.Companion") (;98;) (sub final $kotlin.Any (struct (field $vtable (ref $"#type67 kotlin.Companion.vtable")) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.String (;99;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.String.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $leftIfInSum (mut (ref null $kotlin.String))) (field $length (mut i32)) (field $_chars (mut (ref null $kotlin.wasm.internal.WasmCharArray))))))
    (type $kotlin.assert$lambda (;100;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.assert$lambda.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.Unit (;101;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.Unit.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.wasm.unsafe.Pointer (;102;) (sub final $kotlin.Any (struct (field $vtable (ref $kotlin.wasm.unsafe.Pointer.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $address (mut i32)))))
    (type $kotlin.wasm.unsafe.MemoryAllocator (;103;) (sub $kotlin.Any (struct (field $vtable (ref $kotlin.wasm.unsafe.MemoryAllocator.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.Throwable (;104;) (sub $kotlin.Any (struct (field $vtable (ref $kotlin.Throwable.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $SoftFailure.vtable (;105;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $invoke (ref null $"#type269 ")))))
    (type $main$lambda.vtable (;106;) (sub final $kotlin.Any.vtable (struct (field $toString (ref null $"#type170 ")) (field $invoke (ref null $"#type269 ")) (field $"#field2 invoke" (ref null $"#type161 ")))))
    (type $SoftFailure (;107;) (sub final $kotlin.Any (struct (field $vtable (ref $SoftFailure.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $failures (mut (ref null $kotlin.Any))))))
    (type $main$lambda (;108;) (sub final $kotlin.Any (struct (field $vtable (ref $main$lambda.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.collections.AbstractMutableCollection.vtable (;109;) (sub $kotlin.collections.AbstractCollection.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-size> (ref null $"#type153 ")) (field $iterator (ref null $"#type161 ")) (field $isEmpty (ref null $"#type153 ")) (field $"#field4 iterator" (ref null $"#type161 ")))))
    (type $kotlin.collections.AbstractList.vtable (;110;) (sub $kotlin.collections.AbstractCollection.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-size> (ref null $"#type153 ")) (field $iterator (ref null $"#type161 ")) (field $isEmpty (ref null $"#type153 ")) (field $get (ref null $"#type169 ")))))
    (type $kotlin.random.Default.vtable (;111;) (sub final $kotlin.random.Random.vtable (struct (field $toString (ref null $"#type170 ")) (field $nextBits (ref null $"#type154 ")) (field $nextInt (ref null $"#type153 ")) (field $"#field3 nextInt" (ref null $"#type195 ")))))
    (type $kotlin.random.XorWowRandom.vtable (;112;) (sub final $kotlin.random.Random.vtable (struct (field $toString (ref null $"#type170 ")) (field $nextBits (ref null $"#type154 ")) (field $nextInt (ref null $"#type153 ")) (field $"#field3 nextInt" (ref null $"#type195 ")))))
    (type $kotlin.Int.vtable (;113;) (sub final $kotlin.Number.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.wasm.internal.CharCodes.vtable (;114;) (sub final $kotlin.Enum.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.Error.vtable (;115;) (sub $kotlin.Throwable.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.Exception.vtable (;116;) (sub $kotlin.Throwable.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.wasm.unsafe.ScopedMemoryAllocator.vtable (;117;) (sub final $kotlin.wasm.unsafe.MemoryAllocator.vtable (struct (field $toString (ref null $"#type170 ")) (field $allocate (ref null $"#type154 ")))))
    (type $kotlin.wasm.WasiError.vtable (;118;) (sub final $kotlin.Throwable.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.wasm.WasiErrorCode.vtable (;119;) (sub final $kotlin.Enum.vtable (struct (field $toString (ref null $"#type170 ")))))
    (type $kotlin.collections.AbstractMutableCollection (;120;) (sub $kotlin.collections.AbstractCollection (struct (field $vtable (ref $kotlin.collections.AbstractMutableCollection.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.collections.AbstractList (;121;) (sub $kotlin.collections.AbstractCollection (struct (field $vtable (ref $kotlin.collections.AbstractList.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)))))
    (type $kotlin.random.Default (;122;) (sub final $kotlin.random.Random (struct (field $vtable (ref $kotlin.random.Default.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $defaultRandom (mut (ref null $kotlin.random.Random))))))
    (type $kotlin.random.XorWowRandom (;123;) (sub final $kotlin.random.Random (struct (field $vtable (ref $kotlin.random.XorWowRandom.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $x (mut i32)) (field $y (mut i32)) (field $z (mut i32)) (field $w (mut i32)) (field $v (mut i32)) (field $addend (mut i32)))))
    (type $kotlin.Int (;124;) (sub final $kotlin.Number (struct (field $vtable (ref $kotlin.Int.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $value (mut i32)))))
    (type $kotlin.wasm.internal.CharCodes (;125;) (sub final $kotlin.Enum (struct (field $vtable (ref $kotlin.wasm.internal.CharCodes.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $name (mut (ref null $kotlin.String))) (field $ordinal (mut i32)) (field $code (mut i32)))))
    (type $kotlin.Error (;126;) (sub $kotlin.Throwable (struct (field $vtable (ref $kotlin.Error.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.Exception (;127;) (sub $kotlin.Throwable (struct (field $vtable (ref $kotlin.Exception.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.wasm.unsafe.ScopedMemoryAllocator (;128;) (sub final $kotlin.wasm.unsafe.MemoryAllocator (struct (field $vtable (ref $kotlin.wasm.unsafe.ScopedMemoryAllocator.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $parent (mut (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))) (field $destroyed (mut i8)) (field $suspended (mut i8)) (field $availableAddress (mut i32)))))
    (type $kotlin.wasm.WasiError (;129;) (sub final $kotlin.Throwable (struct (field $vtable (ref $kotlin.wasm.WasiError.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))) (field $error (mut (ref null $kotlin.wasm.WasiErrorCode))))))
    (type $kotlin.wasm.WasiErrorCode (;130;) (sub final $kotlin.Enum (struct (field $vtable (ref $kotlin.wasm.WasiErrorCode.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $name (mut (ref null $kotlin.String))) (field $ordinal (mut i32)))))
    (type $kotlin.collections.AbstractMutableList.vtable (;131;) (sub $kotlin.collections.AbstractMutableCollection.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-size> (ref null $"#type153 ")) (field $iterator (ref null $"#type161 ")) (field $isEmpty (ref null $"#type153 ")) (field $"#field4 iterator" (ref null $"#type161 ")) (field $get (ref null $"#type169 ")))))
    (type $kotlin.enums.EnumEntriesList.vtable (;132;) (sub final $kotlin.collections.AbstractList.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-size> (ref null $"#type153 ")) (field $iterator (ref null $"#type161 ")) (field $isEmpty (ref null $"#type153 ")) (field $get (ref null $"#type169 ")) (field $"#field5 get" (ref null $"#type193 ")))))
    (type $kotlin.AssertionError.vtable (;133;) (sub $kotlin.Error.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.RuntimeException.vtable (;134;) (sub $kotlin.Exception.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.OutOfMemoryError.vtable (;135;) (sub $kotlin.Error.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.collections.AbstractMutableList (;136;) (sub $kotlin.collections.AbstractMutableCollection (struct (field $vtable (ref $kotlin.collections.AbstractMutableList.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $modCount (mut i32)))))
    (type $kotlin.enums.EnumEntriesList (;137;) (sub final $kotlin.collections.AbstractList (struct (field $vtable (ref $kotlin.enums.EnumEntriesList.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $entries (mut (ref null $kotlin.Array))))))
    (type $kotlin.AssertionError (;138;) (sub $kotlin.Error (struct (field $vtable (ref $kotlin.AssertionError.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.RuntimeException (;139;) (sub $kotlin.Exception (struct (field $vtable (ref $kotlin.RuntimeException.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.OutOfMemoryError (;140;) (sub $kotlin.Error (struct (field $vtable (ref $kotlin.OutOfMemoryError.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.collections.ArrayList.vtable (;141;) (sub final $kotlin.collections.AbstractMutableList.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-size> (ref null $"#type153 ")) (field $iterator (ref null $"#type161 ")) (field $isEmpty (ref null $"#type153 ")) (field $"#field4 iterator" (ref null $"#type161 ")) (field $get (ref null $"#type169 ")) (field $listIterator (ref null $"#type169 ")))))
    (type $kotlin.IllegalArgumentException.vtable (;142;) (sub $kotlin.RuntimeException.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.NoSuchElementException.vtable (;143;) (sub $kotlin.RuntimeException.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.IndexOutOfBoundsException.vtable (;144;) (sub $kotlin.RuntimeException.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.IllegalStateException.vtable (;145;) (sub $kotlin.RuntimeException.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.ConcurrentModificationException.vtable (;146;) (sub $kotlin.RuntimeException.vtable (struct (field $toString (ref null $"#type170 ")) (field $<get-message> (ref null $"#type170 ")))))
    (type $kotlin.collections.ArrayList (;147;) (sub final $kotlin.collections.AbstractMutableList (struct (field $vtable (ref $kotlin.collections.ArrayList.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $modCount (mut i32)) (field $backing (mut (ref null $kotlin.Array))) (field $length (mut i32)) (field $isReadOnly (mut i8)))))
    (type $kotlin.IllegalArgumentException (;148;) (sub $kotlin.RuntimeException (struct (field $vtable (ref $kotlin.IllegalArgumentException.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.NoSuchElementException (;149;) (sub $kotlin.RuntimeException (struct (field $vtable (ref $kotlin.NoSuchElementException.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.IndexOutOfBoundsException (;150;) (sub $kotlin.RuntimeException (struct (field $vtable (ref $kotlin.IndexOutOfBoundsException.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.IllegalStateException (;151;) (sub $kotlin.RuntimeException (struct (field $vtable (ref $kotlin.IllegalStateException.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $kotlin.ConcurrentModificationException (;152;) (sub $kotlin.RuntimeException (struct (field $vtable (ref $kotlin.ConcurrentModificationException.vtable)) (field $itable structref) (field $typeInfo (mut i32)) (field $_hashCode (mut i32)) (field $message (mut (ref null $kotlin.String))) (field $cause (mut (ref null $kotlin.Throwable))) (field $suppressedExceptionsList (mut (ref null $kotlin.Any))))))
    (type $"#type153 " (@name "") (;153;) (func (param (ref null $kotlin.Any)) (result i32)))
    (type $"#type154 " (@name "") (;154;) (func (param (ref null $kotlin.Any) i32) (result i32)))
    (type $"#type155 " (@name "") (;155;) (func (param (ref null $kotlin.Number)) (result (ref null $kotlin.Number))))
    (type $"#type156 " (@name "") (;156;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any) i32 (ref null $kotlin.Any) (ref null $kotlin.Any)) (result (ref null $kotlin.String))))
    (type $"#type157 " (@name "") (;157;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Int) (ref null $kotlin.Any) (ref null $kotlin.Any) i32 (ref null $kotlin.Any)) (result (ref null $kotlin.String))))
    (type $"#type158 " (@name "") (;158;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any) i32 (ref null $kotlin.Any) (ref null $kotlin.Any)) (result (ref null $kotlin.Any))))
    (type $"#type159 " (@name "") (;159;) (func (param (ref null $kotlin.collections.AbstractMutableCollection)) (result (ref null $kotlin.collections.AbstractMutableCollection))))
    (type $"#type160 " (@name "") (;160;) (func (param (ref null $kotlin.collections.IteratorImpl) (ref null $kotlin.collections.AbstractMutableList)) (result (ref null $kotlin.collections.IteratorImpl))))
    (type $"#type161 " (@name "") (;161;) (func (param (ref null $kotlin.Any)) (result (ref null $kotlin.Any))))
    (type $"#type162 " (@name "") (;162;) (func (param (ref null $kotlin.collections.IteratorImpl))))
    (type $"#type163 " (@name "") (;163;) (func (param (ref null $kotlin.collections.AbstractMutableList)) (result (ref null $kotlin.collections.AbstractMutableList))))
    (type $"#type164 " (@name "") (;164;) (func (param (ref null $kotlin.collections.Companion)) (result (ref null $kotlin.collections.Companion))))
    (type $"#type165 " (@name "") (;165;) (func (result (ref null $kotlin.collections.Companion))))
    (type $"#type166 " (@name "") (;166;) (func (param (ref null $kotlin.collections.Itr) (ref null $kotlin.collections.ArrayList) i32) (result (ref null $kotlin.collections.Itr))))
    (type $"#type167 " (@name "") (;167;) (func (param (ref null $kotlin.collections.ArrayList) i32) (result (ref null $kotlin.collections.ArrayList))))
    (type $"#type168 " (@name "") (;168;) (func (param (ref null $kotlin.collections.ArrayList)) (result (ref null $kotlin.collections.ArrayList))))
    (type $"#type169 " (@name "") (;169;) (func (param (ref null $kotlin.Any) i32) (result (ref null $kotlin.Any))))
    (type $"#type170 " (@name "") (;170;) (func (param (ref null $kotlin.Any)) (result (ref null $kotlin.String))))
    (type $"#type171 " (@name "") (;171;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any)) (result (ref null $kotlin.Any))))
    (type $"#type172 " (@name "") (;172;) (func (param (ref null $kotlin.text.StringBuilder) (ref null $kotlin.CharArray)) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type173 " (@name "") (;173;) (func (param (ref null $kotlin.text.StringBuilder)) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type174 " (@name "") (;174;) (func (param (ref null $kotlin.text.StringBuilder) i32) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type175 " (@name "") (;175;) (func (param (ref null $kotlin.Any) i32) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type176 " (@name "") (;176;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any)) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type177 " (@name "") (;177;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any) i32 i32) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type178 " (@name "") (;178;) (func (param (ref null $kotlin.text.StringBuilder) (ref null $kotlin.Any)) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type179 " (@name "") (;179;) (func (param (ref null $kotlin.text.StringBuilder) (ref null $kotlin.String)) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type180 " (@name "") (;180;) (func (param (ref null $kotlin.text.StringBuilder) (ref null $kotlin.Any) i32 i32) (result (ref null $kotlin.text.StringBuilder))))
    (type $"#type181 " (@name "") (;181;) (func (param (ref null $kotlin.text.StringBuilder) i32)))
    (type $"#type182 " (@name "") (;182;) (func (param (ref null $kotlin.CharArray) i32 (ref null $kotlin.String)) (result i32)))
    (type $"#type183 " (@name "") (;183;) (func (param (ref null $kotlin.collections.AbstractCollection$toString$lambda) (ref null $kotlin.collections.AbstractCollection)) (result (ref null $kotlin.collections.AbstractCollection$toString$lambda))))
    (type $"#type184 " (@name "") (;184;) (func (param (ref null $kotlin.collections.AbstractCollection)) (result (ref null $kotlin.collections.AbstractCollection))))
    (type $"#type185 " (@name "") (;185;) (func (param (ref null $"#type81 kotlin.collections.IteratorImpl") (ref null $kotlin.collections.AbstractList)) (result (ref null $"#type81 kotlin.collections.IteratorImpl"))))
    (type $"#type186 " (@name "") (;186;) (func (param (ref null $"#type82 kotlin.collections.Companion")) (result (ref null $"#type82 kotlin.collections.Companion"))))
    (type $"#type187 " (@name "") (;187;) (func (param (ref null $"#type82 kotlin.collections.Companion") i32 i32)))
    (type $"#type188 " (@name "") (;188;) (func (param (ref null $"#type82 kotlin.collections.Companion") i32 i32 i32)))
    (type $"#type189 " (@name "") (;189;) (func (param (ref null $"#type82 kotlin.collections.Companion") i32 i32) (result i32)))
    (type $"#type190 " (@name "") (;190;) (func (param (ref null $kotlin.collections.AbstractList)) (result (ref null $kotlin.collections.AbstractList))))
    (type $"#type191 " (@name "") (;191;) (func (param (ref null $kotlin.Array)) (result (ref null $kotlin.Any))))
    (type $"#type192 " (@name "") (;192;) (func (param (ref null $kotlin.enums.EnumEntriesList) (ref null $kotlin.Array)) (result (ref null $kotlin.enums.EnumEntriesList))))
    (type $"#type193 " (@name "") (;193;) (func (param (ref null $kotlin.Any) i32) (result (ref null $kotlin.Enum))))
    (type $"#type194 " (@name "") (;194;) (func (param (ref null $kotlin.random.Default)) (result (ref null $kotlin.random.Default))))
    (type $"#type195 " (@name "") (;195;) (func (param (ref null $kotlin.Any) i32 i32) (result i32)))
    (type $"#type196 " (@name "") (;196;) (func (result (ref null $kotlin.random.Default))))
    (type $"#type197 " (@name "") (;197;) (func (param (ref null $kotlin.random.Random)) (result (ref null $kotlin.random.Random))))
    (type $"#type198 " (@name "") (;198;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any)) (result (ref null $kotlin.String))))
    (type $"#type199 " (@name "") (;199;) (func (param i64) (result (ref null $kotlin.random.Random))))
    (type $"#type200 " (@name "") (;200;) (func (param (ref null $kotlin.random.Companion)) (result (ref null $kotlin.random.Companion))))
    (type $"#type201 " (@name "") (;201;) (func (param (ref null $kotlin.random.XorWowRandom) i32 i32 i32 i32 i32 i32) (result (ref null $kotlin.random.XorWowRandom))))
    (type $"#type202 " (@name "") (;202;) (func (param (ref null $kotlin.random.XorWowRandom) i32 i32) (result (ref null $kotlin.random.XorWowRandom))))
    (type $"#type203 " (@name "") (;203;) (func (param (ref null $kotlin.Any) (ref null $kotlin.Any) (ref null $kotlin.Any))))
    (type $"#type204 " (@name "") (;204;) (func (param (ref null $kotlin.Companion)) (result (ref null $kotlin.Companion))))
    (type $"#type205 " (@name "") (;205;) (func (param i32) (result (ref null $kotlin.String))))
    (type $"#type206 " (@name "") (;206;) (func (param (ref null $"#type87 kotlin.Companion")) (result (ref null $"#type87 kotlin.Companion"))))
    (type $"#type207 " (@name "") (;207;) (func (param i64) (result (ref null $kotlin.String))))
    (type $"#type208 " (@name "") (;208;) (func (param (ref null $kotlin.Array) i32) (result (ref null $kotlin.Array))))
    (type $"#type209 " (@name "") (;209;) (func (param (ref null $kotlin.Array) i32) (result (ref null $kotlin.Any))))
    (type $"#type210 " (@name "") (;210;) (func (param (ref null $kotlin.Array) i32 (ref null $kotlin.Any))))
    (type $"#type211 " (@name "") (;211;) (func (param (ref null $kotlin.Array)) (result i32)))
    (type $"#type212 " (@name "") (;212;) (func (param (ref null $kotlin.CharArray) i32) (result (ref null $kotlin.CharArray))))
    (type $"#type213 " (@name "") (;213;) (func (param (ref null $kotlin.CharArray) i32) (result i32)))
    (type $"#type214 " (@name "") (;214;) (func (param (ref null $kotlin.CharArray) i32 i32)))
    (type $"#type215 " (@name "") (;215;) (func (param (ref null $kotlin.CharArray)) (result i32)))
    (type $"#type216 " (@name "") (;216;) (func (param (ref null $"#type93 kotlin.Companion")) (result (ref null $"#type93 kotlin.Companion"))))
    (type $"#type217 " (@name "") (;217;) (func (param (ref null $"#type95 kotlin.Companion")) (result (ref null $"#type95 kotlin.Companion"))))
    (type $"#type218 " (@name "") (;218;) (func (param (ref null $kotlin.Enum) (ref null $kotlin.String) i32) (result (ref null $kotlin.Enum))))
    (type $"#type219 " (@name "") (;219;) (func (param (ref null $"#type97 kotlin.Companion")) (result (ref null $"#type97 kotlin.Companion"))))
    (type $"#type220 " (@name "") (;220;) (func (param (ref null $"#type98 kotlin.Companion")) (result (ref null $"#type98 kotlin.Companion"))))
    (type $"#type221 " (@name "") (;221;) (func (param (ref null $kotlin.String) (ref null $kotlin.Any)) (result (ref null $kotlin.String))))
    (type $"#type222 " (@name "") (;222;) (func (param (ref null $kotlin.String))))
    (type $"#type223 " (@name "") (;223;) (func (param i32 i32 i32) (result (ref null $kotlin.String))))
    (type $"#type224 " (@name "") (;224;) (func (param (ref null $kotlin.wasm.internal.WasmCharArray) i32 i32)))
    (type $"#type225 " (@name "") (;225;) (func (param (ref null $kotlin.wasm.internal.WasmCharArray) i64 i32)))
    (type $"#type226 " (@name "") (;226;) (func (param (ref null $kotlin.wasm.internal.CharCodes) (ref null $kotlin.String) i32 i32) (result (ref null $kotlin.wasm.internal.CharCodes))))
    (type $"#type227 " (@name "") (;227;) (func (result (ref null $kotlin.wasm.internal.CharCodes))))
    (type $"#type228 " (@name "") (;228;) (func (param i32 i32 i32 i32) (result (ref null $kotlin.String))))
    (type $"#type229 " (@name "") (;229;) (func (param (ref null $kotlin.CharArray) (ref null $kotlin.CharArray) i32 i32 i32) (result (ref null $kotlin.CharArray))))
    (type $"#type230 " (@name "") (;230;) (func (param (ref null $kotlin.CharArray) i32 i32) (result (ref null $kotlin.CharArray))))
    (type $"#type231 " (@name "") (;231;) (func (param i32 (ref null $kotlin.Any))))
    (type $"#type232 " (@name "") (;232;) (func (param (ref null $kotlin.assert$lambda)) (result (ref null $kotlin.assert$lambda))))
    (type $"#type233 " (@name "") (;233;) (func (param (ref null $kotlin.IllegalArgumentException) (ref null $kotlin.String)) (result (ref null $kotlin.IllegalArgumentException))))
    (type $"#type234 " (@name "") (;234;) (func (param (ref null $kotlin.IllegalArgumentException)) (result (ref null $kotlin.IllegalArgumentException))))
    (type $"#type235 " (@name "") (;235;) (func (param (ref null $kotlin.NoSuchElementException)) (result (ref null $kotlin.NoSuchElementException))))
    (type $"#type236 " (@name "") (;236;) (func (param (ref null $kotlin.IndexOutOfBoundsException)) (result (ref null $kotlin.IndexOutOfBoundsException))))
    (type $"#type237 " (@name "") (;237;) (func (param (ref null $kotlin.IndexOutOfBoundsException) (ref null $kotlin.String)) (result (ref null $kotlin.IndexOutOfBoundsException))))
    (type $"#type238 " (@name "") (;238;) (func (param (ref null $kotlin.AssertionError) (ref null $kotlin.Any)) (result (ref null $kotlin.AssertionError))))
    (type $"#type239 " (@name "") (;239;) (func (param (ref null $kotlin.AssertionError)) (result (ref null $kotlin.AssertionError))))
    (type $"#type240 " (@name "") (;240;) (func (param (ref null $kotlin.RuntimeException)) (result (ref null $kotlin.RuntimeException))))
    (type $"#type241 " (@name "") (;241;) (func (param (ref null $kotlin.RuntimeException) (ref null $kotlin.String)) (result (ref null $kotlin.RuntimeException))))
    (type $"#type242 " (@name "") (;242;) (func (param (ref null $kotlin.Error)) (result (ref null $kotlin.Error))))
    (type $"#type243 " (@name "") (;243;) (func (param (ref null $kotlin.Error) (ref null $kotlin.String) (ref null $kotlin.Throwable)) (result (ref null $kotlin.Error))))
    (type $"#type244 " (@name "") (;244;) (func (param (ref null $kotlin.Exception)) (result (ref null $kotlin.Exception))))
    (type $"#type245 " (@name "") (;245;) (func (param (ref null $kotlin.Exception) (ref null $kotlin.String)) (result (ref null $kotlin.Exception))))
    (type $"#type246 " (@name "") (;246;) (func (param (ref null $kotlin.IllegalStateException) (ref null $kotlin.String)) (result (ref null $kotlin.IllegalStateException))))
    (type $"#type247 " (@name "") (;247;) (func (param (ref null $kotlin.IllegalStateException)) (result (ref null $kotlin.IllegalStateException))))
    (type $"#type248 " (@name "") (;248;) (func (param (ref null $kotlin.OutOfMemoryError)) (result (ref null $kotlin.OutOfMemoryError))))
    (type $"#type249 " (@name "") (;249;) (func (param (ref null $kotlin.ConcurrentModificationException)) (result (ref null $kotlin.ConcurrentModificationException))))
    (type $"#type250 " (@name "") (;250;) (func (param (ref null $kotlin.Unit)) (result (ref null $kotlin.Unit))))
    (type $"#type251 " (@name "") (;251;) (func (result (ref null $kotlin.Unit))))
    (type $"#type252 " (@name "") (;252;) (func (param (ref null $kotlin.CharArray) i32 i32) (result i32)))
    (type $"#type253 " (@name "") (;253;) (func (param (ref null $kotlin.CharArray) i32 i32) (result (ref null $kotlin.String))))
    (type $"#type254 " (@name "") (;254;) (func (param (ref null $kotlin.CharArray) i32 (ref null $kotlin.String) i32 i32) (result i32)))
    (type $"#type255 " (@name "") (;255;) (func (param (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator) i32 (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))))
    (type $"#type256 " (@name "") (;256;) (func (param (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))))
    (type $"#type257 " (@name "") (;257;) (func (param (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))))
    (type $"#type258 " (@name "") (;258;) (func (param (ref null $kotlin.wasm.unsafe.MemoryAllocator)) (result (ref null $kotlin.wasm.unsafe.MemoryAllocator))))
    (type $"#type259 " (@name "") (;259;) (func (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))))
    (type $"#type260 " (@name "") (;260;) (func (param (ref null $kotlin.Throwable) (ref null $kotlin.String) (ref null $kotlin.Throwable)) (result (ref null $kotlin.Throwable))))
    (type $"#type261 " (@name "") (;261;) (func (param (ref null $kotlin.Throwable) (ref null $kotlin.String)) (result (ref null $kotlin.Throwable))))
    (type $"#type262 " (@name "") (;262;) (func (param (ref null $kotlin.Throwable)) (result (ref null $kotlin.Throwable))))
    (type $"#type263 " (@name "") (;263;) (func (param (ref null $kotlin.wasm.WasiError) (ref null $kotlin.wasm.WasiErrorCode)) (result (ref null $kotlin.wasm.WasiError))))
    (type $"#type264 " (@name "") (;264;) (func (result (ref null $kotlin.Array))))
    (type $"#type265 " (@name "") (;265;) (func (result (ref null $kotlin.Any))))
    (type $"#type266 " (@name "") (;266;) (func (param (ref null $kotlin.wasm.WasiErrorCode) (ref null $kotlin.String) i32) (result (ref null $kotlin.wasm.WasiErrorCode))))
    (type $"#type267 " (@name "") (;267;) (func (result (ref null $kotlin.wasm.WasiErrorCode))))
    (type $"#type268 " (@name "") (;268;) (func (result (ref null $kotlin.random.Random))))
    (type $"#type269 " (@name "") (;269;) (func (param (ref null $kotlin.Any))))
    (type $"#type270 " (@name "") (;270;) (func (param (ref null $SoftFailure)) (result (ref null $SoftFailure))))
    (type $"#type271 " (@name "") (;271;) (func (param (ref null $main$lambda)) (result (ref null $main$lambda))))
    (type $"#type272 " (@name "") (;272;) (func (param (ref null $kotlin.Throwable))))
  )
  (import "wasi_snapshot_preview1" "random_get" (func (;0;) (type 0)))
  (func $kotlin.Number.<init> (;1;) (type $"#type155 ") (param $<this> (ref null $kotlin.Number)) (result (ref null $kotlin.Number))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.joinToString (;2;) (type $"#type156 ") (param $<this> (ref null $kotlin.Any)) (param $separator (ref null $kotlin.Any)) (param $prefix (ref null $kotlin.Any)) (param $postfix (ref null $kotlin.Any)) (param $limit i32) (param $truncated (ref null $kotlin.Any)) (param $transform (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    local.get $<this>
    ref.null none
    call $"#func29 kotlin.text.StringBuilder.<init>"
    local.get $separator
    local.get $prefix
    local.get $postfix
    local.get $limit
    local.get $truncated
    local.get $transform
    call $kotlin.collections.joinTo
    ref.cast (ref null $kotlin.text.StringBuilder)
    call $kotlin.text.StringBuilder.toString
    return
  )
  (func $kotlin.collections.joinToString$default (;3;) (type $"#type157 ") (param $<this> (ref null $kotlin.Any)) (param $separator (ref null $kotlin.Any)) (param $prefix (ref null $kotlin.Any)) (param $postfix (ref null $kotlin.Any)) (param $limit (ref null $kotlin.Int)) (param $truncated (ref null $kotlin.Any)) (param $transform (ref null $kotlin.Any)) (param $$mask0 i32) (param $$handler (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    local.get $$mask0
    i32.const 1
    i32.and
    i32.const 0
    i32.eq
    i32.eqz
    if ;; label = @1
      i32.const 2
      i32.const 12
      i32.const 2
      call $kotlin.stringLiteral
      local.set $separator
    else
    end
    local.get $$mask0
    i32.const 2
    i32.and
    i32.const 0
    i32.eq
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 0
      i32.const 0
      call $kotlin.stringLiteral
      local.set $prefix
    else
    end
    local.get $$mask0
    i32.const 4
    i32.and
    i32.const 0
    i32.eq
    i32.eqz
    if ;; label = @1
      i32.const 0
      i32.const 0
      i32.const 0
      call $kotlin.stringLiteral
      local.set $postfix
    else
    end
    local.get $$mask0
    i32.const 8
    i32.and
    i32.const 0
    i32.eq
    i32.eqz
    if ;; label = @1
      global.get $kotlin.Int.vtable
      global.get $kotlin.Int.classITable
      i32.const 1208
      i32.const 0
      i32.const -1
      struct.new $kotlin.Int
      local.set $limit
    else
    end
    local.get $$mask0
    i32.const 16
    i32.and
    i32.const 0
    i32.eq
    i32.eqz
    if ;; label = @1
      i32.const 3
      i32.const 16
      i32.const 3
      call $kotlin.stringLiteral
      local.set $truncated
    else
    end
    local.get $$mask0
    i32.const 32
    i32.and
    i32.const 0
    i32.eq
    i32.eqz
    if ;; label = @1
      ref.null none
      local.set $transform
    else
    end
    local.get $<this>
    local.get $separator
    local.get $prefix
    local.get $postfix
    local.get $limit
    struct.get $kotlin.Int $value
    local.get $truncated
    local.get $transform
    call $kotlin.collections.joinToString
    return
  )
  (func $kotlin.collections.joinTo (;4;) (type $"#type158 ") (param $<this> (ref null $kotlin.Any)) (param $buffer (ref null $kotlin.Any)) (param $separator (ref null $kotlin.Any)) (param $prefix (ref null $kotlin.Any)) (param $postfix (ref null $kotlin.Any)) (param $limit i32) (param $truncated (ref null $kotlin.Any)) (param $transform (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $count i32) (local $<iterator> (ref null $kotlin.Any)) (local $element (ref null $kotlin.Any)) (local $tmp0 i32) (local $this i32)
    local.get $buffer
    local.get $prefix
    local.get $buffer
    struct.get $kotlin.Any $itable
    ref.cast (ref $classITable)
    struct.get $classITable $kotlin.text.itable
    struct.get $kotlin.text.Appendable.itable $"#field1 append"
    call_ref $"#type171 "
    drop
    i32.const 0
    local.set $count
    local.get $<this>
    local.get $<this>
    struct.get $kotlin.Any $itable
    ref.cast (ref $"#type38 classITable")
    struct.get $"#type38 classITable" $kotlin.collections.itable
    struct.get $kotlin.collections.Iterable.itable $iterator
    call_ref $"#type161 "
    local.set $<iterator>
    loop ;; label = @1
      block ;; label = @2
        local.get $<iterator>
        local.get $<iterator>
        struct.get $kotlin.Any $itable
        ref.cast (ref $"#type39 classITable")
        struct.get $"#type39 classITable" $kotlin.collections.itable
        struct.get $kotlin.collections.Iterator.itable $hasNext
        call_ref $"#type153 "
        i32.eqz
        br_if 0 (;@2;)
        local.get $<iterator>
        local.get $<iterator>
        struct.get $kotlin.Any $itable
        ref.cast (ref $"#type39 classITable")
        struct.get $"#type39 classITable" $kotlin.collections.itable
        struct.get $kotlin.collections.Iterator.itable $next
        call_ref $"#type161 "
        local.set $element
        local.get $count
        local.set $tmp0
        block (result i32) ;; label = @3
          nop
          local.get $tmp0
          local.tee $this
          i32.const 1
          i32.add
          br 0 (;@3;)
        end
        local.tee $count
        i32.const 1
        i32.gt_s
        if ;; label = @3
          local.get $buffer
          local.get $separator
          local.get $buffer
          struct.get $kotlin.Any $itable
          ref.cast (ref $classITable)
          struct.get $classITable $kotlin.text.itable
          struct.get $kotlin.text.Appendable.itable $"#field1 append"
          call_ref $"#type171 "
          drop
        else
        end
        local.get $limit
        i32.const 0
        i32.lt_s
        if (result i32) ;; label = @3
          i32.const 1
        else
          local.get $count
          local.get $limit
          i32.le_s
        end
        if ;; label = @3
          local.get $buffer
          local.get $element
          local.get $transform
          call $kotlin.text.appendElement
        else
          br 1 (;@2;)
        end
        br 1 (;@1;)
      end
    end
    local.get $limit
    i32.const 0
    i32.ge_s
    if (result i32) ;; label = @1
      local.get $count
      local.get $limit
      i32.gt_s
    else
      i32.const 0
    end
    if ;; label = @1
      local.get $buffer
      local.get $truncated
      local.get $buffer
      struct.get $kotlin.Any $itable
      ref.cast (ref $classITable)
      struct.get $classITable $kotlin.text.itable
      struct.get $kotlin.text.Appendable.itable $"#field1 append"
      call_ref $"#type171 "
      drop
    else
    end
    local.get $buffer
    local.get $postfix
    local.get $buffer
    struct.get $kotlin.Any $itable
    ref.cast (ref $classITable)
    struct.get $classITable $kotlin.text.itable
    struct.get $kotlin.text.Appendable.itable $"#field1 append"
    call_ref $"#type171 "
    drop
    local.get $buffer
    return
  )
  (func $kotlin.ranges.coerceAtMost (;5;) (type 0) (param $<this> i32) (param $maximumValue i32) (result i32)
    local.get $<this>
    local.get $maximumValue
    i32.gt_s
    if (result i32) ;; label = @1
      local.get $maximumValue
    else
      local.get $<this>
    end
    return
  )
  (func $kotlin.collections.AbstractMutableCollection.<init> (;6;) (type $"#type159 ") (param $<this> (ref null $kotlin.collections.AbstractMutableCollection)) (result (ref null $kotlin.collections.AbstractMutableCollection))
    local.get $<this>
    call $kotlin.collections.AbstractCollection.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.IteratorImpl.<init> (;7;) (type $"#type160 ") (param $<this> (ref null $kotlin.collections.IteratorImpl)) (param $$outer (ref null $kotlin.collections.AbstractMutableList)) (result (ref null $kotlin.collections.IteratorImpl))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.collections.IteratorImpl.vtable
      global.get $kotlin.collections.IteratorImpl.classITable
      i32.const 56
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      ref.null $kotlin.collections.AbstractMutableList
      struct.new $kotlin.collections.IteratorImpl
      local.set $<this>
    end
    local.get $<this>
    local.get $$outer
    struct.set $kotlin.collections.IteratorImpl $$this
    local.get $<this>
    i32.const 0
    struct.set $kotlin.collections.IteratorImpl $index
    local.get $<this>
    i32.const -1
    struct.set $kotlin.collections.IteratorImpl $last
    local.get $<this>
    local.get $<this>
    struct.get $kotlin.collections.IteratorImpl $$this
    struct.get $kotlin.collections.AbstractMutableList $modCount
    struct.set $kotlin.collections.IteratorImpl $expectedModCount
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.IteratorImpl.hasNext (;8;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.collections.IteratorImpl)) (local $tmp (ref null $kotlin.collections.AbstractMutableList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.IteratorImpl)
    local.tee $tmp0_<this>
    struct.get $kotlin.collections.IteratorImpl $index
    local.get $tmp0_<this>
    struct.get $kotlin.collections.IteratorImpl $$this
    local.tee $tmp
    local.get $tmp
    struct.get $kotlin.collections.AbstractCollection $vtable
    struct.get $kotlin.collections.AbstractCollection.vtable $<get-size>
    call_ref $"#type153 "
    i32.lt_s
    return
  )
  (func $kotlin.collections.IteratorImpl.next (;9;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.IteratorImpl)) (local $<unary> i32) (local $tmp0 i32) (local $this i32) (local $tmp (ref null $kotlin.collections.AbstractMutableList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.IteratorImpl)
    local.tee $tmp0_<this>
    call $kotlin.collections.IteratorImpl.checkForComodification
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.collections.IteratorImpl $vtable
    struct.get $kotlin.collections.IteratorImpl.vtable $hasNext
    call_ref $"#type153 "
    i32.eqz
    if ;; label = @1
      ref.null none
      call $kotlin.NoSuchElementException.<init>
      throw 0
    else
    end
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.collections.IteratorImpl $index
    local.set $<unary>
    local.get $tmp0_<this>
    local.get $<unary>
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      i32.const 1
      i32.add
      br 0 (;@1;)
    end
    struct.set $kotlin.collections.IteratorImpl $index
    local.get $<unary>
    struct.set $kotlin.collections.IteratorImpl $last
    local.get $tmp0_<this>
    struct.get $kotlin.collections.IteratorImpl $$this
    local.tee $tmp
    local.get $tmp0_<this>
    struct.get $kotlin.collections.IteratorImpl $last
    local.get $tmp
    struct.get $kotlin.Any $itable
    ref.cast (ref $"#type38 classITable")
    struct.get $"#type38 classITable" $"#field2 kotlin.collections.itable"
    struct.get $kotlin.collections.List.itable $get
    call_ref $"#type169 "
    return
  )
  (func $kotlin.collections.IteratorImpl.checkForComodification (;10;) (type $"#type162 ") (param $<this> (ref null $kotlin.collections.IteratorImpl))
    local.get $<this>
    struct.get $kotlin.collections.IteratorImpl $$this
    struct.get $kotlin.collections.AbstractMutableList $modCount
    local.get $<this>
    struct.get $kotlin.collections.IteratorImpl $expectedModCount
    i32.eq
    i32.eqz
    if ;; label = @1
      ref.null none
      call $kotlin.ConcurrentModificationException.<init>
      throw 0
    else
    end
    nop
  )
  (func $kotlin.collections.AbstractMutableList.<init> (;11;) (type $"#type163 ") (param $<this> (ref null $kotlin.collections.AbstractMutableList)) (result (ref null $kotlin.collections.AbstractMutableList))
    local.get $<this>
    call $kotlin.collections.AbstractMutableCollection.<init>
    drop
    local.get $<this>
    i32.const 0
    struct.set $kotlin.collections.AbstractMutableList $modCount
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.AbstractMutableList.iterator (;12;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.AbstractMutableList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.AbstractMutableList)
    local.set $tmp0_<this>
    ref.null none
    local.get $tmp0_<this>
    call $kotlin.collections.IteratorImpl.<init>
    return
  )
  (func $"#func13 kotlin.collections.AbstractMutableList.iterator" (@name "kotlin.collections.AbstractMutableList.iterator") (;13;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    local.get $<this>
    local.get $<this>
    ref.cast (ref $kotlin.collections.AbstractMutableList)
    struct.get $kotlin.collections.AbstractMutableList $vtable
    struct.get $kotlin.collections.AbstractMutableList.vtable $"#field4 iterator"
    call_ref $"#type161 "
    return
  )
  (func $kotlin.collections.Companion.<init> (;14;) (type $"#type164 ") (param $<this> (ref null $kotlin.collections.Companion)) (result (ref null $kotlin.collections.Companion))
    (local $tmp0 (ref null $kotlin.Any)) (local $this (ref null $kotlin.Any)) (local $tmp1 (ref null $kotlin.collections.ArrayList)) (local $it (ref null $kotlin.collections.ArrayList))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.collections.Companion.vtable
      ref.null struct
      i32.const 124
      i32.const 0
      ref.null $kotlin.collections.ArrayList
      struct.new $kotlin.collections.Companion
      local.set $<this>
    end
    local.get $<this>
    global.set $kotlin.collections.Companion_instance
    local.get $<this>
    ref.null none
    i32.const 0
    call $kotlin.collections.ArrayList.<init>
    local.set $tmp0
    block (result (ref null $kotlin.collections.ArrayList)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      ref.cast (ref null $kotlin.collections.ArrayList)
      local.set $tmp1
      block (result (ref null $kotlin.Unit)) ;; label = @2
        nop
        local.get $tmp1
        local.tee $it
        i32.const 1
        struct.set $kotlin.collections.ArrayList $isReadOnly
        global.get $kotlin.Unit_instance
      end
      drop
      local.get $this
      ref.cast (ref null $kotlin.collections.ArrayList)
      br 0 (;@1;)
    end
    struct.set $kotlin.collections.Companion $Empty
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.Companion_getInstance (;15;) (type $"#type165 ") (result (ref null $kotlin.collections.Companion))
    global.get $kotlin.collections.Companion_instance
    ref.is_null
    if ;; label = @1
      ref.null none
      call $kotlin.collections.Companion.<init>
      drop
    else
    end
    global.get $kotlin.collections.Companion_instance
    return
  )
  (func $kotlin.collections.Itr.<init> (;16;) (type $"#type166 ") (param $<this> (ref null $kotlin.collections.Itr)) (param $list (ref null $kotlin.collections.ArrayList)) (param $index i32) (result (ref null $kotlin.collections.Itr))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.collections.Itr.vtable
      global.get $kotlin.collections.Itr.classITable
      i32.const 156
      i32.const 0
      ref.null $kotlin.collections.ArrayList
      i32.const 0
      i32.const 0
      i32.const 0
      struct.new $kotlin.collections.Itr
      local.set $<this>
    end
    local.get $<this>
    local.get $list
    struct.set $kotlin.collections.Itr $list
    local.get $<this>
    local.get $index
    struct.set $kotlin.collections.Itr $index
    local.get $<this>
    i32.const -1
    struct.set $kotlin.collections.Itr $lastIndex
    local.get $<this>
    local.get $<this>
    struct.get $kotlin.collections.Itr $list
    struct.get $kotlin.collections.AbstractMutableList $modCount
    struct.set $kotlin.collections.Itr $expectedModCount
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.Itr.hasNext (;17;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.collections.Itr))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.Itr)
    local.tee $tmp0_<this>
    struct.get $kotlin.collections.Itr $index
    local.get $tmp0_<this>
    struct.get $kotlin.collections.Itr $list
    struct.get $kotlin.collections.ArrayList $length
    i32.lt_s
    return
  )
  (func $kotlin.collections.Itr.next (;18;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.Itr)) (local $tmp0 (ref null $kotlin.collections.Itr)) (local $this (ref null $kotlin.collections.Itr)) (local $<unary> i32) (local $tmp1 i32) (local $"#local6 this" (@name "this") i32)
    local.get $<this>
    ref.cast (ref null $kotlin.collections.Itr)
    local.tee $tmp0_<this>
    local.set $tmp0
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      struct.get $kotlin.collections.Itr $list
      struct.get $kotlin.collections.AbstractMutableList $modCount
      local.get $this
      struct.get $kotlin.collections.Itr $expectedModCount
      i32.eq
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        ref.null none
        call $kotlin.ConcurrentModificationException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    local.get $tmp0_<this>
    struct.get $kotlin.collections.Itr $index
    local.get $tmp0_<this>
    struct.get $kotlin.collections.Itr $list
    struct.get $kotlin.collections.ArrayList $length
    i32.ge_s
    if ;; label = @1
      ref.null none
      call $kotlin.NoSuchElementException.<init>
      throw 0
    else
    end
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.collections.Itr $index
    local.set $<unary>
    local.get $tmp0_<this>
    local.get $<unary>
    local.set $tmp1
    block (result i32) ;; label = @1
      nop
      local.get $tmp1
      local.tee $"#local6 this"
      i32.const 1
      i32.add
      br 0 (;@1;)
    end
    struct.set $kotlin.collections.Itr $index
    local.get $<unary>
    struct.set $kotlin.collections.Itr $lastIndex
    local.get $tmp0_<this>
    struct.get $kotlin.collections.Itr $list
    struct.get $kotlin.collections.ArrayList $backing
    local.get $tmp0_<this>
    struct.get $kotlin.collections.Itr $lastIndex
    call $kotlin.Array.get
    return
  )
  (func $kotlin.collections.ArrayList.<init> (;19;) (type $"#type167 ") (param $<this> (ref null $kotlin.collections.ArrayList)) (param $initialCapacity i32) (result (ref null $kotlin.collections.ArrayList))
    (local $tmp0 i32) (local $size i32) (local $"#local4 tmp0" (@name "tmp0") i32) (local $value i32) (local $message (ref null $kotlin.Any))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.collections.ArrayList.vtable
      global.get $kotlin.collections.ArrayList.classITable
      i32.const 208
      i32.const 0
      i32.const 0
      ref.null $kotlin.Array
      i32.const 0
      i32.const 0
      struct.new $kotlin.collections.ArrayList
      local.set $<this>
    end
    call $kotlin.collections.Companion_getInstance
    drop
    local.get $<this>
    call $kotlin.collections.AbstractMutableList.<init>
    drop
    local.get $<this>
    local.get $initialCapacity
    local.set $tmp0
    block (result (ref null $kotlin.Array)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $size
      i32.const 0
      i32.ge_s
      local.set $"#local4 tmp0"
      block (result (ref null $kotlin.Unit)) ;; label = @2
        nop
        local.get $"#local4 tmp0"
        local.tee $value
        i32.eqz
        if (result (ref null $kotlin.Unit)) ;; label = @3
          block (result (ref null $kotlin.Any)) ;; label = @4
            nop
            i32.const 10
            i32.const 176
            i32.const 30
            call $kotlin.stringLiteral
            br 0 (;@4;)
          end
          local.set $message
          ref.null none
          local.get $message
          local.get $message
          struct.get $kotlin.Any $vtable
          struct.get $kotlin.Any.vtable $toString
          call_ref $"#type170 "
          call $kotlin.IllegalArgumentException.<init>
          throw 0
        else
          global.get $kotlin.Unit_instance
        end
      end
      drop
      ref.null none
      local.get $size
      call $kotlin.Array.<init>
      br 0 (;@1;)
    end
    struct.set $kotlin.collections.ArrayList $backing
    local.get $<this>
    i32.const 0
    struct.set $kotlin.collections.ArrayList $length
    local.get $<this>
    i32.const 0
    struct.set $kotlin.collections.ArrayList $isReadOnly
    nop
    local.get $<this>
    return
  )
  (func $"#func20 kotlin.collections.ArrayList.<init>" (@name "kotlin.collections.ArrayList.<init>") (;20;) (type $"#type168 ") (param $<this> (ref null $kotlin.collections.ArrayList)) (result (ref null $kotlin.collections.ArrayList))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.collections.ArrayList.vtable
      global.get $kotlin.collections.ArrayList.classITable
      i32.const 208
      i32.const 0
      i32.const 0
      ref.null $kotlin.Array
      i32.const 0
      i32.const 0
      struct.new $kotlin.collections.ArrayList
      local.set $<this>
    end
    call $kotlin.collections.Companion_getInstance
    drop
    local.get $<this>
    i32.const 10
    call $kotlin.collections.ArrayList.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.ArrayList.<get-size> (;21;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.collections.ArrayList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.ArrayList)
    local.tee $tmp0_<this>
    struct.get $kotlin.collections.ArrayList $length
    return
  )
  (func $kotlin.collections.ArrayList.isEmpty (;22;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.collections.ArrayList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.ArrayList)
    local.tee $tmp0_<this>
    struct.get $kotlin.collections.ArrayList $length
    i32.const 0
    i32.eq
    return
  )
  (func $kotlin.collections.ArrayList.get (;23;) (type $"#type169 ") (param $<this> (ref null $kotlin.Any)) (param $index i32) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.ArrayList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.ArrayList)
    local.set $tmp0_<this>
    global.get $"#global1 kotlin.collections.Companion_instance"
    local.get $index
    local.get $tmp0_<this>
    struct.get $kotlin.collections.ArrayList $length
    call $kotlin.collections.Companion.checkElementIndex
    local.get $tmp0_<this>
    struct.get $kotlin.collections.ArrayList $backing
    local.get $index
    call $kotlin.Array.get
    return
  )
  (func $kotlin.collections.ArrayList.iterator (;24;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.ArrayList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.ArrayList)
    local.tee $tmp0_<this>
    i32.const 0
    call $kotlin.collections.ArrayList.listIterator
    return
  )
  (func $"#func25 kotlin.collections.ArrayList.iterator" (@name "kotlin.collections.ArrayList.iterator") (;25;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    local.get $<this>
    call $kotlin.collections.ArrayList.iterator
    return
  )
  (func $kotlin.collections.ArrayList.listIterator (;26;) (type $"#type169 ") (param $<this> (ref null $kotlin.Any)) (param $index i32) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.ArrayList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.ArrayList)
    local.set $tmp0_<this>
    global.get $"#global1 kotlin.collections.Companion_instance"
    local.get $index
    local.get $tmp0_<this>
    struct.get $kotlin.collections.ArrayList $length
    call $kotlin.collections.Companion.checkPositionIndex
    ref.null none
    local.get $tmp0_<this>
    local.get $index
    call $kotlin.collections.Itr.<init>
    return
  )
  (func $kotlin.collections.ArrayList.toString (;27;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> (ref null $kotlin.collections.ArrayList)) (local $tmp0 (ref null $kotlin.Array)) (local $tmp1 i32) (local $tmp2 i32) (local $tmp3 (ref null $kotlin.Any)) (local $this (ref null $kotlin.Array)) (local $offset i32) (local $length i32) (local $thisCollection (ref null $kotlin.Any)) (local $sb (ref null $kotlin.text.StringBuilder)) (local $i i32) (local $nextElement (ref null $kotlin.Any)) (local $<unary> i32) (local $"#local14 tmp0" (@name "tmp0") i32) (local $"#local15 this" (@name "this") i32)
    local.get $<this>
    ref.cast (ref null $kotlin.collections.ArrayList)
    local.tee $tmp0_<this>
    struct.get $kotlin.collections.ArrayList $backing
    local.set $tmp0
    i32.const 0
    local.set $tmp1
    local.get $tmp0_<this>
    struct.get $kotlin.collections.ArrayList $length
    local.set $tmp2
    local.get $tmp0_<this>
    local.set $tmp3
    block (result (ref null $kotlin.String)) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      local.get $tmp1
      local.set $offset
      local.get $tmp2
      local.set $length
      local.get $tmp3
      local.set $thisCollection
      ref.null none
      i32.const 2
      local.get $length
      i32.const 3
      i32.mul
      i32.add
      call $"#func30 kotlin.text.StringBuilder.<init>"
      local.tee $sb
      i32.const 11
      i32.const 236
      i32.const 1
      call $kotlin.stringLiteral
      call $"#func40 kotlin.text.StringBuilder.append"
      drop
      i32.const 0
      local.set $i
      loop ;; label = @2
        block ;; label = @3
          local.get $i
          local.get $length
          i32.lt_s
          i32.eqz
          br_if 0 (;@3;)
          local.get $i
          i32.const 0
          i32.gt_s
          if ;; label = @4
            local.get $sb
            i32.const 2
            i32.const 12
            i32.const 2
            call $kotlin.stringLiteral
            call $"#func40 kotlin.text.StringBuilder.append"
            drop
          else
          end
          local.get $this
          local.get $offset
          local.get $i
          i32.add
          call $kotlin.Array.get
          local.tee $nextElement
          local.get $thisCollection
          ref.eq
          if ;; label = @4
            local.get $sb
            i32.const 12
            i32.const 238
            i32.const 17
            call $kotlin.stringLiteral
            call $"#func40 kotlin.text.StringBuilder.append"
            drop
          else
            local.get $sb
            local.get $nextElement
            call $"#func38 kotlin.text.StringBuilder.append"
            drop
          end
          local.get $i
          local.tee $<unary>
          local.set $"#local14 tmp0"
          block (result i32) ;; label = @4
            nop
            local.get $"#local14 tmp0"
            local.tee $"#local15 this"
            i32.const 1
            i32.add
            br 0 (;@4;)
          end
          local.set $i
          br 1 (;@2;)
        end
      end
      local.get $sb
      i32.const 13
      i32.const 272
      i32.const 1
      call $kotlin.stringLiteral
      call $"#func40 kotlin.text.StringBuilder.append"
      drop
      local.get $sb
      call $kotlin.text.StringBuilder.toString
      br 0 (;@1;)
    end
    return
  )
  (func $kotlin.text.StringBuilder.<init> (;28;) (type $"#type172 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $array (ref null $kotlin.CharArray)) (result (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.text.StringBuilder.vtable
      global.get $kotlin.text.StringBuilder.classITable
      i32.const 344
      i32.const 0
      ref.null $kotlin.CharArray
      i32.const 0
      struct.new $kotlin.text.StringBuilder
      local.set $<this>
    end
    local.get $<this>
    local.get $array
    struct.set $kotlin.text.StringBuilder $array
    local.get $<this>
    i32.const 0
    struct.set $kotlin.text.StringBuilder $_length
    nop
    local.get $<this>
    return
  )
  (func $"#func29 kotlin.text.StringBuilder.<init>" (@name "kotlin.text.StringBuilder.<init>") (;29;) (type $"#type173 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (result (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.text.StringBuilder.vtable
      global.get $kotlin.text.StringBuilder.classITable
      i32.const 344
      i32.const 0
      ref.null $kotlin.CharArray
      i32.const 0
      struct.new $kotlin.text.StringBuilder
      local.set $<this>
    end
    local.get $<this>
    i32.const 10
    call $"#func30 kotlin.text.StringBuilder.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func30 kotlin.text.StringBuilder.<init>" (@name "kotlin.text.StringBuilder.<init>") (;30;) (type $"#type174 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $capacity i32) (result (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.text.StringBuilder.vtable
      global.get $kotlin.text.StringBuilder.classITable
      i32.const 344
      i32.const 0
      ref.null $kotlin.CharArray
      i32.const 0
      struct.new $kotlin.text.StringBuilder
      local.set $<this>
    end
    local.get $<this>
    ref.null none
    local.get $capacity
    call $kotlin.CharArray.<init>
    call $kotlin.text.StringBuilder.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.text.StringBuilder.<get-length> (;31;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    ref.cast (ref null $kotlin.text.StringBuilder)
    local.tee $tmp0_<this>
    struct.get $kotlin.text.StringBuilder $_length
    return
  )
  (func $kotlin.text.StringBuilder.get (;32;) (type $"#type154 ") (param $<this> (ref null $kotlin.Any)) (param $index i32) (result i32)
    (local $tmp0_<this> (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    ref.cast (ref null $kotlin.text.StringBuilder)
    local.set $tmp0_<this>
    global.get $"#global1 kotlin.collections.Companion_instance"
    local.get $index
    local.get $tmp0_<this>
    struct.get $kotlin.text.StringBuilder $_length
    call $kotlin.collections.Companion.checkElementIndex
    local.get $tmp0_<this>
    struct.get $kotlin.text.StringBuilder $array
    local.get $index
    call $kotlin.CharArray.get
    return
  )
  (func $kotlin.text.StringBuilder.append (;33;) (type $"#type175 ") (param $<this> (ref null $kotlin.Any)) (param $value i32) (result (ref null $kotlin.text.StringBuilder))
    (local $tmp0_<this> (ref null $kotlin.text.StringBuilder)) (local $<unary> i32) (local $tmp0 i32) (local $this i32)
    local.get $<this>
    ref.cast (ref null $kotlin.text.StringBuilder)
    local.tee $tmp0_<this>
    i32.const 1
    call $kotlin.text.StringBuilder.ensureExtraCapacity
    local.get $tmp0_<this>
    struct.get $kotlin.text.StringBuilder $array
    local.get $tmp0_<this>
    struct.get $kotlin.text.StringBuilder $_length
    local.set $<unary>
    local.get $tmp0_<this>
    local.get $<unary>
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      i32.const 1
      i32.add
      br 0 (;@1;)
    end
    struct.set $kotlin.text.StringBuilder $_length
    local.get $<unary>
    local.get $value
    call $kotlin.CharArray.set
    local.get $tmp0_<this>
    return
  )
  (func $"#func34 kotlin.text.StringBuilder.append" (@name "kotlin.text.StringBuilder.append") (;34;) (type $"#type169 ") (param $<this> (ref null $kotlin.Any)) (param $value i32) (result (ref null $kotlin.Any))
    local.get $<this>
    local.get $value
    call $kotlin.text.StringBuilder.append
    return
  )
  (func $"#func35 kotlin.text.StringBuilder.append" (@name "kotlin.text.StringBuilder.append") (;35;) (type $"#type176 ") (param $<this> (ref null $kotlin.Any)) (param $value (ref null $kotlin.Any)) (result (ref null $kotlin.text.StringBuilder))
    (local $tmp0_<this> (ref null $kotlin.text.StringBuilder)) (local $toAppend (ref null $kotlin.Any)) (local $tmp0_elvis_lhs (ref null $kotlin.Any))
    local.get $<this>
    ref.cast (ref null $kotlin.text.StringBuilder)
    local.set $tmp0_<this>
    local.get $value
    local.tee $tmp0_elvis_lhs
    ref.is_null
    if (result (ref null $kotlin.Any)) ;; label = @1
      i32.const 15
      i32.const 300
      i32.const 4
      call $kotlin.stringLiteral
    else
      local.get $tmp0_elvis_lhs
    end
    local.set $toAppend
    local.get $tmp0_<this>
    local.get $toAppend
    i32.const 0
    local.get $toAppend
    local.get $toAppend
    struct.get $kotlin.Any $itable
    ref.cast (ref $classITable)
    struct.get $classITable $kotlin.itable
    struct.get $kotlin.CharSequence.itable $<get-length>
    call_ref $"#type153 "
    call $"#func37 kotlin.text.StringBuilder.append"
    return
  )
  (func $"#func36 kotlin.text.StringBuilder.append" (@name "kotlin.text.StringBuilder.append") (;36;) (type $"#type171 ") (param $<this> (ref null $kotlin.Any)) (param $value (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    local.get $<this>
    local.get $value
    call $"#func35 kotlin.text.StringBuilder.append"
    return
  )
  (func $"#func37 kotlin.text.StringBuilder.append" (@name "kotlin.text.StringBuilder.append") (;37;) (type $"#type177 ") (param $<this> (ref null $kotlin.Any)) (param $value (ref null $kotlin.Any)) (param $startIndex i32) (param $endIndex i32) (result (ref null $kotlin.text.StringBuilder))
    (local $tmp0_<this> (ref null $kotlin.text.StringBuilder)) (local $tmp0_elvis_lhs (ref null $kotlin.Any))
    local.get $<this>
    ref.cast (ref null $kotlin.text.StringBuilder)
    local.tee $tmp0_<this>
    local.get $value
    local.tee $tmp0_elvis_lhs
    ref.is_null
    if (result (ref null $kotlin.Any)) ;; label = @1
      i32.const 15
      i32.const 300
      i32.const 4
      call $kotlin.stringLiteral
    else
      local.get $tmp0_elvis_lhs
    end
    local.get $startIndex
    local.get $endIndex
    call $kotlin.text.StringBuilder.appendRange
    return
  )
  (func $"#func38 kotlin.text.StringBuilder.append" (@name "kotlin.text.StringBuilder.append") (;38;) (type $"#type178 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $value (ref null $kotlin.Any)) (result (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    local.get $value
    call $kotlin.toString
    call $"#func40 kotlin.text.StringBuilder.append"
    return
  )
  (func $"#func39 kotlin.text.StringBuilder.append" (@name "kotlin.text.StringBuilder.append") (;39;) (type $"#type174 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $value i32) (result (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    i32.const 11
    call $kotlin.text.StringBuilder.ensureExtraCapacity
    local.get $<this>
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $_length
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $array
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $_length
    local.get $value
    call $kotlin.text.insertInt
    i32.add
    struct.set $kotlin.text.StringBuilder $_length
    local.get $<this>
    return
  )
  (func $"#func40 kotlin.text.StringBuilder.append" (@name "kotlin.text.StringBuilder.append") (;40;) (type $"#type179 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $value (ref null $kotlin.String)) (result (ref null $kotlin.text.StringBuilder))
    (local $toAppend (ref null $kotlin.String)) (local $tmp0_elvis_lhs (ref null $kotlin.String))
    local.get $value
    local.tee $tmp0_elvis_lhs
    ref.is_null
    if (result (ref null $kotlin.String)) ;; label = @1
      i32.const 15
      i32.const 300
      i32.const 4
      call $kotlin.stringLiteral
    else
      local.get $tmp0_elvis_lhs
    end
    local.set $toAppend
    local.get $<this>
    local.get $toAppend
    struct.get $kotlin.String $length
    call $kotlin.text.StringBuilder.ensureExtraCapacity
    local.get $<this>
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $_length
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $array
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $_length
    local.get $toAppend
    call $kotlin.text.insertString
    i32.add
    struct.set $kotlin.text.StringBuilder $_length
    local.get $<this>
    return
  )
  (func $kotlin.text.StringBuilder.toString (;41;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    ref.cast (ref null $kotlin.text.StringBuilder)
    local.tee $tmp0_<this>
    struct.get $kotlin.text.StringBuilder $array
    i32.const 0
    local.get $tmp0_<this>
    struct.get $kotlin.text.StringBuilder $_length
    call $kotlin.text.unsafeStringFromCharArray
    return
  )
  (func $kotlin.text.StringBuilder.appendRange (;42;) (type $"#type180 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $value (ref null $kotlin.Any)) (param $startIndex i32) (param $endIndex i32) (result (ref null $kotlin.text.StringBuilder))
    (local $extraLength i32) (local $tmp0_safe_receiver (ref null $kotlin.String)) (local $tmp0 (ref null $kotlin.Any)) (local $this (ref null $kotlin.Any)) (local $tmp1 (ref null $kotlin.String)) (local $it (ref null $kotlin.String)) (local $index i32) (local $<unary> i32) (local $tmp2 i32) (local $"#local13 this" (@name "this") i32) (local $"#local14 <unary>" (@name "<unary>") i32) (local $tmp3 i32) (local $"#local16 this" (@name "this") i32)
    global.get $"#global1 kotlin.collections.Companion_instance"
    local.get $startIndex
    local.get $endIndex
    local.get $value
    local.get $value
    struct.get $kotlin.Any $itable
    ref.cast (ref $classITable)
    struct.get $classITable $kotlin.itable
    struct.get $kotlin.CharSequence.itable $<get-length>
    call_ref $"#type153 "
    call $kotlin.collections.Companion.checkBoundsIndexes
    local.get $endIndex
    local.get $startIndex
    i32.sub
    local.set $extraLength
    local.get $<this>
    local.get $extraLength
    call $kotlin.text.StringBuilder.ensureExtraCapacity
    local.get $value
    ref.test (ref $kotlin.String)
    if (result (ref null $kotlin.String)) ;; label = @1
      local.get $value
      ref.cast (ref null $kotlin.String)
    else
      ref.null none
    end
    local.tee $tmp0_safe_receiver
    ref.is_null
    if ;; label = @1
    else
      local.get $tmp0_safe_receiver
      local.set $tmp0
      block ;; label = @2
        nop
        local.get $tmp0
        local.tee $this
        ref.cast (ref null $kotlin.String)
        local.set $tmp1
        block (result (ref null $kotlin.Any)) ;; label = @3
          nop
          local.get $tmp1
          local.set $it
          local.get $<this>
          local.get $<this>
          struct.get $kotlin.text.StringBuilder $_length
          local.get $<this>
          struct.get $kotlin.text.StringBuilder $array
          local.get $<this>
          struct.get $kotlin.text.StringBuilder $_length
          local.get $it
          local.get $startIndex
          local.get $extraLength
          call $"#func185 kotlin.text.insertString"
          i32.add
          struct.set $kotlin.text.StringBuilder $_length
          local.get $<this>
          return
        end
        drop
        unreachable
      end
      unreachable
    end
    local.get $startIndex
    local.set $index
    loop ;; label = @1
      block ;; label = @2
        local.get $index
        local.get $endIndex
        i32.lt_s
        i32.eqz
        br_if 0 (;@2;)
        local.get $<this>
        struct.get $kotlin.text.StringBuilder $array
        local.get $<this>
        struct.get $kotlin.text.StringBuilder $_length
        local.set $<unary>
        local.get $<this>
        local.get $<unary>
        local.set $tmp2
        block (result i32) ;; label = @3
          nop
          local.get $tmp2
          local.tee $"#local13 this"
          i32.const 1
          i32.add
          br 0 (;@3;)
        end
        struct.set $kotlin.text.StringBuilder $_length
        local.get $<unary>
        local.get $value
        local.get $index
        local.tee $"#local14 <unary>"
        local.set $tmp3
        block (result i32) ;; label = @3
          nop
          local.get $tmp3
          local.tee $"#local16 this"
          i32.const 1
          i32.add
          br 0 (;@3;)
        end
        local.set $index
        local.get $"#local14 <unary>"
        local.get $value
        struct.get $kotlin.Any $itable
        ref.cast (ref $classITable)
        struct.get $classITable $kotlin.itable
        struct.get $kotlin.CharSequence.itable $get
        call_ref $"#type154 "
        call $kotlin.CharArray.set
        br 1 (;@1;)
      end
    end
    local.get $<this>
    return
  )
  (func $kotlin.text.StringBuilder.ensureExtraCapacity (;43;) (type $"#type181 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $n i32)
    local.get $<this>
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $_length
    local.get $n
    i32.add
    call $kotlin.text.StringBuilder.ensureCapacityInternal
    nop
  )
  (func $kotlin.text.StringBuilder.ensureCapacityInternal (;44;) (type $"#type181 ") (param $<this> (ref null $kotlin.text.StringBuilder)) (param $minCapacity i32)
    (local $newSize i32)
    local.get $minCapacity
    i32.const 0
    i32.lt_s
    if ;; label = @1
      ref.null none
      call $kotlin.OutOfMemoryError.<init>
      throw 0
    else
    end
    local.get $minCapacity
    local.get $<this>
    struct.get $kotlin.text.StringBuilder $array
    call $kotlin.CharArray.<get-size>
    i32.gt_s
    if ;; label = @1
      global.get $"#global1 kotlin.collections.Companion_instance"
      local.get $<this>
      struct.get $kotlin.text.StringBuilder $array
      call $kotlin.CharArray.<get-size>
      local.get $minCapacity
      call $kotlin.collections.Companion.newCapacity
      local.set $newSize
      local.get $<this>
      local.get $<this>
      struct.get $kotlin.text.StringBuilder $array
      local.get $newSize
      call $kotlin.collections.copyOf
      struct.set $kotlin.text.StringBuilder $array
    else
    end
    nop
  )
  (func $kotlin.text.insertString (;45;) (type $"#type182 ") (param $array (ref null $kotlin.CharArray)) (param $start i32) (param $value (ref null $kotlin.String)) (result i32)
    local.get $array
    local.get $start
    local.get $value
    i32.const 0
    local.get $value
    struct.get $kotlin.String $length
    call $"#func185 kotlin.text.insertString"
    return
  )
  (func $kotlin.collections.AbstractCollection$toString$lambda.<init> (;46;) (type $"#type183 ") (param $<this> (ref null $kotlin.collections.AbstractCollection$toString$lambda)) (param $this$0 (ref null $kotlin.collections.AbstractCollection)) (result (ref null $kotlin.collections.AbstractCollection$toString$lambda))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.collections.AbstractCollection$toString$lambda.vtable
      global.get $kotlin.collections.AbstractCollection$toString$lambda.classITable
      i32.const 384
      i32.const 0
      ref.null $kotlin.collections.AbstractCollection
      struct.new $kotlin.collections.AbstractCollection$toString$lambda
      local.set $<this>
    end
    local.get $<this>
    local.get $this$0
    struct.set $kotlin.collections.AbstractCollection$toString$lambda $this$0
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.AbstractCollection$toString$lambda.invoke (;47;) (type $"#type171 ") (param $<this> (ref null $kotlin.Any)) (param $it (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.AbstractCollection$toString$lambda))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.AbstractCollection$toString$lambda)
    local.set $tmp0_<this>
    local.get $it
    local.get $tmp0_<this>
    struct.get $kotlin.collections.AbstractCollection$toString$lambda $this$0
    ref.eq
    if (result (ref null $kotlin.Any)) ;; label = @1
      i32.const 12
      i32.const 238
      i32.const 17
      call $kotlin.stringLiteral
    else
      local.get $it
      call $kotlin.toString
    end
    return
  )
  (func $"#func48 kotlin.collections.AbstractCollection$toString$lambda.invoke" (@name "kotlin.collections.AbstractCollection$toString$lambda.invoke") (;48;) (type $"#type171 ") (param $<this> (ref null $kotlin.Any)) (param $p1 (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    local.get $<this>
    local.get $p1
    call $kotlin.collections.AbstractCollection$toString$lambda.invoke
    return
  )
  (func $kotlin.collections.AbstractCollection.<init> (;49;) (type $"#type184 ") (param $<this> (ref null $kotlin.collections.AbstractCollection)) (result (ref null $kotlin.collections.AbstractCollection))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.AbstractCollection.isEmpty (;50;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.collections.AbstractCollection))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.AbstractCollection)
    local.tee $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.collections.AbstractCollection $vtable
    struct.get $kotlin.collections.AbstractCollection.vtable $<get-size>
    call_ref $"#type153 "
    i32.const 0
    i32.eq
    return
  )
  (func $kotlin.collections.AbstractCollection.toString (;51;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> (ref null $kotlin.collections.AbstractCollection))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.AbstractCollection)
    local.tee $tmp0_<this>
    i32.const 2
    i32.const 12
    i32.const 2
    call $kotlin.stringLiteral
    i32.const 11
    i32.const 236
    i32.const 1
    call $kotlin.stringLiteral
    i32.const 13
    i32.const 272
    i32.const 1
    call $kotlin.stringLiteral
    ref.null none
    ref.null none
    ref.null none
    local.get $tmp0_<this>
    call $kotlin.collections.AbstractCollection$toString$lambda.<init>
    i32.const 24
    ref.null none
    call $kotlin.collections.joinToString$default
    return
  )
  (func $"#func52 kotlin.collections.IteratorImpl.<init>" (@name "kotlin.collections.IteratorImpl.<init>") (;52;) (type $"#type185 ") (param $<this> (ref null $"#type81 kotlin.collections.IteratorImpl")) (param $$outer (ref null $kotlin.collections.AbstractList)) (result (ref null $"#type81 kotlin.collections.IteratorImpl"))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $"#global116 kotlin.collections.IteratorImpl.vtable"
      global.get $"#global161 kotlin.collections.IteratorImpl.classITable"
      i32.const 452
      i32.const 0
      i32.const 0
      ref.null $kotlin.collections.AbstractList
      struct.new $"#type81 kotlin.collections.IteratorImpl"
      local.set $<this>
    end
    local.get $<this>
    local.get $$outer
    struct.set $"#type81 kotlin.collections.IteratorImpl" $$this
    local.get $<this>
    i32.const 0
    struct.set $"#type81 kotlin.collections.IteratorImpl" $index
    nop
    local.get $<this>
    return
  )
  (func $"#func53 kotlin.collections.IteratorImpl.hasNext" (@name "kotlin.collections.IteratorImpl.hasNext") (;53;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $"#type81 kotlin.collections.IteratorImpl")) (local $tmp (ref null $kotlin.collections.AbstractList))
    local.get $<this>
    ref.cast (ref null $"#type81 kotlin.collections.IteratorImpl")
    local.tee $tmp0_<this>
    struct.get $"#type81 kotlin.collections.IteratorImpl" $index
    local.get $tmp0_<this>
    struct.get $"#type81 kotlin.collections.IteratorImpl" $$this
    local.tee $tmp
    local.get $tmp
    struct.get $kotlin.collections.AbstractList $vtable
    struct.get $kotlin.collections.AbstractList.vtable $<get-size>
    call_ref $"#type153 "
    i32.lt_s
    return
  )
  (func $"#func54 kotlin.collections.IteratorImpl.next" (@name "kotlin.collections.IteratorImpl.next") (;54;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $"#type81 kotlin.collections.IteratorImpl")) (local $tmp (ref null $kotlin.collections.AbstractList)) (local $<unary> i32) (local $tmp0 i32) (local $this i32)
    local.get $<this>
    ref.cast (ref null $"#type81 kotlin.collections.IteratorImpl")
    local.tee $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $"#type81 kotlin.collections.IteratorImpl" $vtable
    struct.get $"#type50 kotlin.collections.IteratorImpl.vtable" $hasNext
    call_ref $"#type153 "
    i32.eqz
    if ;; label = @1
      ref.null none
      call $kotlin.NoSuchElementException.<init>
      throw 0
    else
    end
    local.get $tmp0_<this>
    struct.get $"#type81 kotlin.collections.IteratorImpl" $$this
    local.tee $tmp
    local.get $tmp0_<this>
    struct.get $"#type81 kotlin.collections.IteratorImpl" $index
    local.set $<unary>
    local.get $tmp0_<this>
    local.get $<unary>
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      i32.const 1
      i32.add
      br 0 (;@1;)
    end
    struct.set $"#type81 kotlin.collections.IteratorImpl" $index
    local.get $<unary>
    local.get $tmp
    struct.get $kotlin.collections.AbstractList $vtable
    struct.get $kotlin.collections.AbstractList.vtable $get
    call_ref $"#type169 "
    return
  )
  (func $"#func55 kotlin.collections.Companion.<init>" (@name "kotlin.collections.Companion.<init>") (;55;) (type $"#type186 ") (param $<this> (ref null $"#type82 kotlin.collections.Companion")) (result (ref null $"#type82 kotlin.collections.Companion"))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $"#global117 kotlin.collections.Companion.vtable"
      ref.null struct
      i32.const 488
      i32.const 0
      i32.const 0
      struct.new $"#type82 kotlin.collections.Companion"
      local.set $<this>
    end
    local.get $<this>
    i32.const 2147483639
    struct.set $"#type82 kotlin.collections.Companion" $maxArraySize
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.Companion.checkElementIndex (;56;) (type $"#type187 ") (param $<this> (ref null $"#type82 kotlin.collections.Companion")) (param $index i32) (param $size i32)
    (local $tmp (ref null $kotlin.text.StringBuilder))
    local.get $index
    i32.const 0
    i32.lt_s
    if (result i32) ;; label = @1
      i32.const 1
    else
      local.get $index
      local.get $size
      i32.ge_s
    end
    if ;; label = @1
      ref.null none
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $tmp
      i32.const 18
      i32.const 412
      i32.const 7
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $index
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 19
      i32.const 426
      i32.const 8
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $size
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $tmp
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
      call $"#func161 kotlin.IndexOutOfBoundsException.<init>"
      throw 0
    else
    end
    nop
  )
  (func $kotlin.collections.Companion.checkPositionIndex (;57;) (type $"#type187 ") (param $<this> (ref null $"#type82 kotlin.collections.Companion")) (param $index i32) (param $size i32)
    (local $tmp (ref null $kotlin.text.StringBuilder))
    local.get $index
    i32.const 0
    i32.lt_s
    if (result i32) ;; label = @1
      i32.const 1
    else
      local.get $index
      local.get $size
      i32.gt_s
    end
    if ;; label = @1
      ref.null none
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $tmp
      i32.const 18
      i32.const 412
      i32.const 7
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $index
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 19
      i32.const 426
      i32.const 8
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $size
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $tmp
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
      call $"#func161 kotlin.IndexOutOfBoundsException.<init>"
      throw 0
    else
    end
    nop
  )
  (func $kotlin.collections.Companion.checkRangeIndexes (;58;) (type $"#type188 ") (param $<this> (ref null $"#type82 kotlin.collections.Companion")) (param $fromIndex i32) (param $toIndex i32) (param $size i32)
    (local $tmp (ref null $kotlin.text.StringBuilder)) (local $"#local5 tmp" (@name "tmp") (ref null $kotlin.text.StringBuilder))
    local.get $fromIndex
    i32.const 0
    i32.lt_s
    if (result i32) ;; label = @1
      i32.const 1
    else
      local.get $toIndex
      local.get $size
      i32.gt_s
    end
    if ;; label = @1
      ref.null none
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $tmp
      i32.const 20
      i32.const 442
      i32.const 11
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $fromIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 21
      i32.const 464
      i32.const 11
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $toIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 19
      i32.const 426
      i32.const 8
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $size
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $tmp
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
      call $"#func161 kotlin.IndexOutOfBoundsException.<init>"
      throw 0
    else
    end
    local.get $fromIndex
    local.get $toIndex
    i32.gt_s
    if ;; label = @1
      ref.null none
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $"#local5 tmp"
      i32.const 20
      i32.const 442
      i32.const 11
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      local.get $fromIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      i32.const 22
      i32.const 486
      i32.const 12
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      local.get $toIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      local.get $"#local5 tmp"
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
      call $kotlin.IllegalArgumentException.<init>
      throw 0
    else
    end
    nop
  )
  (func $kotlin.collections.Companion.checkBoundsIndexes (;59;) (type $"#type188 ") (param $<this> (ref null $"#type82 kotlin.collections.Companion")) (param $startIndex i32) (param $endIndex i32) (param $size i32)
    (local $tmp (ref null $kotlin.text.StringBuilder)) (local $"#local5 tmp" (@name "tmp") (ref null $kotlin.text.StringBuilder))
    local.get $startIndex
    i32.const 0
    i32.lt_s
    if (result i32) ;; label = @1
      i32.const 1
    else
      local.get $endIndex
      local.get $size
      i32.gt_s
    end
    if ;; label = @1
      ref.null none
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $tmp
      i32.const 23
      i32.const 510
      i32.const 12
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $startIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 24
      i32.const 534
      i32.const 12
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $endIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 19
      i32.const 426
      i32.const 8
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $size
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $tmp
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
      call $"#func161 kotlin.IndexOutOfBoundsException.<init>"
      throw 0
    else
    end
    local.get $startIndex
    local.get $endIndex
    i32.gt_s
    if ;; label = @1
      ref.null none
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $"#local5 tmp"
      i32.const 23
      i32.const 510
      i32.const 12
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      local.get $startIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      i32.const 25
      i32.const 558
      i32.const 13
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      local.get $endIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $"#local5 tmp"
      local.get $"#local5 tmp"
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
      call $kotlin.IllegalArgumentException.<init>
      throw 0
    else
    end
    nop
  )
  (func $kotlin.collections.Companion.newCapacity (;60;) (type $"#type189 ") (param $<this> (ref null $"#type82 kotlin.collections.Companion")) (param $oldCapacity i32) (param $minCapacity i32) (result i32)
    (local $newCapacity i32)
    local.get $oldCapacity
    local.get $oldCapacity
    i32.const 1
    i32.shr_s
    i32.add
    local.tee $newCapacity
    local.get $minCapacity
    i32.sub
    i32.const 0
    i32.lt_s
    if ;; label = @1
      local.get $minCapacity
      local.set $newCapacity
    else
    end
    local.get $newCapacity
    i32.const 2147483639
    i32.sub
    i32.const 0
    i32.gt_s
    if ;; label = @1
      local.get $minCapacity
      i32.const 2147483639
      i32.gt_s
      if (result i32) ;; label = @2
        i32.const 2147483647
      else
        i32.const 2147483639
      end
      local.set $newCapacity
    else
    end
    local.get $newCapacity
    return
  )
  (func $kotlin.collections.AbstractList.<init> (;61;) (type $"#type190 ") (param $<this> (ref null $kotlin.collections.AbstractList)) (result (ref null $kotlin.collections.AbstractList))
    local.get $<this>
    call $kotlin.collections.AbstractCollection.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.collections.AbstractList.iterator (;62;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $kotlin.collections.AbstractList))
    local.get $<this>
    ref.cast (ref null $kotlin.collections.AbstractList)
    local.set $tmp0_<this>
    ref.null none
    local.get $tmp0_<this>
    call $"#func52 kotlin.collections.IteratorImpl.<init>"
    return
  )
  (func $kotlin.enums.enumEntries (;63;) (type $"#type191 ") (param $entries (ref null $kotlin.Array)) (result (ref null $kotlin.Any))
    ref.null none
    local.get $entries
    call $kotlin.enums.EnumEntriesList.<init>
    return
  )
  (func $kotlin.enums.EnumEntriesList.<init> (;64;) (type $"#type192 ") (param $<this> (ref null $kotlin.enums.EnumEntriesList)) (param $entries (ref null $kotlin.Array)) (result (ref null $kotlin.enums.EnumEntriesList))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.enums.EnumEntriesList.vtable
      global.get $kotlin.enums.EnumEntriesList.classITable
      i32.const 548
      i32.const 0
      ref.null $kotlin.Array
      struct.new $kotlin.enums.EnumEntriesList
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.collections.AbstractList.<init>
    drop
    local.get $<this>
    local.get $entries
    struct.set $kotlin.enums.EnumEntriesList $entries
    nop
    local.get $<this>
    return
  )
  (func $kotlin.enums.EnumEntriesList.<get-size> (;65;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.enums.EnumEntriesList))
    local.get $<this>
    ref.cast (ref null $kotlin.enums.EnumEntriesList)
    local.tee $tmp0_<this>
    struct.get $kotlin.enums.EnumEntriesList $entries
    call $kotlin.Array.<get-size>
    return
  )
  (func $kotlin.enums.EnumEntriesList.get (;66;) (type $"#type193 ") (param $<this> (ref null $kotlin.Any)) (param $index i32) (result (ref null $kotlin.Enum))
    (local $tmp0_<this> (ref null $kotlin.enums.EnumEntriesList))
    local.get $<this>
    ref.cast (ref null $kotlin.enums.EnumEntriesList)
    local.set $tmp0_<this>
    global.get $"#global1 kotlin.collections.Companion_instance"
    local.get $index
    local.get $tmp0_<this>
    struct.get $kotlin.enums.EnumEntriesList $entries
    call $kotlin.Array.<get-size>
    call $kotlin.collections.Companion.checkElementIndex
    local.get $tmp0_<this>
    struct.get $kotlin.enums.EnumEntriesList $entries
    local.get $index
    call $kotlin.Array.get
    ref.cast (ref null $kotlin.Enum)
    return
  )
  (func $"#func67 kotlin.enums.EnumEntriesList.get" (@name "kotlin.enums.EnumEntriesList.get") (;67;) (type $"#type169 ") (param $<this> (ref null $kotlin.Any)) (param $index i32) (result (ref null $kotlin.Any))
    local.get $<this>
    local.get $index
    call $kotlin.enums.EnumEntriesList.get
    return
  )
  (func $kotlin.random.Default.<init> (;68;) (type $"#type194 ") (param $<this> (ref null $kotlin.random.Default)) (result (ref null $kotlin.random.Default))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.random.Default.vtable
      global.get $kotlin.random.Default.classITable
      i32.const 620
      i32.const 0
      ref.null $kotlin.random.Random
      struct.new $kotlin.random.Default
      local.set $<this>
    end
    local.get $<this>
    global.set $kotlin.random.Default_instance
    local.get $<this>
    call $kotlin.random.Random.<init>
    drop
    local.get $<this>
    call $kotlin.random.defaultPlatformRandom
    struct.set $kotlin.random.Default $defaultRandom
    nop
    local.get $<this>
    return
  )
  (func $kotlin.random.Default.nextBits (;69;) (type $"#type154 ") (param $<this> (ref null $kotlin.Any)) (param $bitCount i32) (result i32)
    (local $tmp0_<this> (ref null $kotlin.random.Default)) (local $tmp (ref null $kotlin.random.Random))
    local.get $<this>
    ref.cast (ref null $kotlin.random.Default)
    local.tee $tmp0_<this>
    struct.get $kotlin.random.Default $defaultRandom
    local.tee $tmp
    local.get $bitCount
    local.get $tmp
    struct.get $kotlin.random.Random $vtable
    struct.get $kotlin.random.Random.vtable $nextBits
    call_ref $"#type154 "
    return
  )
  (func $kotlin.random.Default.nextInt (;70;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.random.Default)) (local $tmp (ref null $kotlin.random.Random))
    local.get $<this>
    ref.cast (ref null $kotlin.random.Default)
    local.tee $tmp0_<this>
    struct.get $kotlin.random.Default $defaultRandom
    local.tee $tmp
    local.get $tmp
    struct.get $kotlin.random.Random $vtable
    struct.get $kotlin.random.Random.vtable $nextInt
    call_ref $"#type153 "
    return
  )
  (func $"#func71 kotlin.random.Default.nextInt" (@name "kotlin.random.Default.nextInt") (;71;) (type $"#type195 ") (param $<this> (ref null $kotlin.Any)) (param $from i32) (param $until i32) (result i32)
    (local $tmp0_<this> (ref null $kotlin.random.Default)) (local $tmp (ref null $kotlin.random.Random))
    local.get $<this>
    ref.cast (ref null $kotlin.random.Default)
    local.tee $tmp0_<this>
    struct.get $kotlin.random.Default $defaultRandom
    local.tee $tmp
    local.get $from
    local.get $until
    local.get $tmp
    struct.get $kotlin.random.Random $vtable
    struct.get $kotlin.random.Random.vtable $"#field3 nextInt"
    call_ref $"#type195 "
    return
  )
  (func $kotlin.random.Default_getInstance (;72;) (type $"#type196 ") (result (ref null $kotlin.random.Default))
    global.get $kotlin.random.Default_instance
    ref.is_null
    if ;; label = @1
      ref.null none
      call $kotlin.random.Default.<init>
      drop
    else
    end
    global.get $kotlin.random.Default_instance
    return
  )
  (func $kotlin.random.Random.<init> (;73;) (type $"#type197 ") (param $<this> (ref null $kotlin.random.Random)) (result (ref null $kotlin.random.Random))
    call $kotlin.random.Default_getInstance
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.random.Random.nextInt (;74;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.random.Random))
    local.get $<this>
    ref.cast (ref null $kotlin.random.Random)
    local.tee $tmp0_<this>
    i32.const 32
    local.get $tmp0_<this>
    struct.get $kotlin.random.Random $vtable
    struct.get $kotlin.random.Random.vtable $nextBits
    call_ref $"#type154 "
    return
  )
  (func $"#func75 kotlin.random.Random.nextInt" (@name "kotlin.random.Random.nextInt") (;75;) (type $"#type195 ") (param $<this> (ref null $kotlin.Any)) (param $from i32) (param $until i32) (result i32)
    (local $tmp0_<this> (ref null $kotlin.random.Random)) (local $n i32) (local $rnd i32) (local $tmp0 i32) (local $this i32) (local $bitCount i32) (local $v i32) (local $bits i32) (local $"#local11 rnd" (@name "rnd") i32)
    local.get $<this>
    ref.cast (ref null $kotlin.random.Random)
    local.set $tmp0_<this>
    local.get $from
    local.get $until
    call $kotlin.random.checkRangeBounds
    local.get $until
    local.get $from
    i32.sub
    local.tee $n
    i32.const 0
    i32.gt_s
    if (result i32) ;; label = @1
      i32.const 1
    else
      local.get $n
      i32.const -2147483648
      i32.eq
    end
    if ;; label = @1
      local.get $n
      local.get $n
      local.set $tmp0
      block (result i32) ;; label = @2
        nop
        local.get $tmp0
        local.set $this
        i32.const 0
        local.get $this
        i32.sub
        br 0 (;@2;)
      end
      i32.and
      local.get $n
      i32.eq
      if (result i32) ;; label = @2
        local.get $n
        call $kotlin.random.fastLog2
        local.set $bitCount
        local.get $tmp0_<this>
        local.get $bitCount
        local.get $tmp0_<this>
        struct.get $kotlin.random.Random $vtable
        struct.get $kotlin.random.Random.vtable $nextBits
        call_ref $"#type154 "
      else
        loop ;; label = @3
          block ;; label = @4
            block ;; label = @5
              local.get $tmp0_<this>
              local.get $tmp0_<this>
              struct.get $kotlin.random.Random $vtable
              struct.get $kotlin.random.Random.vtable $nextInt
              call_ref $"#type153 "
              i32.const 1
              i32.shr_u
              local.tee $bits
              local.get $n
              i32.rem_s
              local.set $v
            end
            local.get $bits
            local.get $v
            i32.sub
            local.get $n
            i32.const 1
            i32.sub
            i32.add
            i32.const 0
            i32.lt_s
            br_if 1 (;@3;)
          end
        end
        local.get $v
      end
      local.set $rnd
      local.get $from
      local.get $rnd
      i32.add
      return
    else
      loop ;; label = @2
        block ;; label = @3
          i32.const 1
          i32.eqz
          br_if 0 (;@3;)
          local.get $tmp0_<this>
          local.get $tmp0_<this>
          struct.get $kotlin.random.Random $vtable
          struct.get $kotlin.random.Random.vtable $nextInt
          call_ref $"#type153 "
          local.set $"#local11 rnd"
          local.get $from
          local.get $"#local11 rnd"
          i32.le_s
          if (result i32) ;; label = @4
            local.get $"#local11 rnd"
            local.get $until
            i32.lt_s
          else
            i32.const 0
          end
          if ;; label = @4
            local.get $"#local11 rnd"
            return
          else
          end
          br 1 (;@2;)
        end
      end
    end
    unreachable
  )
  (func $kotlin.random.checkRangeBounds (;76;) (type 1) (param $from i32) (param $until i32)
    (local $tmp0 i32) (local $value i32) (local $message (ref null $kotlin.Any))
    local.get $until
    local.get $from
    i32.gt_s
    local.set $tmp0
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $value
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        block (result (ref null $kotlin.Any)) ;; label = @3
          nop
          global.get $kotlin.Int.vtable
          global.get $kotlin.Int.classITable
          i32.const 1208
          i32.const 0
          local.get $from
          struct.new $kotlin.Int
          global.get $kotlin.Int.vtable
          global.get $kotlin.Int.classITable
          i32.const 1208
          i32.const 0
          local.get $until
          struct.new $kotlin.Int
          call $kotlin.random.boundsErrorMessage
          br 0 (;@3;)
        end
        local.set $message
        ref.null none
        local.get $message
        local.get $message
        struct.get $kotlin.Any $vtable
        struct.get $kotlin.Any.vtable $toString
        call_ref $"#type170 "
        call $kotlin.IllegalArgumentException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    return
  )
  (func $kotlin.random.fastLog2 (;77;) (type 2) (param $value i32) (result i32)
    i32.const 31
    local.get $value
    i32.clz
    i32.sub
    return
  )
  (func $kotlin.random.boundsErrorMessage (;78;) (type $"#type198 ") (param $from (ref null $kotlin.Any)) (param $until (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp (ref null $kotlin.text.StringBuilder))
    ref.null none
    call $"#func29 kotlin.text.StringBuilder.<init>"
    local.tee $tmp
    i32.const 30
    i32.const 664
    i32.const 24
    call $kotlin.stringLiteral
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    local.get $from
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    i32.const 2
    i32.const 12
    i32.const 2
    call $kotlin.stringLiteral
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    local.get $until
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    i32.const 31
    i32.const 712
    i32.const 2
    call $kotlin.stringLiteral
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    local.get $tmp
    struct.get $kotlin.Any $vtable
    struct.get $kotlin.Any.vtable $toString
    call_ref $"#type170 "
    return
  )
  (func $kotlin.random.Random (;79;) (type $"#type199 ") (param $seed i64) (result (ref null $kotlin.random.Random))
    (local $tmp0 i64) (local $tmp1 i32) (local $this i64) (local $bitCount i32)
    ref.null none
    local.get $seed
    call $kotlin.Long__toInt-impl
    local.get $seed
    local.set $tmp0
    i32.const 32
    local.set $tmp1
    block (result i64) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      local.get $tmp1
      local.set $bitCount
      local.get $this
      local.get $bitCount
      call $kotlin.Int__toLong-impl
      i64.shr_s
      br 0 (;@1;)
    end
    call $kotlin.Long__toInt-impl
    call $"#func83 kotlin.random.XorWowRandom.<init>"
    return
  )
  (func $kotlin.random.takeUpperBits (;80;) (type 0) (param $<this> i32) (param $bitCount i32) (result i32)
    (local $tmp0 i32) (local $this i32)
    local.get $<this>
    i32.const 32
    local.get $bitCount
    i32.sub
    i32.shr_u
    local.get $bitCount
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      i32.const 0
      local.get $this
      i32.sub
      br 0 (;@1;)
    end
    i32.const 31
    i32.shr_s
    i32.and
    return
  )
  (func $kotlin.random.Companion.<init> (;81;) (type $"#type200 ") (param $<this> (ref null $kotlin.random.Companion)) (result (ref null $kotlin.random.Companion))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.random.Companion.vtable
      ref.null struct
      i32.const 684
      i32.const 0
      i64.const 0
      struct.new $kotlin.random.Companion
      local.set $<this>
    end
    local.get $<this>
    i64.const 0
    struct.set $kotlin.random.Companion $serialVersionUID
    nop
    local.get $<this>
    return
  )
  (func $kotlin.random.XorWowRandom.<init> (;82;) (type $"#type201 ") (param $<this> (ref null $kotlin.random.XorWowRandom)) (param $x i32) (param $y i32) (param $z i32) (param $w i32) (param $v i32) (param $addend i32) (result (ref null $kotlin.random.XorWowRandom))
    (local $tmp0 i32) (local $value i32) (local $message (ref null $kotlin.Any)) (local $tmp1 i32) (local $times i32) (local $inductionVariable i32) (local $index i32) (local $tmp2 i32) (local $it i32)
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.random.XorWowRandom.vtable
      global.get $kotlin.random.XorWowRandom.classITable
      i32.const 716
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      struct.new $kotlin.random.XorWowRandom
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.random.Random.<init>
    drop
    local.get $<this>
    local.get $x
    struct.set $kotlin.random.XorWowRandom $x
    local.get $<this>
    local.get $y
    struct.set $kotlin.random.XorWowRandom $y
    local.get $<this>
    local.get $z
    struct.set $kotlin.random.XorWowRandom $z
    local.get $<this>
    local.get $w
    struct.set $kotlin.random.XorWowRandom $w
    local.get $<this>
    local.get $v
    struct.set $kotlin.random.XorWowRandom $v
    local.get $<this>
    local.get $addend
    struct.set $kotlin.random.XorWowRandom $addend
    local.get $<this>
    struct.get $kotlin.random.XorWowRandom $x
    local.get $<this>
    struct.get $kotlin.random.XorWowRandom $y
    i32.or
    local.get $<this>
    struct.get $kotlin.random.XorWowRandom $z
    i32.or
    local.get $<this>
    struct.get $kotlin.random.XorWowRandom $w
    i32.or
    local.get $<this>
    struct.get $kotlin.random.XorWowRandom $v
    i32.or
    i32.const 0
    i32.eq
    i32.eqz
    local.set $tmp0
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $value
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        block (result (ref null $kotlin.Any)) ;; label = @3
          nop
          i32.const 33
          i32.const 740
          i32.const 54
          call $kotlin.stringLiteral
          br 0 (;@3;)
        end
        local.set $message
        ref.null none
        local.get $message
        local.get $message
        struct.get $kotlin.Any $vtable
        struct.get $kotlin.Any.vtable $toString
        call_ref $"#type170 "
        call $kotlin.IllegalArgumentException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    i32.const 64
    local.set $tmp1
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp1
      local.set $times
      i32.const 0
      local.tee $inductionVariable
      local.get $times
      i32.lt_s
      if (result (ref null $kotlin.Unit)) ;; label = @2
        loop ;; label = @3
          block ;; label = @4
            block ;; label = @5
              local.get $inductionVariable
              local.set $index
              local.get $inductionVariable
              i32.const 1
              i32.add
              local.set $inductionVariable
              local.get $index
              local.set $tmp2
              block (result (ref null $kotlin.Unit)) ;; label = @6
                nop
                local.get $tmp2
                local.set $it
                local.get $<this>
                call $kotlin.random.XorWowRandom.nextInt
                drop
                global.get $kotlin.Unit_instance
              end
              drop
            end
            local.get $inductionVariable
            local.get $times
            i32.lt_s
            br_if 1 (;@3;)
          end
        end
        global.get $kotlin.Unit_instance
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func83 kotlin.random.XorWowRandom.<init>" (@name "kotlin.random.XorWowRandom.<init>") (;83;) (type $"#type202 ") (param $<this> (ref null $kotlin.random.XorWowRandom)) (param $seed1 i32) (param $seed2 i32) (result (ref null $kotlin.random.XorWowRandom))
    (local $tmp0 i32) (local $this i32)
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.random.XorWowRandom.vtable
      global.get $kotlin.random.XorWowRandom.classITable
      i32.const 716
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      struct.new $kotlin.random.XorWowRandom
      local.set $<this>
    end
    local.get $<this>
    local.get $seed1
    local.get $seed2
    i32.const 0
    i32.const 0
    local.get $seed1
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      i32.const -1
      i32.xor
      br 0 (;@1;)
    end
    local.get $seed1
    i32.const 10
    i32.shl
    local.get $seed2
    i32.const 4
    i32.shr_u
    i32.xor
    call $kotlin.random.XorWowRandom.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.random.XorWowRandom.nextInt (;84;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.random.XorWowRandom)) (local $t i32) (local $v0 i32)
    local.get $<this>
    ref.cast (ref null $kotlin.random.XorWowRandom)
    local.tee $tmp0_<this>
    struct.get $kotlin.random.XorWowRandom $x
    local.tee $t
    local.get $t
    i32.const 2
    i32.shr_u
    i32.xor
    local.set $t
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.random.XorWowRandom $y
    struct.set $kotlin.random.XorWowRandom $x
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.random.XorWowRandom $z
    struct.set $kotlin.random.XorWowRandom $y
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.random.XorWowRandom $w
    struct.set $kotlin.random.XorWowRandom $z
    local.get $tmp0_<this>
    struct.get $kotlin.random.XorWowRandom $v
    local.set $v0
    local.get $tmp0_<this>
    local.get $v0
    struct.set $kotlin.random.XorWowRandom $w
    local.get $t
    local.get $t
    i32.const 1
    i32.shl
    i32.xor
    local.get $v0
    i32.xor
    local.get $v0
    i32.const 4
    i32.shl
    i32.xor
    local.set $t
    local.get $tmp0_<this>
    local.get $t
    struct.set $kotlin.random.XorWowRandom $v
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.random.XorWowRandom $addend
    i32.const 362437
    i32.add
    struct.set $kotlin.random.XorWowRandom $addend
    local.get $t
    local.get $tmp0_<this>
    struct.get $kotlin.random.XorWowRandom $addend
    i32.add
    return
  )
  (func $kotlin.random.XorWowRandom.nextBits (;85;) (type $"#type154 ") (param $<this> (ref null $kotlin.Any)) (param $bitCount i32) (result i32)
    (local $tmp0_<this> (ref null $kotlin.random.XorWowRandom))
    local.get $<this>
    ref.cast (ref null $kotlin.random.XorWowRandom)
    local.tee $tmp0_<this>
    call $kotlin.random.XorWowRandom.nextInt
    local.get $bitCount
    call $kotlin.random.takeUpperBits
    return
  )
  (func $kotlin.text.appendElement (;86;) (type $"#type203 ") (param $<this> (ref null $kotlin.Any)) (param $element (ref null $kotlin.Any)) (param $transform (ref null $kotlin.Any))
    (local $IS_INTERFACE_PARAMETER (ref null $kotlin.Any))
    local.get $transform
    ref.is_null
    i32.eqz
    if ;; label = @1
      local.get $<this>
      local.get $transform
      local.get $element
      local.get $transform
      struct.get $kotlin.Any $itable
      ref.cast (ref $"#type40 classITable")
      struct.get $"#type40 classITable" $"#field1 kotlin.itable"
      struct.get $kotlin.Function1.itable $invoke
      call_ref $"#type171 "
      local.get $<this>
      struct.get $kotlin.Any $itable
      ref.cast (ref $classITable)
      struct.get $classITable $kotlin.text.itable
      struct.get $kotlin.text.Appendable.itable $"#field1 append"
      call_ref $"#type171 "
      drop
    else
      local.get $element
      ref.is_null
      if (result i32) ;; label = @2
        i32.const 1
      else
        local.get $element
        local.set $IS_INTERFACE_PARAMETER
        block (result i32) ;; label = @3
          block (result structref) ;; label = @4
            local.get $IS_INTERFACE_PARAMETER
            struct.get $kotlin.Any $itable
            br_on_cast_fail 0 (;@4;) structref (ref $classITable)
            struct.get $classITable $kotlin.itable
            ref.is_null
            i32.eqz
            br 1 (;@3;)
          end
          drop
          i32.const 0
        end
      end
      if ;; label = @2
        local.get $<this>
        local.get $element
        local.get $<this>
        struct.get $kotlin.Any $itable
        ref.cast (ref $classITable)
        struct.get $classITable $kotlin.text.itable
        struct.get $kotlin.text.Appendable.itable $"#field1 append"
        call_ref $"#type171 "
        drop
      else
        local.get $element
        ref.is_null
        if (result i32) ;; label = @3
          i32.const 0
        else
          local.get $element
          ref.test (ref $kotlin.Char)
        end
        if ;; label = @3
          local.get $<this>
          local.get $element
          ref.cast (ref $kotlin.Char)
          struct.get_u $kotlin.Char $value
          local.get $<this>
          struct.get $kotlin.Any $itable
          ref.cast (ref $classITable)
          struct.get $classITable $kotlin.text.itable
          struct.get $kotlin.text.Appendable.itable $append
          call_ref $"#type169 "
          drop
        else
          local.get $<this>
          local.get $element
          local.get $element
          struct.get $kotlin.Any $vtable
          struct.get $kotlin.Any.vtable $toString
          call_ref $"#type170 "
          local.get $<this>
          struct.get $kotlin.Any $itable
          ref.cast (ref $classITable)
          struct.get $classITable $kotlin.text.itable
          struct.get $kotlin.text.Appendable.itable $"#field1 append"
          call_ref $"#type171 "
          drop
        end
      end
    end
    nop
  )
  (func $kotlin.<UInt__<init>-impl> (;87;) (type 2) (param $data i32) (result i32)
    local.get $data
    return
  )
  (func $kotlin.<UInt__<get-data>-impl> (;88;) (type 2) (param $$this i32) (result i32)
    local.get $$this
    return
  )
  (func $kotlin.Companion.<init> (;89;) (type $"#type204 ") (param $<this> (ref null $kotlin.Companion)) (result (ref null $kotlin.Companion))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Companion.vtable
      ref.null struct
      i32.const 752
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      struct.new $kotlin.Companion
      local.set $<this>
    end
    local.get $<this>
    i32.const 0
    struct.set $kotlin.Companion $MIN_VALUE
    local.get $<this>
    i32.const -1
    struct.set $kotlin.Companion $MAX_VALUE
    local.get $<this>
    i32.const 4
    struct.set $kotlin.Companion $SIZE_BYTES
    local.get $<this>
    i32.const 32
    struct.set $kotlin.Companion $SIZE_BITS
    nop
    local.get $<this>
    return
  )
  (func $kotlin.UInt__toString-impl (;90;) (type $"#type205 ") (param $$this i32) (result (ref null $kotlin.String))
    (local $tmp0 i32) (local $value i32) (local $"#local3 tmp0" (@name "tmp0") i32) (local $this i32)
    local.get $$this
    call $kotlin.<UInt__<get-data>-impl>
    local.set $tmp0
    block (result (ref null $kotlin.String)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $value
      local.set $"#local3 tmp0"
      block (result i32) ;; label = @2
        nop
        local.get $"#local3 tmp0"
        local.tee $this
        call $kotlin.<UInt__<init>-impl>
        br 0 (;@2;)
      end
      call $kotlin.wasm.internal.utoa32
      br 0 (;@1;)
    end
    return
  )
  (func $kotlin.UInt.toString (;91;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> i32)
    local.get $<this>
    ref.cast (ref $kotlin.UInt)
    struct.get $kotlin.UInt $data
    local.tee $tmp0_<this>
    call $kotlin.UInt__toString-impl
    return
  )
  (func $kotlin.<ULong__<init>-impl> (;92;) (type 3) (param $data i64) (result i64)
    local.get $data
    return
  )
  (func $kotlin.<ULong__<get-data>-impl> (;93;) (type 3) (param $$this i64) (result i64)
    local.get $$this
    return
  )
  (func $"#func94 kotlin.Companion.<init>" (@name "kotlin.Companion.<init>") (;94;) (type $"#type206 ") (param $<this> (ref null $"#type87 kotlin.Companion")) (result (ref null $"#type87 kotlin.Companion"))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $"#global124 kotlin.Companion.vtable"
      ref.null struct
      i32.const 820
      i32.const 0
      i64.const 0
      i64.const 0
      i32.const 0
      i32.const 0
      struct.new $"#type87 kotlin.Companion"
      local.set $<this>
    end
    local.get $<this>
    i64.const 0
    struct.set $"#type87 kotlin.Companion" $MIN_VALUE
    local.get $<this>
    i64.const -1
    struct.set $"#type87 kotlin.Companion" $MAX_VALUE
    local.get $<this>
    i32.const 8
    struct.set $"#type87 kotlin.Companion" $SIZE_BYTES
    local.get $<this>
    i32.const 64
    struct.set $"#type87 kotlin.Companion" $SIZE_BITS
    nop
    local.get $<this>
    return
  )
  (func $kotlin.ULong__toString-impl (;95;) (type $"#type207 ") (param $$this i64) (result (ref null $kotlin.String))
    (local $tmp0 i64) (local $value i64) (local $"#local3 tmp0" (@name "tmp0") i64) (local $this i64)
    local.get $$this
    call $kotlin.<ULong__<get-data>-impl>
    local.set $tmp0
    block (result (ref null $kotlin.String)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $value
      local.set $"#local3 tmp0"
      block (result i64) ;; label = @2
        nop
        local.get $"#local3 tmp0"
        local.tee $this
        call $kotlin.<ULong__<init>-impl>
        br 0 (;@2;)
      end
      call $kotlin.wasm.internal.utoa64
      br 0 (;@1;)
    end
    return
  )
  (func $kotlin.ULong.toString (;96;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> i64)
    local.get $<this>
    ref.cast (ref $kotlin.ULong)
    struct.get $kotlin.ULong $data
    local.tee $tmp0_<this>
    call $kotlin.ULong__toString-impl
    return
  )
  (func $kotlin.Any.toString (;97;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $typeInfoPtr i32) (local $packageName (ref null $kotlin.String)) (local $simpleName (ref null $kotlin.String)) (local $qualifiedName (ref null $kotlin.String)) (local $tmp0 (ref null $kotlin.Any)) (local $this (ref null $kotlin.Any)) (local $tmp (ref null $kotlin.text.StringBuilder)) (local $"#local8 tmp" (@name "tmp") (ref null $kotlin.text.StringBuilder))
    local.get $<this>
    struct.get $kotlin.Any $typeInfo
    local.tee $typeInfoPtr
    call $kotlin.wasm.internal.getPackageName
    local.set $packageName
    local.get $typeInfoPtr
    call $kotlin.wasm.internal.getSimpleName
    local.set $simpleName
    local.get $packageName
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      local.get $this
      struct.get $kotlin.Any $itable
      ref.cast (ref $classITable)
      struct.get $classITable $kotlin.itable
      struct.get $kotlin.CharSequence.itable $<get-length>
      call_ref $"#type153 "
      i32.const 0
      i32.eq
      br 0 (;@1;)
    end
    if (result (ref null $kotlin.String)) ;; label = @1
      local.get $simpleName
    else
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $tmp
      local.get $packageName
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 37
      i32.const 872
      i32.const 1
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $simpleName
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $tmp
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
    end
    local.set $qualifiedName
    ref.null none
    call $"#func29 kotlin.text.StringBuilder.<init>"
    local.tee $"#local8 tmp"
    local.get $qualifiedName
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $"#local8 tmp"
    i32.const 38
    i32.const 874
    i32.const 1
    call $kotlin.stringLiteral
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $"#local8 tmp"
    local.get $<this>
    call $kotlin.identityHashCode
    call $"#func39 kotlin.text.StringBuilder.append"
    drop
    local.get $"#local8 tmp"
    local.get $"#local8 tmp"
    struct.get $kotlin.Any $vtable
    struct.get $kotlin.Any.vtable $toString
    call_ref $"#type170 "
    return
  )
  (func $kotlin.identityHashCode (;98;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    local.get $<this>
    struct.get $kotlin.Any $_hashCode
    i32.const 0
    i32.eq
    if ;; label = @1
      local.get $<this>
      call $kotlin.random.Default_getInstance
      i32.const 1
      i32.const 2147483647
      call $"#func71 kotlin.random.Default.nextInt"
      struct.set $kotlin.Any $_hashCode
    else
    end
    local.get $<this>
    struct.get $kotlin.Any $_hashCode
    return
  )
  (func $kotlin.Array.<init> (;99;) (type $"#type208 ") (param $<this> (ref null $kotlin.Array)) (param $size i32) (result (ref null $kotlin.Array))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Array.vtable
      ref.null struct
      i32.const 920
      i32.const 0
      ref.null $kotlin.wasm.internal.WasmAnyArray
      struct.new $kotlin.Array
      local.set $<this>
    end
    local.get $size
    i32.const 0
    i32.lt_s
    if ;; label = @1
      ref.null none
      i32.const 40
      i32.const 886
      i32.const 19
      call $kotlin.stringLiteral
      call $kotlin.IllegalArgumentException.<init>
      throw 0
    else
    end
    local.get $<this>
    local.get $size
    array.new_default $kotlin.wasm.internal.WasmAnyArray
    struct.set $kotlin.Array $storage
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Array.get (;100;) (type $"#type209 ") (param $<this> (ref null $kotlin.Array)) (param $index i32) (result (ref null $kotlin.Any))
    local.get $<this>
    struct.get $kotlin.Array $storage
    local.get $index
    array.get $kotlin.wasm.internal.WasmAnyArray
    return
  )
  (func $kotlin.Array.set (;101;) (type $"#type210 ") (param $<this> (ref null $kotlin.Array)) (param $index i32) (param $value (ref null $kotlin.Any))
    local.get $<this>
    struct.get $kotlin.Array $storage
    local.get $index
    local.get $value
    array.set $kotlin.wasm.internal.WasmAnyArray
    nop
  )
  (func $kotlin.Array.<get-size> (;102;) (type $"#type211 ") (param $<this> (ref null $kotlin.Array)) (result i32)
    local.get $<this>
    struct.get $kotlin.Array $storage
    array.len
    return
  )
  (func $kotlin.CharArray.<init> (;103;) (type $"#type212 ") (param $<this> (ref null $kotlin.CharArray)) (param $size i32) (result (ref null $kotlin.CharArray))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.CharArray.vtable
      ref.null struct
      i32.const 1016
      i32.const 0
      ref.null $kotlin.wasm.internal.WasmCharArray
      struct.new $kotlin.CharArray
      local.set $<this>
    end
    local.get $size
    i32.const 0
    i32.lt_s
    if ;; label = @1
      ref.null none
      i32.const 40
      i32.const 886
      i32.const 19
      call $kotlin.stringLiteral
      call $kotlin.IllegalArgumentException.<init>
      throw 0
    else
    end
    local.get $<this>
    local.get $size
    array.new_default $kotlin.wasm.internal.WasmCharArray
    struct.set $kotlin.CharArray $storage
    nop
    local.get $<this>
    return
  )
  (func $kotlin.CharArray.get (;104;) (type $"#type213 ") (param $<this> (ref null $kotlin.CharArray)) (param $index i32) (result i32)
    local.get $<this>
    struct.get $kotlin.CharArray $storage
    local.get $index
    array.get_u $kotlin.wasm.internal.WasmCharArray
    return
  )
  (func $kotlin.CharArray.set (;105;) (type $"#type214 ") (param $<this> (ref null $kotlin.CharArray)) (param $index i32) (param $value i32)
    local.get $<this>
    struct.get $kotlin.CharArray $storage
    local.get $index
    local.get $value
    array.set $kotlin.wasm.internal.WasmCharArray
    nop
  )
  (func $kotlin.CharArray.<get-size> (;106;) (type $"#type215 ") (param $<this> (ref null $kotlin.CharArray)) (result i32)
    local.get $<this>
    struct.get $kotlin.CharArray $storage
    array.len
    return
  )
  (func $kotlin.Char__toString-impl (;107;) (type $"#type205 ") (param $$this i32) (result (ref null $kotlin.String))
    (local $array (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp0 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $this (ref null $kotlin.wasm.internal.WasmCharArray))
    i32.const 1
    array.new_default $kotlin.wasm.internal.WasmCharArray
    local.tee $array
    i32.const 0
    local.get $$this
    array.set $kotlin.wasm.internal.WasmCharArray
    local.get $array
    local.set $tmp0
    block (result (ref null $kotlin.String)) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      global.get $kotlin.String.vtable
      global.get $kotlin.String.classITable
      i32.const 1276
      i32.const 0
      ref.null none
      local.get $this
      array.len
      local.get $this
      struct.new $kotlin.String
      br 0 (;@1;)
    end
    return
  )
  (func $"#func108 kotlin.Companion.<init>" (@name "kotlin.Companion.<init>") (;108;) (type $"#type216 ") (param $<this> (ref null $"#type93 kotlin.Companion")) (result (ref null $"#type93 kotlin.Companion"))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $"#global131 kotlin.Companion.vtable"
      ref.null struct
      i32.const 1048
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      struct.new $"#type93 kotlin.Companion"
      local.set $<this>
    end
    local.get $<this>
    i32.const 0
    struct.set $"#type93 kotlin.Companion" $MIN_VALUE
    local.get $<this>
    i32.const 65535
    struct.set $"#type93 kotlin.Companion" $MAX_VALUE
    local.get $<this>
    i32.const 55296
    struct.set $"#type93 kotlin.Companion" $MIN_HIGH_SURROGATE
    local.get $<this>
    i32.const 56319
    struct.set $"#type93 kotlin.Companion" $MAX_HIGH_SURROGATE
    local.get $<this>
    i32.const 56320
    struct.set $"#type93 kotlin.Companion" $MIN_LOW_SURROGATE
    local.get $<this>
    i32.const 57343
    struct.set $"#type93 kotlin.Companion" $MAX_LOW_SURROGATE
    local.get $<this>
    i32.const 55296
    struct.set $"#type93 kotlin.Companion" $MIN_SURROGATE
    local.get $<this>
    i32.const 57343
    struct.set $"#type93 kotlin.Companion" $MAX_SURROGATE
    local.get $<this>
    i32.const 2
    struct.set $"#type93 kotlin.Companion" $SIZE_BYTES
    local.get $<this>
    i32.const 16
    struct.set $"#type93 kotlin.Companion" $SIZE_BITS
    local.get $<this>
    i32.const 65536
    struct.set $"#type93 kotlin.Companion" $MIN_SUPPLEMENTARY_CODE_POINT
    local.get $<this>
    i32.const 0
    struct.set $"#type93 kotlin.Companion" $MIN_CODE_POINT
    local.get $<this>
    i32.const 1114111
    struct.set $"#type93 kotlin.Companion" $MAX_CODE_POINT
    local.get $<this>
    i32.const 2
    struct.set $"#type93 kotlin.Companion" $MIN_RADIX
    local.get $<this>
    i32.const 36
    struct.set $"#type93 kotlin.Companion" $MAX_RADIX
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Char.toString (;109;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> i32)
    local.get $<this>
    ref.cast (ref $kotlin.Char)
    struct.get_u $kotlin.Char $value
    local.tee $tmp0_<this>
    call $kotlin.Char__toString-impl
    return
  )
  (func $"#func110 kotlin.Companion.<init>" (@name "kotlin.Companion.<init>") (;110;) (type $"#type217 ") (param $<this> (ref null $"#type95 kotlin.Companion")) (result (ref null $"#type95 kotlin.Companion"))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $"#global133 kotlin.Companion.vtable"
      ref.null struct
      i32.const 1116
      i32.const 0
      struct.new $"#type95 kotlin.Companion"
      local.set $<this>
    end
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Enum.<init> (;111;) (type $"#type218 ") (param $<this> (ref null $kotlin.Enum)) (param $name (ref null $kotlin.String)) (param $ordinal i32) (result (ref null $kotlin.Enum))
    local.get $<this>
    local.get $name
    struct.set $kotlin.Enum $name
    local.get $<this>
    local.get $ordinal
    struct.set $kotlin.Enum $ordinal
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Enum.toString (;112;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> (ref null $kotlin.Enum))
    local.get $<this>
    ref.cast (ref null $kotlin.Enum)
    local.tee $tmp0_<this>
    struct.get $kotlin.Enum $name
    return
  )
  (func $kotlin.toString (;113;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp1_elvis_lhs (ref null $kotlin.String)) (local $tmp0_safe_receiver (ref null $kotlin.Any))
    local.get $<this>
    local.tee $tmp0_safe_receiver
    ref.is_null
    if (result (ref null $kotlin.String)) ;; label = @1
      ref.null none
    else
      local.get $tmp0_safe_receiver
      local.get $tmp0_safe_receiver
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
    end
    local.tee $tmp1_elvis_lhs
    ref.is_null
    if (result (ref null $kotlin.String)) ;; label = @1
      i32.const 15
      i32.const 300
      i32.const 4
      call $kotlin.stringLiteral
    else
      local.get $tmp1_elvis_lhs
    end
    return
  )
  (func $"#func114 kotlin.Companion.<init>" (@name "kotlin.Companion.<init>") (;114;) (type $"#type219 ") (param $<this> (ref null $"#type97 kotlin.Companion")) (result (ref null $"#type97 kotlin.Companion"))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $"#global134 kotlin.Companion.vtable"
      ref.null struct
      i32.const 1176
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      i32.const 0
      struct.new $"#type97 kotlin.Companion"
      local.set $<this>
    end
    local.get $<this>
    i32.const -2147483648
    struct.set $"#type97 kotlin.Companion" $MIN_VALUE
    local.get $<this>
    i32.const 2147483647
    struct.set $"#type97 kotlin.Companion" $MAX_VALUE
    local.get $<this>
    i32.const 4
    struct.set $"#type97 kotlin.Companion" $SIZE_BYTES
    local.get $<this>
    i32.const 32
    struct.set $"#type97 kotlin.Companion" $SIZE_BITS
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Int__div-impl (;115;) (type 0) (param $$this i32) (param $other i32) (result i32)
    local.get $$this
    i32.const -2147483648
    i32.eq
    if (result i32) ;; label = @1
      local.get $other
      i32.const -1
      i32.eq
    else
      i32.const 0
    end
    if (result i32) ;; label = @1
      i32.const -2147483648
    else
      local.get $$this
      local.get $other
      i32.div_s
    end
    return
  )
  (func $kotlin.Int__dec-impl (;116;) (type 2) (param $$this i32) (result i32)
    local.get $$this
    i32.const 1
    i32.sub
    return
  )
  (func $kotlin.Int__toChar-impl (;117;) (type 2) (param $$this i32) (result i32)
    local.get $$this
    i32.const 65535
    i32.and
    return
  )
  (func $kotlin.Int__toLong-impl (;118;) (type 4) (param $$this i32) (result i64)
    local.get $$this
    i64.extend_i32_s
    return
  )
  (func $kotlin.Int__toString-impl (;119;) (type $"#type205 ") (param $$this i32) (result (ref null $kotlin.String))
    local.get $$this
    call $kotlin.wasm.internal.itoa32
    return
  )
  (func $kotlin.Int.toString (;120;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> i32)
    local.get $<this>
    ref.cast (ref $kotlin.Int)
    struct.get $kotlin.Int $value
    local.tee $tmp0_<this>
    call $kotlin.Int__toString-impl
    return
  )
  (func $kotlin.Long__toInt-impl (;121;) (type 5) (param $$this i64) (result i32)
    local.get $$this
    i32.wrap_i64
    return
  )
  (func $"#func122 kotlin.Companion.<init>" (@name "kotlin.Companion.<init>") (;122;) (type $"#type220 ") (param $<this> (ref null $"#type98 kotlin.Companion")) (result (ref null $"#type98 kotlin.Companion"))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $"#global136 kotlin.Companion.vtable"
      ref.null struct
      i32.const 1244
      i32.const 0
      struct.new $"#type98 kotlin.Companion"
      local.set $<this>
    end
    nop
    local.get $<this>
    return
  )
  (func $kotlin.String.<get-length> (;123;) (type $"#type153 ") (param $<this> (ref null $kotlin.Any)) (result i32)
    (local $tmp0_<this> (ref null $kotlin.String))
    local.get $<this>
    ref.cast (ref null $kotlin.String)
    local.tee $tmp0_<this>
    struct.get $kotlin.String $length
    return
  )
  (func $kotlin.String.plus (;124;) (type $"#type221 ") (param $<this> (ref null $kotlin.String)) (param $other (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $right (ref null $kotlin.String)) (local $tmp0 (ref null $kotlin.String)) (local $this (ref null $kotlin.String))
    local.get $other
    call $kotlin.toString
    local.set $right
    global.get $kotlin.String.vtable
    global.get $kotlin.String.classITable
    i32.const 1276
    i32.const 0
    local.get $<this>
    local.get $<this>
    struct.get $kotlin.String $length
    local.get $right
    struct.get $kotlin.String $length
    i32.add
    local.get $right
    local.set $tmp0
    block (result (ref null $kotlin.wasm.internal.WasmCharArray)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      struct.get $kotlin.String $leftIfInSum
      ref.is_null
      i32.eqz
      if ;; label = @2
        local.get $this
        call $kotlin.String.foldChars
      else
      end
      local.get $this
      struct.get $kotlin.String $_chars
      br 0 (;@1;)
    end
    struct.new $kotlin.String
    return
  )
  (func $kotlin.String.get (;125;) (type $"#type154 ") (param $<this> (ref null $kotlin.Any)) (param $index i32) (result i32)
    (local $tmp0_<this> (ref null $kotlin.String)) (local $tmp0 (ref null $kotlin.String)) (local $this (ref null $kotlin.String))
    local.get $<this>
    ref.cast (ref null $kotlin.String)
    local.tee $tmp0_<this>
    local.set $tmp0
    block (result (ref null $kotlin.wasm.internal.WasmCharArray)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      struct.get $kotlin.String $leftIfInSum
      ref.is_null
      i32.eqz
      if ;; label = @2
        local.get $this
        call $kotlin.String.foldChars
      else
      end
      local.get $this
      struct.get $kotlin.String $_chars
      br 0 (;@1;)
    end
    local.get $index
    array.get_u $kotlin.wasm.internal.WasmCharArray
    return
  )
  (func $kotlin.String.foldChars (;126;) (type $"#type222 ") (param $<this> (ref null $kotlin.String))
    (local $stringLength i32) (local $newArray (ref null $kotlin.wasm.internal.WasmCharArray)) (local $currentStartIndex i32) (local $currentLeftString (ref null $kotlin.String)) (local $currentLeftStringChars (ref null $kotlin.wasm.internal.WasmCharArray)) (local $currentLeftStringLen i32) (local $tmp0 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp1 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp2 i32) (local $tmp3 i32) (local $tmp4 i32) (local $source (ref null $kotlin.wasm.internal.WasmCharArray)) (local $destination (ref null $kotlin.wasm.internal.WasmCharArray)) (local $sourceIndex i32) (local $destinationIndex i32) (local $length i32) (local $tmp5 i32) (local $value i32)
    local.get $<this>
    struct.get $kotlin.String $length
    local.tee $stringLength
    array.new_default $kotlin.wasm.internal.WasmCharArray
    local.set $newArray
    local.get $stringLength
    local.set $currentStartIndex
    local.get $<this>
    local.set $currentLeftString
    loop ;; label = @1
      block ;; label = @2
        local.get $currentLeftString
        ref.is_null
        i32.eqz
        i32.eqz
        br_if 0 (;@2;)
        local.get $currentLeftString
        struct.get $kotlin.String $_chars
        local.tee $currentLeftStringChars
        array.len
        local.set $currentLeftStringLen
        local.get $currentStartIndex
        local.get $currentLeftStringLen
        i32.sub
        local.set $currentStartIndex
        local.get $currentLeftStringChars
        local.set $tmp0
        local.get $newArray
        local.set $tmp1
        i32.const 0
        local.set $tmp2
        local.get $currentStartIndex
        local.set $tmp3
        local.get $currentLeftStringLen
        local.set $tmp4
        block (result (ref null $kotlin.Unit)) ;; label = @3
          nop
          local.get $tmp0
          local.set $source
          local.get $tmp1
          local.set $destination
          local.get $tmp2
          local.set $sourceIndex
          local.get $tmp3
          local.set $destinationIndex
          local.get $tmp4
          local.set $length
          local.get $destination
          local.get $destinationIndex
          local.get $source
          local.get $sourceIndex
          local.get $length
          array.copy $kotlin.wasm.internal.WasmCharArray $kotlin.wasm.internal.WasmCharArray
          global.get $kotlin.Unit_instance
        end
        drop
        local.get $currentLeftString
        struct.get $kotlin.String $leftIfInSum
        local.set $currentLeftString
        br 1 (;@1;)
      end
    end
    local.get $currentStartIndex
    i32.const 0
    i32.eq
    local.set $tmp5
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp5
      local.tee $value
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        ref.null none
        i32.const 48
        i32.const 1014
        i32.const 13
        call $kotlin.stringLiteral
        call $kotlin.IllegalStateException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    local.get $<this>
    local.get $newArray
    struct.set $kotlin.String $_chars
    local.get $<this>
    ref.null none
    struct.set $kotlin.String $leftIfInSum
    nop
  )
  (func $kotlin.String.toString (;127;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> (ref null $kotlin.String))
    local.get $<this>
    ref.cast (ref null $kotlin.String)
    local.tee $tmp0_<this>
    return
  )
  (func $kotlin.stringLiteral (;128;) (type $"#type223 ") (param $poolId i32) (param $startAddress i32) (param $length i32) (result (ref null $kotlin.String))
    (local $cached (ref null $kotlin.String)) (local $chars (ref null $kotlin.wasm.internal.WasmCharArray)) (local $newString (ref null $kotlin.String))
    global.get $kotlin.wasm.internal.stringPool
    local.get $poolId
    call $kotlin.Array.get
    ref.cast (ref null $kotlin.String)
    local.tee $cached
    ref.is_null
    i32.eqz
    if ;; label = @1
      local.get $cached
      return
    else
    end
    local.get $startAddress
    local.get $length
    array.new_data $kotlin.wasm.internal.WasmCharArray 0
    ref.cast (ref null $kotlin.wasm.internal.WasmCharArray)
    local.set $chars
    global.get $kotlin.String.vtable
    global.get $kotlin.String.classITable
    i32.const 1276
    i32.const 0
    ref.null none
    local.get $length
    local.get $chars
    struct.new $kotlin.String
    local.set $newString
    global.get $kotlin.wasm.internal.stringPool
    local.get $poolId
    local.get $newString
    call $kotlin.Array.set
    local.get $newString
    return
  )
  (func $kotlin.wasm.internal.itoa32 (;129;) (type $"#type205 ") (param $inputValue i32) (result (ref null $kotlin.String))
    (local $isNegative i32) (local $absValue i32) (local $tmp0 i32) (local $this i32) (local $absValueString (ref null $kotlin.String)) (local $tmp1 i32) (local $"#local7 this" (@name "this") i32)
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    local.get $inputValue
    i32.const 0
    i32.eq
    if ;; label = @1
      i32.const 49
      i32.const 1040
      i32.const 1
      call $kotlin.stringLiteral
      return
    else
    end
    local.get $inputValue
    i32.const 0
    i32.lt_s
    local.tee $isNegative
    if (result i32) ;; label = @1
      local.get $inputValue
      local.set $tmp0
      block (result i32) ;; label = @2
        nop
        local.get $tmp0
        local.set $this
        i32.const 0
        local.get $this
        i32.sub
        br 0 (;@2;)
      end
    else
      local.get $inputValue
    end
    local.tee $absValue
    local.set $tmp1
    block (result i32) ;; label = @1
      nop
      local.get $tmp1
      local.tee $"#local7 this"
      call $kotlin.<UInt__<init>-impl>
      br 0 (;@1;)
    end
    call $kotlin.wasm.internal.utoa32
    local.set $absValueString
    local.get $isNegative
    if (result (ref null $kotlin.String)) ;; label = @1
      i32.const 50
      i32.const 1042
      i32.const 1
      call $kotlin.stringLiteral
      local.get $absValueString
      call $kotlin.String.plus
    else
      local.get $absValueString
    end
    return
  )
  (func $kotlin.wasm.internal.utoa32 (;130;) (type $"#type205 ") (param $inputValue i32) (result (ref null $kotlin.String))
    (local $decimals i32) (local $buf (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp0 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $this (ref null $kotlin.wasm.internal.WasmCharArray))
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    local.get $inputValue
    i32.const 0
    i32.eq
    if ;; label = @1
      i32.const 49
      i32.const 1040
      i32.const 1
      call $kotlin.stringLiteral
      return
    else
    end
    local.get $inputValue
    call $kotlin.wasm.internal.decimalCount32
    local.tee $decimals
    array.new_default $kotlin.wasm.internal.WasmCharArray
    local.tee $buf
    local.get $inputValue
    local.get $decimals
    call $kotlin.wasm.internal.utoaDecSimple
    local.get $buf
    local.set $tmp0
    block (result (ref null $kotlin.String)) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      global.get $kotlin.String.vtable
      global.get $kotlin.String.classITable
      i32.const 1276
      i32.const 0
      ref.null none
      local.get $this
      array.len
      local.get $this
      struct.new $kotlin.String
      br 0 (;@1;)
    end
    return
  )
  (func $kotlin.wasm.internal.utoa64 (;131;) (type $"#type207 ") (param $inputValue i64) (result (ref null $kotlin.String))
    (local $tmp0 i64) (local $tmp1 i32) (local $this i64) (local $other i32) (local $"#local5 tmp1" (@name "tmp1") i64) (local $tmp2 i64) (local $"#local7 tmp0" (@name "tmp0") i32) (local $"#local8 this" (@name "this") i32) (local $"#local9 this" (@name "this") i64) (local $"#local10 other" (@name "other") i64) (local $"#local11 tmp0" (@name "tmp0") i64) (local $"#local12 tmp1" (@name "tmp1") i64) (local $v1 i64) (local $v2 i64) (local $"#local15 tmp2" (@name "tmp2") i64) (local $"#local16 this" (@name "this") i64) (local $"#local17 tmp0" (@name "tmp0") i64) (local $"#local18 this" (@name "this") i64) (local $decimals i32) (local $buf (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp3 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $"#local22 this" (@name "this") (ref null $kotlin.wasm.internal.WasmCharArray))
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    local.get $inputValue
    local.set $tmp0
    i32.const -1
    local.set $tmp1
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      local.get $tmp1
      local.set $other
      local.get $this
      local.set $"#local5 tmp1"
      local.get $other
      local.set $"#local7 tmp0"
      block (result i64) ;; label = @2
        nop
        local.get $"#local7 tmp0"
        local.tee $"#local8 this"
        call $kotlin.<UInt__<get-data>-impl>
        i64.extend_i32_u
        br 0 (;@2;)
      end
      local.set $tmp2
      block (result i32) ;; label = @2
        nop
        local.get $"#local5 tmp1"
        local.set $"#local9 this"
        local.get $tmp2
        local.set $"#local10 other"
        local.get $"#local9 this"
        call $kotlin.<ULong__<get-data>-impl>
        local.set $"#local11 tmp0"
        local.get $"#local10 other"
        call $kotlin.<ULong__<get-data>-impl>
        local.set $"#local12 tmp1"
        block (result i32) ;; label = @3
          nop
          local.get $"#local11 tmp0"
          local.set $v1
          local.get $"#local12 tmp1"
          local.set $v2
          local.get $v1
          local.get $v2
          call $kotlin.wasm.internal.wasm_u64_compareTo
          br 0 (;@3;)
        end
        br 0 (;@2;)
      end
      br 0 (;@1;)
    end
    i32.const 0
    i32.le_s
    if ;; label = @1
      local.get $inputValue
      local.set $"#local15 tmp2"
      block (result i32) ;; label = @2
        nop
        local.get $"#local15 tmp2"
        local.tee $"#local16 this"
        call $kotlin.<ULong__<get-data>-impl>
        local.set $"#local17 tmp0"
        block (result i32) ;; label = @3
          nop
          local.get $"#local17 tmp0"
          local.tee $"#local18 this"
          call $kotlin.Long__toInt-impl
          call $kotlin.<UInt__<init>-impl>
          br 0 (;@3;)
        end
        br 0 (;@2;)
      end
      call $kotlin.wasm.internal.utoa32
      return
    else
    end
    local.get $inputValue
    call $kotlin.wasm.internal.decimalCount64High
    local.tee $decimals
    array.new_default $kotlin.wasm.internal.WasmCharArray
    local.tee $buf
    local.get $inputValue
    local.get $decimals
    call $kotlin.wasm.internal.utoaDecSimple64
    local.get $buf
    local.set $tmp3
    block (result (ref null $kotlin.String)) ;; label = @1
      nop
      local.get $tmp3
      local.set $"#local22 this"
      global.get $kotlin.String.vtable
      global.get $kotlin.String.classITable
      i32.const 1276
      i32.const 0
      ref.null none
      local.get $"#local22 this"
      array.len
      local.get $"#local22 this"
      struct.new $kotlin.String
      br 0 (;@1;)
    end
    return
  )
  (func $kotlin.wasm.internal.decimalCount32 (;132;) (type 2) (param $value i32) (result i32)
    (local $tmp0 i32) (local $tmp1 i32) (local $this i32) (local $other i32) (local $"#local5 tmp0" (@name "tmp0") i32) (local $"#local6 tmp1" (@name "tmp1") i32) (local $v1 i32) (local $v2 i32) (local $tmp2 i32) (local $tmp3 i32) (local $"#local11 this" (@name "this") i32) (local $"#local12 other" (@name "other") i32) (local $"#local13 tmp0" (@name "tmp0") i32) (local $"#local14 tmp1" (@name "tmp1") i32) (local $"#local15 v1" (@name "v1") i32) (local $"#local16 v2" (@name "v2") i32) (local $tmp4 i32) (local $tmp5 i32) (local $"#local19 this" (@name "this") i32) (local $"#local20 other" (@name "other") i32) (local $"#local21 tmp0" (@name "tmp0") i32) (local $"#local22 tmp1" (@name "tmp1") i32) (local $"#local23 v1" (@name "v1") i32) (local $"#local24 v2" (@name "v2") i32) (local $tmp6 i32) (local $tmp7 i32) (local $"#local27 this" (@name "this") i32) (local $"#local28 other" (@name "other") i32) (local $"#local29 tmp0" (@name "tmp0") i32) (local $"#local30 tmp1" (@name "tmp1") i32) (local $"#local31 v1" (@name "v1") i32) (local $"#local32 v2" (@name "v2") i32) (local $tmp8 i32) (local $tmp9 i32) (local $"#local35 this" (@name "this") i32) (local $"#local36 other" (@name "other") i32) (local $"#local37 tmp0" (@name "tmp0") i32) (local $"#local38 tmp1" (@name "tmp1") i32) (local $"#local39 v1" (@name "v1") i32) (local $"#local40 v2" (@name "v2") i32) (local $tmp10 i32) (local $tmp11 i32) (local $"#local43 this" (@name "this") i32) (local $"#local44 other" (@name "other") i32) (local $"#local45 tmp0" (@name "tmp0") i32) (local $"#local46 tmp1" (@name "tmp1") i32) (local $"#local47 v1" (@name "v1") i32) (local $"#local48 v2" (@name "v2") i32) (local $tmp12 i32) (local $tmp13 i32) (local $"#local51 this" (@name "this") i32) (local $"#local52 other" (@name "other") i32) (local $"#local53 tmp0" (@name "tmp0") i32) (local $"#local54 tmp1" (@name "tmp1") i32) (local $"#local55 v1" (@name "v1") i32) (local $"#local56 v2" (@name "v2") i32) (local $tmp14 i32) (local $tmp15 i32) (local $"#local59 this" (@name "this") i32) (local $"#local60 other" (@name "other") i32) (local $"#local61 tmp0" (@name "tmp0") i32) (local $"#local62 tmp1" (@name "tmp1") i32) (local $"#local63 v1" (@name "v1") i32) (local $"#local64 v2" (@name "v2") i32) (local $tmp16 i32) (local $tmp17 i32) (local $"#local67 this" (@name "this") i32) (local $"#local68 other" (@name "other") i32) (local $"#local69 tmp0" (@name "tmp0") i32) (local $"#local70 tmp1" (@name "tmp1") i32) (local $"#local71 v1" (@name "v1") i32) (local $"#local72 v2" (@name "v2") i32)
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    local.get $value
    local.set $tmp0
    i32.const 100000
    local.set $tmp1
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      local.get $tmp1
      local.set $other
      local.get $this
      call $kotlin.<UInt__<get-data>-impl>
      local.set $"#local5 tmp0"
      local.get $other
      call $kotlin.<UInt__<get-data>-impl>
      local.set $"#local6 tmp1"
      block (result i32) ;; label = @2
        nop
        local.get $"#local5 tmp0"
        local.set $v1
        local.get $"#local6 tmp1"
        local.set $v2
        local.get $v1
        local.get $v2
        call $kotlin.wasm.internal.wasm_u32_compareTo
        br 0 (;@2;)
      end
      br 0 (;@1;)
    end
    i32.const 0
    i32.lt_s
    if ;; label = @1
      local.get $value
      local.set $tmp2
      i32.const 100
      local.set $tmp3
      block (result i32) ;; label = @2
        nop
        local.get $tmp2
        local.set $"#local11 this"
        local.get $tmp3
        local.set $"#local12 other"
        local.get $"#local11 this"
        call $kotlin.<UInt__<get-data>-impl>
        local.set $"#local13 tmp0"
        local.get $"#local12 other"
        call $kotlin.<UInt__<get-data>-impl>
        local.set $"#local14 tmp1"
        block (result i32) ;; label = @3
          nop
          local.get $"#local13 tmp0"
          local.set $"#local15 v1"
          local.get $"#local14 tmp1"
          local.set $"#local16 v2"
          local.get $"#local15 v1"
          local.get $"#local16 v2"
          call $kotlin.wasm.internal.wasm_u32_compareTo
          br 0 (;@3;)
        end
        br 0 (;@2;)
      end
      i32.const 0
      i32.lt_s
      if ;; label = @2
        i32.const 1
        local.get $value
        local.set $tmp4
        i32.const 10
        local.set $tmp5
        block (result i32) ;; label = @3
          nop
          local.get $tmp4
          local.set $"#local19 this"
          local.get $tmp5
          local.set $"#local20 other"
          local.get $"#local19 this"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local21 tmp0"
          local.get $"#local20 other"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local22 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local21 tmp0"
            local.set $"#local23 v1"
            local.get $"#local22 tmp1"
            local.set $"#local24 v2"
            local.get $"#local23 v1"
            local.get $"#local24 v2"
            call $kotlin.wasm.internal.wasm_u32_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      else
        i32.const 3
        local.get $value
        local.set $tmp6
        i32.const 10000
        local.set $tmp7
        block (result i32) ;; label = @3
          nop
          local.get $tmp6
          local.set $"#local27 this"
          local.get $tmp7
          local.set $"#local28 other"
          local.get $"#local27 this"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local29 tmp0"
          local.get $"#local28 other"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local30 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local29 tmp0"
            local.set $"#local31 v1"
            local.get $"#local30 tmp1"
            local.set $"#local32 v2"
            local.get $"#local31 v1"
            local.get $"#local32 v2"
            call $kotlin.wasm.internal.wasm_u32_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        local.get $value
        local.set $tmp8
        i32.const 1000
        local.set $tmp9
        block (result i32) ;; label = @3
          nop
          local.get $tmp8
          local.set $"#local35 this"
          local.get $tmp9
          local.set $"#local36 other"
          local.get $"#local35 this"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local37 tmp0"
          local.get $"#local36 other"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local38 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local37 tmp0"
            local.set $"#local39 v1"
            local.get $"#local38 tmp1"
            local.set $"#local40 v2"
            local.get $"#local39 v1"
            local.get $"#local40 v2"
            call $kotlin.wasm.internal.wasm_u32_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      end
      unreachable
    else
      local.get $value
      local.set $tmp10
      i32.const 10000000
      local.set $tmp11
      block (result i32) ;; label = @2
        nop
        local.get $tmp10
        local.set $"#local43 this"
        local.get $tmp11
        local.set $"#local44 other"
        local.get $"#local43 this"
        call $kotlin.<UInt__<get-data>-impl>
        local.set $"#local45 tmp0"
        local.get $"#local44 other"
        call $kotlin.<UInt__<get-data>-impl>
        local.set $"#local46 tmp1"
        block (result i32) ;; label = @3
          nop
          local.get $"#local45 tmp0"
          local.set $"#local47 v1"
          local.get $"#local46 tmp1"
          local.set $"#local48 v2"
          local.get $"#local47 v1"
          local.get $"#local48 v2"
          call $kotlin.wasm.internal.wasm_u32_compareTo
          br 0 (;@3;)
        end
        br 0 (;@2;)
      end
      i32.const 0
      i32.lt_s
      if ;; label = @2
        i32.const 6
        local.get $value
        local.set $tmp12
        i32.const 1000000
        local.set $tmp13
        block (result i32) ;; label = @3
          nop
          local.get $tmp12
          local.set $"#local51 this"
          local.get $tmp13
          local.set $"#local52 other"
          local.get $"#local51 this"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local53 tmp0"
          local.get $"#local52 other"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local54 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local53 tmp0"
            local.set $"#local55 v1"
            local.get $"#local54 tmp1"
            local.set $"#local56 v2"
            local.get $"#local55 v1"
            local.get $"#local56 v2"
            call $kotlin.wasm.internal.wasm_u32_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      else
        i32.const 8
        local.get $value
        local.set $tmp14
        i32.const 1000000000
        local.set $tmp15
        block (result i32) ;; label = @3
          nop
          local.get $tmp14
          local.set $"#local59 this"
          local.get $tmp15
          local.set $"#local60 other"
          local.get $"#local59 this"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local61 tmp0"
          local.get $"#local60 other"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local62 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local61 tmp0"
            local.set $"#local63 v1"
            local.get $"#local62 tmp1"
            local.set $"#local64 v2"
            local.get $"#local63 v1"
            local.get $"#local64 v2"
            call $kotlin.wasm.internal.wasm_u32_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        local.get $value
        local.set $tmp16
        i32.const 100000000
        local.set $tmp17
        block (result i32) ;; label = @3
          nop
          local.get $tmp16
          local.set $"#local67 this"
          local.get $tmp17
          local.set $"#local68 other"
          local.get $"#local67 this"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local69 tmp0"
          local.get $"#local68 other"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local70 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local69 tmp0"
            local.set $"#local71 v1"
            local.get $"#local70 tmp1"
            local.set $"#local72 v2"
            local.get $"#local71 v1"
            local.get $"#local72 v2"
            call $kotlin.wasm.internal.wasm_u32_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      end
      unreachable
    end
    unreachable
  )
  (func $kotlin.wasm.internal.utoaDecSimple (;133;) (type $"#type224 ") (param $buffer (ref null $kotlin.wasm.internal.WasmCharArray)) (param $numInput i32) (param $offsetInput i32)
    (local $num i32) (local $offset i32) (local $t i32) (local $tmp0 i32) (local $tmp1 i32) (local $this i32) (local $other i32) (local $r i32) (local $tmp2 i32) (local $tmp3 i32) (local $"#local13 this" (@name "this") i32) (local $"#local14 other" (@name "other") i32) (local $<unary> i32) (local $tmp4 i32) (local $"#local17 this" (@name "this") i32) (local $tmp5 i32) (local $tmp6 i32) (local $"#local20 this" (@name "this") i32) (local $"#local21 other" (@name "other") i32) (local $"#local22 tmp0" (@name "tmp0") i32) (local $"#local23 tmp1" (@name "tmp1") i32) (local $v1 i32) (local $v2 i32)
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    local.get $numInput
    local.set $num
    local.get $offsetInput
    local.set $offset
    loop ;; label = @1
      block ;; label = @2
        block ;; label = @3
          local.get $num
          local.set $tmp0
          i32.const 10
          local.set $tmp1
          block (result i32) ;; label = @4
            nop
            local.get $tmp0
            local.set $this
            local.get $tmp1
            local.set $other
            local.get $this
            local.get $other
            i32.div_u
            br 0 (;@4;)
          end
          local.set $t
          local.get $num
          local.set $tmp2
          i32.const 10
          local.set $tmp3
          block (result i32) ;; label = @4
            nop
            local.get $tmp2
            local.set $"#local13 this"
            local.get $tmp3
            local.set $"#local14 other"
            local.get $"#local13 this"
            local.get $"#local14 other"
            i32.rem_u
            br 0 (;@4;)
          end
          local.set $r
          local.get $t
          local.set $num
          local.get $offset
          local.tee $<unary>
          call $kotlin.Int__dec-impl
          local.set $offset
          local.get $buffer
          local.get $offset
          local.get $r
          local.set $tmp4
          block (result i32) ;; label = @4
            nop
            local.get $tmp4
            local.tee $"#local17 this"
            call $kotlin.<UInt__<get-data>-impl>
            br 0 (;@4;)
          end
          call $kotlin.wasm.internal.digitToChar
          array.set $kotlin.wasm.internal.WasmCharArray
        end
        local.get $num
        local.set $tmp5
        i32.const 0
        local.set $tmp6
        block (result i32) ;; label = @3
          nop
          local.get $tmp5
          local.set $"#local20 this"
          local.get $tmp6
          local.set $"#local21 other"
          local.get $"#local20 this"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local22 tmp0"
          local.get $"#local21 other"
          call $kotlin.<UInt__<get-data>-impl>
          local.set $"#local23 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local22 tmp0"
            local.set $v1
            local.get $"#local23 tmp1"
            local.set $v2
            local.get $v1
            local.get $v2
            call $kotlin.wasm.internal.wasm_u32_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.gt_s
        br_if 1 (;@1;)
      end
    end
    nop
  )
  (func $kotlin.wasm.internal.decimalCount64High (;134;) (type 5) (param $value i64) (result i32)
    (local $tmp0 i64) (local $tmp1 i64) (local $this i64) (local $other i64) (local $"#local5 tmp0" (@name "tmp0") i64) (local $"#local6 tmp1" (@name "tmp1") i64) (local $v1 i64) (local $v2 i64) (local $tmp2 i64) (local $tmp3 i64) (local $"#local11 this" (@name "this") i64) (local $"#local12 other" (@name "other") i64) (local $"#local13 tmp0" (@name "tmp0") i64) (local $"#local14 tmp1" (@name "tmp1") i64) (local $"#local15 v1" (@name "v1") i64) (local $"#local16 v2" (@name "v2") i64) (local $tmp4 i64) (local $tmp5 i64) (local $"#local19 this" (@name "this") i64) (local $"#local20 other" (@name "other") i64) (local $"#local21 tmp0" (@name "tmp0") i64) (local $"#local22 tmp1" (@name "tmp1") i64) (local $"#local23 v1" (@name "v1") i64) (local $"#local24 v2" (@name "v2") i64) (local $tmp6 i64) (local $tmp7 i64) (local $"#local27 this" (@name "this") i64) (local $"#local28 other" (@name "other") i64) (local $"#local29 tmp0" (@name "tmp0") i64) (local $"#local30 tmp1" (@name "tmp1") i64) (local $"#local31 v1" (@name "v1") i64) (local $"#local32 v2" (@name "v2") i64) (local $tmp8 i64) (local $tmp9 i64) (local $"#local35 this" (@name "this") i64) (local $"#local36 other" (@name "other") i64) (local $"#local37 tmp0" (@name "tmp0") i64) (local $"#local38 tmp1" (@name "tmp1") i64) (local $"#local39 v1" (@name "v1") i64) (local $"#local40 v2" (@name "v2") i64) (local $tmp10 i64) (local $tmp11 i64) (local $"#local43 this" (@name "this") i64) (local $"#local44 other" (@name "other") i64) (local $"#local45 tmp0" (@name "tmp0") i64) (local $"#local46 tmp1" (@name "tmp1") i64) (local $"#local47 v1" (@name "v1") i64) (local $"#local48 v2" (@name "v2") i64) (local $tmp12 i64) (local $tmp13 i64) (local $"#local51 this" (@name "this") i64) (local $"#local52 other" (@name "other") i64) (local $"#local53 tmp0" (@name "tmp0") i64) (local $"#local54 tmp1" (@name "tmp1") i64) (local $"#local55 v1" (@name "v1") i64) (local $"#local56 v2" (@name "v2") i64) (local $tmp14 i64) (local $tmp15 i64) (local $"#local59 this" (@name "this") i64) (local $"#local60 other" (@name "other") i64) (local $"#local61 tmp0" (@name "tmp0") i64) (local $"#local62 tmp1" (@name "tmp1") i64) (local $"#local63 v1" (@name "v1") i64) (local $"#local64 v2" (@name "v2") i64) (local $tmp16 i64) (local $tmp17 i64) (local $"#local67 this" (@name "this") i64) (local $"#local68 other" (@name "other") i64) (local $"#local69 tmp0" (@name "tmp0") i64) (local $"#local70 tmp1" (@name "tmp1") i64) (local $"#local71 v1" (@name "v1") i64) (local $"#local72 v2" (@name "v2") i64) (local $tmp18 i64) (local $tmp19 i64) (local $"#local75 this" (@name "this") i64) (local $"#local76 other" (@name "other") i64) (local $"#local77 tmp0" (@name "tmp0") i64) (local $"#local78 tmp1" (@name "tmp1") i64) (local $"#local79 v1" (@name "v1") i64) (local $"#local80 v2" (@name "v2") i64)
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    local.get $value
    local.set $tmp0
    i64.const 1000000000000000
    local.set $tmp1
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.set $this
      local.get $tmp1
      local.set $other
      local.get $this
      call $kotlin.<ULong__<get-data>-impl>
      local.set $"#local5 tmp0"
      local.get $other
      call $kotlin.<ULong__<get-data>-impl>
      local.set $"#local6 tmp1"
      block (result i32) ;; label = @2
        nop
        local.get $"#local5 tmp0"
        local.set $v1
        local.get $"#local6 tmp1"
        local.set $v2
        local.get $v1
        local.get $v2
        call $kotlin.wasm.internal.wasm_u64_compareTo
        br 0 (;@2;)
      end
      br 0 (;@1;)
    end
    i32.const 0
    i32.lt_s
    if ;; label = @1
      local.get $value
      local.set $tmp2
      i64.const 1000000000000
      local.set $tmp3
      block (result i32) ;; label = @2
        nop
        local.get $tmp2
        local.set $"#local11 this"
        local.get $tmp3
        local.set $"#local12 other"
        local.get $"#local11 this"
        call $kotlin.<ULong__<get-data>-impl>
        local.set $"#local13 tmp0"
        local.get $"#local12 other"
        call $kotlin.<ULong__<get-data>-impl>
        local.set $"#local14 tmp1"
        block (result i32) ;; label = @3
          nop
          local.get $"#local13 tmp0"
          local.set $"#local15 v1"
          local.get $"#local14 tmp1"
          local.set $"#local16 v2"
          local.get $"#local15 v1"
          local.get $"#local16 v2"
          call $kotlin.wasm.internal.wasm_u64_compareTo
          br 0 (;@3;)
        end
        br 0 (;@2;)
      end
      i32.const 0
      i32.lt_s
      if ;; label = @2
        i32.const 10
        local.get $value
        local.set $tmp4
        i64.const 100000000000
        local.set $tmp5
        block (result i32) ;; label = @3
          nop
          local.get $tmp4
          local.set $"#local19 this"
          local.get $tmp5
          local.set $"#local20 other"
          local.get $"#local19 this"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local21 tmp0"
          local.get $"#local20 other"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local22 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local21 tmp0"
            local.set $"#local23 v1"
            local.get $"#local22 tmp1"
            local.set $"#local24 v2"
            local.get $"#local23 v1"
            local.get $"#local24 v2"
            call $kotlin.wasm.internal.wasm_u64_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        local.get $value
        local.set $tmp6
        i64.const 10000000000
        local.set $tmp7
        block (result i32) ;; label = @3
          nop
          local.get $tmp6
          local.set $"#local27 this"
          local.get $tmp7
          local.set $"#local28 other"
          local.get $"#local27 this"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local29 tmp0"
          local.get $"#local28 other"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local30 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local29 tmp0"
            local.set $"#local31 v1"
            local.get $"#local30 tmp1"
            local.set $"#local32 v2"
            local.get $"#local31 v1"
            local.get $"#local32 v2"
            call $kotlin.wasm.internal.wasm_u64_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      else
        i32.const 13
        local.get $value
        local.set $tmp8
        i64.const 100000000000000
        local.set $tmp9
        block (result i32) ;; label = @3
          nop
          local.get $tmp8
          local.set $"#local35 this"
          local.get $tmp9
          local.set $"#local36 other"
          local.get $"#local35 this"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local37 tmp0"
          local.get $"#local36 other"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local38 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local37 tmp0"
            local.set $"#local39 v1"
            local.get $"#local38 tmp1"
            local.set $"#local40 v2"
            local.get $"#local39 v1"
            local.get $"#local40 v2"
            call $kotlin.wasm.internal.wasm_u64_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        local.get $value
        local.set $tmp10
        i64.const 10000000000000
        local.set $tmp11
        block (result i32) ;; label = @3
          nop
          local.get $tmp10
          local.set $"#local43 this"
          local.get $tmp11
          local.set $"#local44 other"
          local.get $"#local43 this"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local45 tmp0"
          local.get $"#local44 other"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local46 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local45 tmp0"
            local.set $"#local47 v1"
            local.get $"#local46 tmp1"
            local.set $"#local48 v2"
            local.get $"#local47 v1"
            local.get $"#local48 v2"
            call $kotlin.wasm.internal.wasm_u64_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      end
      unreachable
    else
      local.get $value
      local.set $tmp12
      i64.const 100000000000000000
      local.set $tmp13
      block (result i32) ;; label = @2
        nop
        local.get $tmp12
        local.set $"#local51 this"
        local.get $tmp13
        local.set $"#local52 other"
        local.get $"#local51 this"
        call $kotlin.<ULong__<get-data>-impl>
        local.set $"#local53 tmp0"
        local.get $"#local52 other"
        call $kotlin.<ULong__<get-data>-impl>
        local.set $"#local54 tmp1"
        block (result i32) ;; label = @3
          nop
          local.get $"#local53 tmp0"
          local.set $"#local55 v1"
          local.get $"#local54 tmp1"
          local.set $"#local56 v2"
          local.get $"#local55 v1"
          local.get $"#local56 v2"
          call $kotlin.wasm.internal.wasm_u64_compareTo
          br 0 (;@3;)
        end
        br 0 (;@2;)
      end
      i32.const 0
      i32.lt_s
      if ;; label = @2
        i32.const 16
        local.get $value
        local.set $tmp14
        i64.const 10000000000000000
        local.set $tmp15
        block (result i32) ;; label = @3
          nop
          local.get $tmp14
          local.set $"#local59 this"
          local.get $tmp15
          local.set $"#local60 other"
          local.get $"#local59 this"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local61 tmp0"
          local.get $"#local60 other"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local62 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local61 tmp0"
            local.set $"#local63 v1"
            local.get $"#local62 tmp1"
            local.set $"#local64 v2"
            local.get $"#local63 v1"
            local.get $"#local64 v2"
            call $kotlin.wasm.internal.wasm_u64_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      else
        i32.const 18
        local.get $value
        local.set $tmp16
        i64.const -8446744073709551616
        local.set $tmp17
        block (result i32) ;; label = @3
          nop
          local.get $tmp16
          local.set $"#local67 this"
          local.get $tmp17
          local.set $"#local68 other"
          local.get $"#local67 this"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local69 tmp0"
          local.get $"#local68 other"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local70 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local69 tmp0"
            local.set $"#local71 v1"
            local.get $"#local70 tmp1"
            local.set $"#local72 v2"
            local.get $"#local71 v1"
            local.get $"#local72 v2"
            call $kotlin.wasm.internal.wasm_u64_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        local.get $value
        local.set $tmp18
        i64.const 1000000000000000000
        local.set $tmp19
        block (result i32) ;; label = @3
          nop
          local.get $tmp18
          local.set $"#local75 this"
          local.get $tmp19
          local.set $"#local76 other"
          local.get $"#local75 this"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local77 tmp0"
          local.get $"#local76 other"
          call $kotlin.<ULong__<get-data>-impl>
          local.set $"#local78 tmp1"
          block (result i32) ;; label = @4
            nop
            local.get $"#local77 tmp0"
            local.set $"#local79 v1"
            local.get $"#local78 tmp1"
            local.set $"#local80 v2"
            local.get $"#local79 v1"
            local.get $"#local80 v2"
            call $kotlin.wasm.internal.wasm_u64_compareTo
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.ge_s
        i32.add
        return
      end
      unreachable
    end
    unreachable
  )
  (func $kotlin.wasm.internal.utoaDecSimple64 (;135;) (type $"#type225 ") (param $buffer (ref null $kotlin.wasm.internal.WasmCharArray)) (param $numInput i64) (param $offsetInput i32)
    (local $num i64) (local $offset i32) (local $t i64) (local $tmp0 i64) (local $tmp1 i32) (local $this i64) (local $other i32) (local $"#local10 tmp1" (@name "tmp1") i64) (local $tmp2 i64) (local $"#local12 tmp0" (@name "tmp0") i32) (local $"#local13 this" (@name "this") i32) (local $"#local14 this" (@name "this") i64) (local $"#local15 other" (@name "other") i64) (local $r i64) (local $"#local17 tmp2" (@name "tmp2") i64) (local $tmp3 i32) (local $"#local19 this" (@name "this") i64) (local $"#local20 other" (@name "other") i32) (local $"#local21 tmp1" (@name "tmp1") i64) (local $"#local22 tmp2" (@name "tmp2") i64) (local $"#local23 tmp0" (@name "tmp0") i32) (local $"#local24 this" (@name "this") i32) (local $"#local25 this" (@name "this") i64) (local $"#local26 other" (@name "other") i64) (local $<unary> i32) (local $tmp4 i64) (local $"#local29 this" (@name "this") i64) (local $tmp5 i64) (local $tmp6 i32) (local $"#local32 this" (@name "this") i64) (local $"#local33 other" (@name "other") i32) (local $"#local34 tmp1" (@name "tmp1") i64) (local $"#local35 tmp2" (@name "tmp2") i64) (local $"#local36 tmp0" (@name "tmp0") i32) (local $"#local37 this" (@name "this") i32) (local $"#local38 this" (@name "this") i64) (local $"#local39 other" (@name "other") i64) (local $"#local40 tmp0" (@name "tmp0") i64) (local $"#local41 tmp1" (@name "tmp1") i64) (local $v1 i64) (local $v2 i64)
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    local.get $numInput
    local.set $num
    local.get $offsetInput
    local.set $offset
    loop ;; label = @1
      block ;; label = @2
        block ;; label = @3
          local.get $num
          local.set $tmp0
          i32.const 10
          local.set $tmp1
          block (result i64) ;; label = @4
            nop
            local.get $tmp0
            local.set $this
            local.get $tmp1
            local.set $other
            local.get $this
            local.set $"#local10 tmp1"
            local.get $other
            local.set $"#local12 tmp0"
            block (result i64) ;; label = @5
              nop
              local.get $"#local12 tmp0"
              local.tee $"#local13 this"
              call $kotlin.<UInt__<get-data>-impl>
              i64.extend_i32_u
              br 0 (;@5;)
            end
            local.set $tmp2
            block (result i64) ;; label = @5
              nop
              local.get $"#local10 tmp1"
              local.set $"#local14 this"
              local.get $tmp2
              local.set $"#local15 other"
              local.get $"#local14 this"
              local.get $"#local15 other"
              i64.div_u
              br 0 (;@5;)
            end
            br 0 (;@4;)
          end
          local.set $t
          local.get $num
          local.set $"#local17 tmp2"
          i32.const 10
          local.set $tmp3
          block (result i64) ;; label = @4
            nop
            local.get $"#local17 tmp2"
            local.set $"#local19 this"
            local.get $tmp3
            local.set $"#local20 other"
            local.get $"#local19 this"
            local.set $"#local21 tmp1"
            local.get $"#local20 other"
            local.set $"#local23 tmp0"
            block (result i64) ;; label = @5
              nop
              local.get $"#local23 tmp0"
              local.tee $"#local24 this"
              call $kotlin.<UInt__<get-data>-impl>
              i64.extend_i32_u
              br 0 (;@5;)
            end
            local.set $"#local22 tmp2"
            block (result i64) ;; label = @5
              nop
              local.get $"#local21 tmp1"
              local.set $"#local25 this"
              local.get $"#local22 tmp2"
              local.set $"#local26 other"
              local.get $"#local25 this"
              local.get $"#local26 other"
              i64.rem_u
              br 0 (;@5;)
            end
            br 0 (;@4;)
          end
          local.set $r
          local.get $t
          local.set $num
          local.get $offset
          local.tee $<unary>
          call $kotlin.Int__dec-impl
          local.set $offset
          local.get $buffer
          local.get $offset
          local.get $r
          local.set $tmp4
          block (result i32) ;; label = @4
            nop
            local.get $tmp4
            local.tee $"#local29 this"
            call $kotlin.<ULong__<get-data>-impl>
            call $kotlin.Long__toInt-impl
            br 0 (;@4;)
          end
          call $kotlin.wasm.internal.digitToChar
          array.set $kotlin.wasm.internal.WasmCharArray
        end
        local.get $num
        local.set $tmp5
        i32.const 0
        local.set $tmp6
        block (result i32) ;; label = @3
          nop
          local.get $tmp5
          local.set $"#local32 this"
          local.get $tmp6
          local.set $"#local33 other"
          local.get $"#local32 this"
          local.set $"#local34 tmp1"
          local.get $"#local33 other"
          local.set $"#local36 tmp0"
          block (result i64) ;; label = @4
            nop
            local.get $"#local36 tmp0"
            local.tee $"#local37 this"
            call $kotlin.<UInt__<get-data>-impl>
            i64.extend_i32_u
            br 0 (;@4;)
          end
          local.set $"#local35 tmp2"
          block (result i32) ;; label = @4
            nop
            local.get $"#local34 tmp1"
            local.set $"#local38 this"
            local.get $"#local35 tmp2"
            local.set $"#local39 other"
            local.get $"#local38 this"
            call $kotlin.<ULong__<get-data>-impl>
            local.set $"#local40 tmp0"
            local.get $"#local39 other"
            call $kotlin.<ULong__<get-data>-impl>
            local.set $"#local41 tmp1"
            block (result i32) ;; label = @5
              nop
              local.get $"#local40 tmp0"
              local.set $v1
              local.get $"#local41 tmp1"
              local.set $v2
              local.get $v1
              local.get $v2
              call $kotlin.wasm.internal.wasm_u64_compareTo
              br 0 (;@5;)
            end
            br 0 (;@4;)
          end
          br 0 (;@3;)
        end
        i32.const 0
        i32.gt_s
        br_if 1 (;@1;)
      end
    end
    nop
  )
  (func $kotlin.wasm.internal.CharCodes_initEntries (;136;) (type 6)
    global.get $kotlin.wasm.internal.CharCodes_entriesInitialized
    if ;; label = @1
      return
    else
    end
    i32.const 1
    global.set $kotlin.wasm.internal.CharCodes_entriesInitialized
    ref.null none
    i32.const 51
    i32.const 1044
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 0
    i32.const 43
    call $kotlin.wasm.internal.CharCodes.<init>
    global.set $kotlin.wasm.internal.CharCodes_PLUS_instance
    ref.null none
    i32.const 52
    i32.const 1052
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 1
    i32.const 45
    call $kotlin.wasm.internal.CharCodes.<init>
    global.set $kotlin.wasm.internal.CharCodes_MINUS_instance
    ref.null none
    i32.const 53
    i32.const 1062
    i32.const 3
    call $kotlin.stringLiteral
    i32.const 2
    i32.const 46
    call $kotlin.wasm.internal.CharCodes.<init>
    global.set $kotlin.wasm.internal.CharCodes_DOT_instance
    ref.null none
    i32.const 54
    i32.const 1068
    i32.const 2
    call $kotlin.stringLiteral
    i32.const 3
    i32.const 48
    call $kotlin.wasm.internal.CharCodes.<init>
    global.set $kotlin.wasm.internal.CharCodes__0_instance
    ref.null none
    i32.const 55
    i32.const 1072
    i32.const 1
    call $kotlin.stringLiteral
    i32.const 4
    i32.const 101
    call $kotlin.wasm.internal.CharCodes.<init>
    global.set $kotlin.wasm.internal.CharCodes_e_instance
    nop
  )
  (func $kotlin.wasm.internal.CharCodes.<init> (;137;) (type $"#type226 ") (param $<this> (ref null $kotlin.wasm.internal.CharCodes)) (param $name (ref null $kotlin.String)) (param $ordinal i32) (param $code i32) (result (ref null $kotlin.wasm.internal.CharCodes))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.wasm.internal.CharCodes.vtable
      global.get $kotlin.wasm.internal.CharCodes.classITable
      i32.const 1316
      i32.const 0
      ref.null $kotlin.String
      i32.const 0
      i32.const 0
      struct.new $kotlin.wasm.internal.CharCodes
      local.set $<this>
    end
    local.get $<this>
    local.get $name
    local.get $ordinal
    call $kotlin.Enum.<init>
    drop
    local.get $<this>
    local.get $code
    struct.set $kotlin.wasm.internal.CharCodes $code
    nop
    local.get $<this>
    return
  )
  (func $kotlin.wasm.internal.digitToChar (;138;) (type 2) (param $input i32) (result i32)
    call $"kotlin.wasm.internal.<init properties Number2String.kt>"
    call $kotlin.wasm.internal.CharCodes__0_getInstance
    struct.get $kotlin.wasm.internal.CharCodes $code
    local.get $input
    i32.add
    call $kotlin.Int__toChar-impl
    return
  )
  (func $kotlin.wasm.internal.CharCodes__0_getInstance (;139;) (type $"#type227 ") (result (ref null $kotlin.wasm.internal.CharCodes))
    call $kotlin.wasm.internal.CharCodes_initEntries
    global.get $kotlin.wasm.internal.CharCodes__0_instance
    return
  )
  (func $"kotlin.wasm.internal.<init properties Number2String.kt>" (;140;) (type 6)
    (local $tmp0 (ref null $kotlin.ShortArray)) (local $elements (ref null $kotlin.ShortArray)) (local $tmp87 (ref null $kotlin.LongArray)) (local $"#local3 tmp0" (@name "tmp0") i64) (local $this i64) (local $tmp1 i64) (local $"#local6 this" (@name "this") i64) (local $tmp2 i64) (local $"#local8 this" (@name "this") i64) (local $tmp3 i64) (local $"#local10 this" (@name "this") i64) (local $tmp4 i64) (local $"#local12 this" (@name "this") i64) (local $tmp5 i64) (local $"#local14 this" (@name "this") i64) (local $tmp6 i64) (local $"#local16 this" (@name "this") i64) (local $tmp7 i64) (local $"#local18 this" (@name "this") i64) (local $tmp8 i64) (local $"#local20 this" (@name "this") i64) (local $tmp9 i64) (local $"#local22 this" (@name "this") i64) (local $tmp10 i64) (local $"#local24 this" (@name "this") i64) (local $tmp11 i64) (local $"#local26 this" (@name "this") i64) (local $tmp12 i64) (local $"#local28 this" (@name "this") i64) (local $tmp13 i64) (local $"#local30 this" (@name "this") i64) (local $tmp14 i64) (local $"#local32 this" (@name "this") i64) (local $tmp15 i64) (local $"#local34 this" (@name "this") i64) (local $tmp16 i64) (local $"#local36 this" (@name "this") i64) (local $tmp17 i64) (local $"#local38 this" (@name "this") i64) (local $tmp18 i64) (local $"#local40 this" (@name "this") i64) (local $tmp19 i64) (local $"#local42 this" (@name "this") i64) (local $tmp20 i64) (local $"#local44 this" (@name "this") i64) (local $tmp21 i64) (local $"#local46 this" (@name "this") i64) (local $tmp22 i64) (local $"#local48 this" (@name "this") i64) (local $tmp23 i64) (local $"#local50 this" (@name "this") i64) (local $tmp24 i64) (local $"#local52 this" (@name "this") i64) (local $tmp25 i64) (local $"#local54 this" (@name "this") i64) (local $tmp26 i64) (local $"#local56 this" (@name "this") i64) (local $tmp27 i64) (local $"#local58 this" (@name "this") i64) (local $tmp28 i64) (local $"#local60 this" (@name "this") i64) (local $tmp29 i64) (local $"#local62 this" (@name "this") i64) (local $tmp30 i64) (local $"#local64 this" (@name "this") i64) (local $tmp31 i64) (local $"#local66 this" (@name "this") i64) (local $tmp32 i64) (local $"#local68 this" (@name "this") i64) (local $tmp33 i64) (local $"#local70 this" (@name "this") i64) (local $tmp34 i64) (local $"#local72 this" (@name "this") i64) (local $tmp35 i64) (local $"#local74 this" (@name "this") i64) (local $tmp36 i64) (local $"#local76 this" (@name "this") i64) (local $tmp37 i64) (local $"#local78 this" (@name "this") i64) (local $tmp38 i64) (local $"#local80 this" (@name "this") i64) (local $tmp39 i64) (local $"#local82 this" (@name "this") i64) (local $tmp40 i64) (local $"#local84 this" (@name "this") i64) (local $tmp41 i64) (local $"#local86 this" (@name "this") i64) (local $tmp42 i64) (local $"#local88 this" (@name "this") i64) (local $tmp43 i64) (local $"#local90 this" (@name "this") i64) (local $tmp44 i64) (local $"#local92 this" (@name "this") i64) (local $tmp45 i64) (local $"#local94 this" (@name "this") i64) (local $tmp46 i64) (local $"#local96 this" (@name "this") i64) (local $tmp47 i64) (local $"#local98 this" (@name "this") i64) (local $tmp48 i64) (local $"#local100 this" (@name "this") i64) (local $tmp49 i64) (local $"#local102 this" (@name "this") i64) (local $tmp50 i64) (local $"#local104 this" (@name "this") i64) (local $tmp51 i64) (local $"#local106 this" (@name "this") i64) (local $tmp52 i64) (local $"#local108 this" (@name "this") i64) (local $tmp53 i64) (local $"#local110 this" (@name "this") i64) (local $tmp54 i64) (local $"#local112 this" (@name "this") i64) (local $tmp55 i64) (local $"#local114 this" (@name "this") i64) (local $tmp56 i64) (local $"#local116 this" (@name "this") i64) (local $tmp57 i64) (local $"#local118 this" (@name "this") i64) (local $tmp58 i64) (local $"#local120 this" (@name "this") i64) (local $tmp59 i64) (local $"#local122 this" (@name "this") i64) (local $tmp60 i64) (local $"#local124 this" (@name "this") i64) (local $tmp61 i64) (local $"#local126 this" (@name "this") i64) (local $tmp62 i64) (local $"#local128 this" (@name "this") i64) (local $tmp63 i64) (local $"#local130 this" (@name "this") i64) (local $tmp64 i64) (local $"#local132 this" (@name "this") i64) (local $tmp65 i64) (local $"#local134 this" (@name "this") i64) (local $tmp66 i64) (local $"#local136 this" (@name "this") i64) (local $tmp67 i64) (local $"#local138 this" (@name "this") i64) (local $tmp68 i64) (local $"#local140 this" (@name "this") i64) (local $tmp69 i64) (local $"#local142 this" (@name "this") i64) (local $tmp70 i64) (local $"#local144 this" (@name "this") i64) (local $tmp71 i64) (local $"#local146 this" (@name "this") i64) (local $tmp72 i64) (local $"#local148 this" (@name "this") i64) (local $tmp73 i64) (local $"#local150 this" (@name "this") i64) (local $tmp74 i64) (local $"#local152 this" (@name "this") i64) (local $tmp75 i64) (local $"#local154 this" (@name "this") i64) (local $tmp76 i64) (local $"#local156 this" (@name "this") i64) (local $tmp77 i64) (local $"#local158 this" (@name "this") i64) (local $tmp78 i64) (local $"#local160 this" (@name "this") i64) (local $tmp79 i64) (local $"#local162 this" (@name "this") i64) (local $tmp80 i64) (local $"#local164 this" (@name "this") i64) (local $tmp81 i64) (local $"#local166 this" (@name "this") i64) (local $tmp82 i64) (local $"#local168 this" (@name "this") i64) (local $tmp83 i64) (local $"#local170 this" (@name "this") i64) (local $tmp84 i64) (local $"#local172 this" (@name "this") i64) (local $tmp85 i64) (local $"#local174 this" (@name "this") i64) (local $tmp86 i64) (local $"#local176 this" (@name "this") i64) (local $"#local177 elements" (@name "elements") (ref null $kotlin.LongArray))
    global.get $"kotlin.wasm.internal.properties initialized Number2String.kt"
    if ;; label = @1
    else
      i32.const 1
      global.set $"kotlin.wasm.internal.properties initialized Number2String.kt"
      i32.const 0
      global.set $kotlin.wasm.internal._K
      i32.const 0
      global.set $kotlin.wasm.internal._exp
      i64.const 0
      global.set $kotlin.wasm.internal._frc_minus
      i64.const 0
      global.set $kotlin.wasm.internal._frc_plus
      i64.const 0
      global.set $kotlin.wasm.internal._frc_pow
      i32.const 0
      global.set $kotlin.wasm.internal._exp_pow
      global.get $kotlin.ShortArray.vtable
      ref.null struct
      i32.const 984
      i32.const 0
      i32.const 0
      i32.const 87
      array.new_data $kotlin.wasm.internal.WasmShortArray 1
      struct.new $kotlin.ShortArray
      local.set $tmp0
      block (result (ref null $kotlin.ShortArray)) ;; label = @2
        nop
        local.get $tmp0
        local.tee $elements
        br 0 (;@2;)
      end
      global.set $kotlin.wasm.internal.EXP_POWERS
      global.get $kotlin.LongArray.vtable
      ref.null struct
      i32.const 952
      i32.const 0
      i64.const -391859759250406776
      local.set $"#local3 tmp0"
      block (result i64) ;; label = @2
        nop
        local.get $"#local3 tmp0"
        local.tee $this
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4994806998408183946
      local.set $tmp1
      block (result i64) ;; label = @2
        nop
        local.get $tmp1
        local.tee $"#local6 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8424269937281487754
      local.set $tmp2
      block (result i64) ;; label = @2
        nop
        local.get $tmp2
        local.tee $"#local8 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3512093806901185046
      local.set $tmp3
      block (result i64) ;; label = @2
        nop
        local.get $tmp3
        local.tee $"#local10 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7319562523736982739
      local.set $tmp4
      block (result i64) ;; label = @2
        nop
        local.get $tmp4
        local.tee $"#local12 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1865951482774665761
      local.set $tmp5
      block (result i64) ;; label = @2
        nop
        local.get $tmp5
        local.tee $"#local14 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6093090917745768758
      local.set $tmp6
      block (result i64) ;; label = @2
        nop
        local.get $tmp6
        local.tee $"#local16 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -38366372719436721
      local.set $tmp7
      block (result i64) ;; label = @2
        nop
        local.get $tmp7
        local.tee $"#local18 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4731433901725329908
      local.set $tmp8
      block (result i64) ;; label = @2
        nop
        local.get $tmp8
        local.tee $"#local20 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8228041688891786180
      local.set $tmp9
      block (result i64) ;; label = @2
        nop
        local.get $tmp9
        local.tee $"#local22 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3219690930897053053
      local.set $tmp10
      block (result i64) ;; label = @2
        nop
        local.get $tmp10
        local.tee $"#local24 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7101705404292871755
      local.set $tmp11
      block (result i64) ;; label = @2
        nop
        local.get $tmp11
        local.tee $"#local26 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1541319077368263733
      local.set $tmp12
      block (result i64) ;; label = @2
        nop
        local.get $tmp12
        local.tee $"#local28 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5851220927660403859
      local.set $tmp13
      block (result i64) ;; label = @2
        nop
        local.get $tmp13
        local.tee $"#local30 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -9062348037703676329
      local.set $tmp14
      block (result i64) ;; label = @2
        nop
        local.get $tmp14
        local.tee $"#local32 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4462904269766699465
      local.set $tmp15
      block (result i64) ;; label = @2
        nop
        local.get $tmp15
        local.tee $"#local34 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8027971522334779313
      local.set $tmp16
      block (result i64) ;; label = @2
        nop
        local.get $tmp16
        local.tee $"#local36 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -2921563150702462265
      local.set $tmp17
      block (result i64) ;; label = @2
        nop
        local.get $tmp17
        local.tee $"#local38 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6879582898840692748
      local.set $tmp18
      block (result i64) ;; label = @2
        nop
        local.get $tmp18
        local.tee $"#local40 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1210330751515841307
      local.set $tmp19
      block (result i64) ;; label = @2
        nop
        local.get $tmp19
        local.tee $"#local42 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5604615407819967858
      local.set $tmp20
      block (result i64) ;; label = @2
        nop
        local.get $tmp20
        local.tee $"#local44 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8878612607581929669
      local.set $tmp21
      block (result i64) ;; label = @2
        nop
        local.get $tmp21
        local.tee $"#local46 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4189117143640191558
      local.set $tmp22
      block (result i64) ;; label = @2
        nop
        local.get $tmp22
        local.tee $"#local48 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7823984217374209642
      local.set $tmp23
      block (result i64) ;; label = @2
        nop
        local.get $tmp23
        local.tee $"#local50 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -2617598379430861436
      local.set $tmp24
      block (result i64) ;; label = @2
        nop
        local.get $tmp24
        local.tee $"#local52 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6653111496142234890
      local.set $tmp25
      block (result i64) ;; label = @2
        nop
        local.get $tmp25
        local.tee $"#local54 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -872862063775190746
      local.set $tmp26
      block (result i64) ;; label = @2
        nop
        local.get $tmp26
        local.tee $"#local56 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5353181642124984136
      local.set $tmp27
      block (result i64) ;; label = @2
        nop
        local.get $tmp27
        local.tee $"#local58 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8691279853972075893
      local.set $tmp28
      block (result i64) ;; label = @2
        nop
        local.get $tmp28
        local.tee $"#local60 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3909969587797413805
      local.set $tmp29
      block (result i64) ;; label = @2
        nop
        local.get $tmp29
        local.tee $"#local62 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7616003081050118571
      local.set $tmp30
      block (result i64) ;; label = @2
        nop
        local.get $tmp30
        local.tee $"#local64 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -2307682335666372931
      local.set $tmp31
      block (result i64) ;; label = @2
        nop
        local.get $tmp31
        local.tee $"#local66 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6422206049907525489
      local.set $tmp32
      block (result i64) ;; label = @2
        nop
        local.get $tmp32
        local.tee $"#local68 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -528786136287117932
      local.set $tmp33
      block (result i64) ;; label = @2
        nop
        local.get $tmp33
        local.tee $"#local70 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5096825099203863601
      local.set $tmp34
      block (result i64) ;; label = @2
        nop
        local.get $tmp34
        local.tee $"#local72 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8500279345513818773
      local.set $tmp35
      block (result i64) ;; label = @2
        nop
        local.get $tmp35
        local.tee $"#local74 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3625356651333078602
      local.set $tmp36
      block (result i64) ;; label = @2
        nop
        local.get $tmp36
        local.tee $"#local76 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7403949918844649556
      local.set $tmp37
      block (result i64) ;; label = @2
        nop
        local.get $tmp37
        local.tee $"#local78 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1991698500497491194
      local.set $tmp38
      block (result i64) ;; label = @2
        nop
        local.get $tmp38
        local.tee $"#local80 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6186779746782440749
      local.set $tmp39
      block (result i64) ;; label = @2
        nop
        local.get $tmp39
        local.tee $"#local82 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -177973607073265138
      local.set $tmp40
      block (result i64) ;; label = @2
        nop
        local.get $tmp40
        local.tee $"#local84 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4835449396872013077
      local.set $tmp41
      block (result i64) ;; label = @2
        nop
        local.get $tmp41
        local.tee $"#local86 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8305539271883716404
      local.set $tmp42
      block (result i64) ;; label = @2
        nop
        local.get $tmp42
        local.tee $"#local88 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3335171328526686932
      local.set $tmp43
      block (result i64) ;; label = @2
        nop
        local.get $tmp43
        local.tee $"#local90 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7187745005283311616
      local.set $tmp44
      block (result i64) ;; label = @2
        nop
        local.get $tmp44
        local.tee $"#local92 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1669528073709551616
      local.set $tmp45
      block (result i64) ;; label = @2
        nop
        local.get $tmp45
        local.tee $"#local94 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5946744073709551616
      local.set $tmp46
      block (result i64) ;; label = @2
        nop
        local.get $tmp46
        local.tee $"#local96 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -9133518327554766460
      local.set $tmp47
      block (result i64) ;; label = @2
        nop
        local.get $tmp47
        local.tee $"#local98 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4568956265895094861
      local.set $tmp48
      block (result i64) ;; label = @2
        nop
        local.get $tmp48
        local.tee $"#local100 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8106986416796705680
      local.set $tmp49
      block (result i64) ;; label = @2
        nop
        local.get $tmp49
        local.tee $"#local102 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3039304518611664792
      local.set $tmp50
      block (result i64) ;; label = @2
        nop
        local.get $tmp50
        local.tee $"#local104 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6967307053960650171
      local.set $tmp51
      block (result i64) ;; label = @2
        nop
        local.get $tmp51
        local.tee $"#local106 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1341049929119499481
      local.set $tmp52
      block (result i64) ;; label = @2
        nop
        local.get $tmp52
        local.tee $"#local108 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5702008784649933400
      local.set $tmp53
      block (result i64) ;; label = @2
        nop
        local.get $tmp53
        local.tee $"#local110 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8951176327949752869
      local.set $tmp54
      block (result i64) ;; label = @2
        nop
        local.get $tmp54
        local.tee $"#local112 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4297245513042813542
      local.set $tmp55
      block (result i64) ;; label = @2
        nop
        local.get $tmp55
        local.tee $"#local114 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7904546130479028392
      local.set $tmp56
      block (result i64) ;; label = @2
        nop
        local.get $tmp56
        local.tee $"#local116 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -2737644984756826646
      local.set $tmp57
      block (result i64) ;; label = @2
        nop
        local.get $tmp57
        local.tee $"#local118 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6742553186979055798
      local.set $tmp58
      block (result i64) ;; label = @2
        nop
        local.get $tmp58
        local.tee $"#local120 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1006140569036166267
      local.set $tmp59
      block (result i64) ;; label = @2
        nop
        local.get $tmp59
        local.tee $"#local122 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5452481866653427593
      local.set $tmp60
      block (result i64) ;; label = @2
        nop
        local.get $tmp60
        local.tee $"#local124 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8765264286586255934
      local.set $tmp61
      block (result i64) ;; label = @2
        nop
        local.get $tmp61
        local.tee $"#local126 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4020214983419339459
      local.set $tmp62
      block (result i64) ;; label = @2
        nop
        local.get $tmp62
        local.tee $"#local128 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7698142301602209613
      local.set $tmp63
      block (result i64) ;; label = @2
        nop
        local.get $tmp63
        local.tee $"#local130 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -2430079312244744221
      local.set $tmp64
      block (result i64) ;; label = @2
        nop
        local.get $tmp64
        local.tee $"#local132 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6513398903789220827
      local.set $tmp65
      block (result i64) ;; label = @2
        nop
        local.get $tmp65
        local.tee $"#local134 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -664674077828931748
      local.set $tmp66
      block (result i64) ;; label = @2
        nop
        local.get $tmp66
        local.tee $"#local136 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5198069505264599346
      local.set $tmp67
      block (result i64) ;; label = @2
        nop
        local.get $tmp67
        local.tee $"#local138 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8575712306248138270
      local.set $tmp68
      block (result i64) ;; label = @2
        nop
        local.get $tmp68
        local.tee $"#local140 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3737760522056206171
      local.set $tmp69
      block (result i64) ;; label = @2
        nop
        local.get $tmp69
        local.tee $"#local142 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7487697328667536417
      local.set $tmp70
      block (result i64) ;; label = @2
        nop
        local.get $tmp70
        local.tee $"#local144 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -2116491865831296966
      local.set $tmp71
      block (result i64) ;; label = @2
        nop
        local.get $tmp71
        local.tee $"#local146 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6279758049420528746
      local.set $tmp72
      block (result i64) ;; label = @2
        nop
        local.get $tmp72
        local.tee $"#local148 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -316522074587315140
      local.set $tmp73
      block (result i64) ;; label = @2
        nop
        local.get $tmp73
        local.tee $"#local150 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4938676049251384304
      local.set $tmp74
      block (result i64) ;; label = @2
        nop
        local.get $tmp74
        local.tee $"#local152 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8382449121214030822
      local.set $tmp75
      block (result i64) ;; label = @2
        nop
        local.get $tmp75
        local.tee $"#local154 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3449775934753242068
      local.set $tmp76
      block (result i64) ;; label = @2
        nop
        local.get $tmp76
        local.tee $"#local156 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7273132090830278359
      local.set $tmp77
      block (result i64) ;; label = @2
        nop
        local.get $tmp77
        local.tee $"#local158 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1796764746270372707
      local.set $tmp78
      block (result i64) ;; label = @2
        nop
        local.get $tmp78
        local.tee $"#local160 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -6041542782089432023
      local.set $tmp79
      block (result i64) ;; label = @2
        nop
        local.get $tmp79
        local.tee $"#local162 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -9204148869281624187
      local.set $tmp80
      block (result i64) ;; label = @2
        nop
        local.get $tmp80
        local.tee $"#local164 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -4674203974643163859
      local.set $tmp81
      block (result i64) ;; label = @2
        nop
        local.get $tmp81
        local.tee $"#local166 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -8185402070463610993
      local.set $tmp82
      block (result i64) ;; label = @2
        nop
        local.get $tmp82
        local.tee $"#local168 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -3156152948152813503
      local.set $tmp83
      block (result i64) ;; label = @2
        nop
        local.get $tmp83
        local.tee $"#local170 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -7054365918152680535
      local.set $tmp84
      block (result i64) ;; label = @2
        nop
        local.get $tmp84
        local.tee $"#local172 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -1470777745987373095
      local.set $tmp85
      block (result i64) ;; label = @2
        nop
        local.get $tmp85
        local.tee $"#local174 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      i64.const -5798663540173640085
      local.set $tmp86
      block (result i64) ;; label = @2
        nop
        local.get $tmp86
        local.tee $"#local176 this"
        call $kotlin.<ULong__<get-data>-impl>
        br 0 (;@2;)
      end
      array.new_fixed $kotlin.wasm.internal.WasmLongArray 87
      struct.new $kotlin.LongArray
      local.set $tmp87
      block (result (ref null $kotlin.LongArray)) ;; label = @2
        nop
        local.get $tmp87
        local.tee $"#local177 elements"
        br 0 (;@2;)
      end
      global.set $kotlin.wasm.internal.FRC_POWERS
    end
    nop
  )
  (func $kotlin.wasm.internal.rangeCheck (;141;) (type 1) (param $index i32) (param $size i32)
    local.get $index
    i32.const 0
    i32.lt_s
    if (result i32) ;; label = @1
      i32.const 1
    else
      local.get $index
      local.get $size
      i32.ge_s
    end
    if ;; label = @1
      ref.null none
      call $kotlin.IndexOutOfBoundsException.<init>
      throw 0
    else
    end
    nop
  )
  (func $kotlin.wasm.internal.getPackageName (;142;) (type $"#type205 ") (param $typeInfoPtr i32) (result (ref null $kotlin.String))
    local.get $typeInfoPtr
    i32.const 0
    i32.const 4
    i32.const 8
    call $kotlin.wasm.internal.getString
    return
  )
  (func $kotlin.wasm.internal.getSimpleName (;143;) (type $"#type205 ") (param $typeInfoPtr i32) (result (ref null $kotlin.String))
    local.get $typeInfoPtr
    i32.const 12
    i32.const 16
    i32.const 20
    call $kotlin.wasm.internal.getString
    return
  )
  (func $kotlin.wasm.internal.getString (;144;) (type $"#type228 ") (param $typeInfoPtr i32) (param $lengthOffset i32) (param $idOffset i32) (param $ptrOffset i32) (result (ref null $kotlin.String))
    (local $length i32) (local $id i32) (local $ptr i32)
    local.get $typeInfoPtr
    local.get $lengthOffset
    i32.add
    i32.load align=1
    local.set $length
    local.get $typeInfoPtr
    local.get $idOffset
    i32.add
    i32.load align=1
    local.set $id
    local.get $typeInfoPtr
    local.get $ptrOffset
    i32.add
    i32.load align=1
    local.set $ptr
    local.get $id
    local.get $ptr
    local.get $length
    call $kotlin.stringLiteral
    return
  )
  (func $kotlin.wasm.internal.wasm_u64_compareTo (;145;) (type 7) (param $x i64) (param $y i64) (result i32)
    local.get $x
    local.get $y
    i64.ge_u
    local.get $x
    local.get $y
    i64.le_u
    i32.sub
    return
  )
  (func $kotlin.wasm.internal.wasm_u32_compareTo (;146;) (type 0) (param $x i32) (param $y i32) (result i32)
    local.get $x
    local.get $y
    i32.ge_u
    local.get $x
    local.get $y
    i32.le_u
    i32.sub
    return
  )
  (func $kotlin.collections.copyInto (;147;) (type $"#type229 ") (param $<this> (ref null $kotlin.CharArray)) (param $destination (ref null $kotlin.CharArray)) (param $destinationOffset i32) (param $startIndex i32) (param $endIndex i32) (result (ref null $kotlin.CharArray))
    (local $rangeSize i32) (local $tmp0 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp1 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp2 i32) (local $tmp3 i32) (local $tmp4 i32) (local $source (ref null $kotlin.wasm.internal.WasmCharArray)) (local $"#local12 destination" (@name "destination") (ref null $kotlin.wasm.internal.WasmCharArray)) (local $sourceIndex i32) (local $destinationIndex i32) (local $length i32)
    global.get $"#global1 kotlin.collections.Companion_instance"
    local.get $startIndex
    local.get $endIndex
    local.get $<this>
    call $kotlin.CharArray.<get-size>
    call $kotlin.collections.Companion.checkRangeIndexes
    local.get $endIndex
    local.get $startIndex
    i32.sub
    local.set $rangeSize
    global.get $"#global1 kotlin.collections.Companion_instance"
    local.get $destinationOffset
    local.get $destinationOffset
    local.get $rangeSize
    i32.add
    local.get $destination
    call $kotlin.CharArray.<get-size>
    call $kotlin.collections.Companion.checkRangeIndexes
    local.get $<this>
    struct.get $kotlin.CharArray $storage
    local.set $tmp0
    local.get $destination
    struct.get $kotlin.CharArray $storage
    local.set $tmp1
    local.get $startIndex
    local.set $tmp2
    local.get $destinationOffset
    local.set $tmp3
    local.get $rangeSize
    local.set $tmp4
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp0
      local.set $source
      local.get $tmp1
      local.set $"#local12 destination"
      local.get $tmp2
      local.set $sourceIndex
      local.get $tmp3
      local.set $destinationIndex
      local.get $tmp4
      local.set $length
      local.get $"#local12 destination"
      local.get $destinationIndex
      local.get $source
      local.get $sourceIndex
      local.get $length
      array.copy $kotlin.wasm.internal.WasmCharArray $kotlin.wasm.internal.WasmCharArray
      global.get $kotlin.Unit_instance
    end
    drop
    local.get $destination
    return
  )
  (func $kotlin.collections.copyOf (;148;) (type $"#type212 ") (param $<this> (ref null $kotlin.CharArray)) (param $newSize i32) (result (ref null $kotlin.CharArray))
    local.get $<this>
    local.get $newSize
    call $kotlin.collections.copyOfUninitializedElements
    return
  )
  (func $kotlin.collections.copyOfUninitializedElements (;149;) (type $"#type212 ") (param $<this> (ref null $kotlin.CharArray)) (param $newSize i32) (result (ref null $kotlin.CharArray))
    local.get $<this>
    i32.const 0
    local.get $newSize
    call $"#func150 kotlin.collections.copyOfUninitializedElements"
    return
  )
  (func $"#func150 kotlin.collections.copyOfUninitializedElements" (@name "kotlin.collections.copyOfUninitializedElements") (;150;) (type $"#type230 ") (param $<this> (ref null $kotlin.CharArray)) (param $fromIndex i32) (param $toIndex i32) (result (ref null $kotlin.CharArray))
    (local $newSize i32) (local $tmp (ref null $kotlin.text.StringBuilder)) (local $result (ref null $kotlin.CharArray))
    local.get $toIndex
    local.get $fromIndex
    i32.sub
    local.tee $newSize
    i32.const 0
    i32.lt_s
    if ;; label = @1
      ref.null none
      ref.null none
      call $"#func29 kotlin.text.StringBuilder.<init>"
      local.tee $tmp
      local.get $fromIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      i32.const 57
      i32.const 1092
      i32.const 3
      call $kotlin.stringLiteral
      call $"#func38 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $toIndex
      call $"#func39 kotlin.text.StringBuilder.append"
      drop
      local.get $tmp
      local.get $tmp
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
      call $kotlin.IllegalArgumentException.<init>
      throw 0
    else
    end
    ref.null none
    local.get $newSize
    call $kotlin.CharArray.<init>
    local.set $result
    local.get $<this>
    local.get $result
    i32.const 0
    local.get $fromIndex
    local.get $toIndex
    local.get $<this>
    call $kotlin.CharArray.<get-size>
    call $kotlin.ranges.coerceAtMost
    call $kotlin.collections.copyInto
    drop
    local.get $result
    return
  )
  (func $kotlin.assert (;151;) (type 8) (param $value i32)
    nop
  )
  (func $"#func152 kotlin.assert" (@name "kotlin.assert") (;152;) (type $"#type231 ") (param $value i32) (param $lazyMessage (ref null $kotlin.Any))
    (local $message (ref null $kotlin.Any))
    local.get $value
    i32.eqz
    if ;; label = @1
      local.get $lazyMessage
      local.get $lazyMessage
      struct.get $kotlin.Any $itable
      ref.cast (ref $"#type40 classITable")
      struct.get $"#type40 classITable" $"#field2 kotlin.itable"
      struct.get $kotlin.Function0.itable $invoke
      call_ref $"#type161 "
      local.set $message
      ref.null none
      local.get $message
      call $kotlin.AssertionError.<init>
      throw 0
    else
    end
    nop
  )
  (func $kotlin.assert$lambda.<init> (;153;) (type $"#type232 ") (param $<this> (ref null $kotlin.assert$lambda)) (result (ref null $kotlin.assert$lambda))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.assert$lambda.vtable
      global.get $kotlin.assert$lambda.classITable
      i32.const 1352
      i32.const 0
      struct.new $kotlin.assert$lambda
      local.set $<this>
    end
    nop
    local.get $<this>
    return
  )
  (func $kotlin.assert$lambda.invoke (;154;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    i32.const 59
    i32.const 1124
    i32.const 16
    call $kotlin.stringLiteral
    return
  )
  (func $"#func155 kotlin.assert$lambda.invoke" (@name "kotlin.assert$lambda.invoke") (;155;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    local.get $<this>
    call $kotlin.assert$lambda.invoke
    return
  )
  (func $kotlin.IllegalArgumentException.<init> (;156;) (type $"#type233 ") (param $<this> (ref null $kotlin.IllegalArgumentException)) (param $message (ref null $kotlin.String)) (result (ref null $kotlin.IllegalArgumentException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.IllegalArgumentException.vtable
      ref.null struct
      i32.const 1392
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.IllegalArgumentException
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    call $"#func166 kotlin.RuntimeException.<init>"
    drop
    local.get $<this>
    call $"#func157 kotlin.IllegalArgumentException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func157 kotlin.IllegalArgumentException.<init>" (@name "kotlin.IllegalArgumentException.<init>") (;157;) (type $"#type234 ") (param $<this> (ref null $kotlin.IllegalArgumentException)) (result (ref null $kotlin.IllegalArgumentException))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.NoSuchElementException.<init> (;158;) (type $"#type235 ") (param $<this> (ref null $kotlin.NoSuchElementException)) (result (ref null $kotlin.NoSuchElementException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.NoSuchElementException.vtable
      ref.null struct
      i32.const 1424
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.NoSuchElementException
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.RuntimeException.<init>
    drop
    local.get $<this>
    call $"#func159 kotlin.NoSuchElementException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func159 kotlin.NoSuchElementException.<init>" (@name "kotlin.NoSuchElementException.<init>") (;159;) (type $"#type235 ") (param $<this> (ref null $kotlin.NoSuchElementException)) (result (ref null $kotlin.NoSuchElementException))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.IndexOutOfBoundsException.<init> (;160;) (type $"#type236 ") (param $<this> (ref null $kotlin.IndexOutOfBoundsException)) (result (ref null $kotlin.IndexOutOfBoundsException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.IndexOutOfBoundsException.vtable
      ref.null struct
      i32.const 1456
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.IndexOutOfBoundsException
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.RuntimeException.<init>
    drop
    local.get $<this>
    call $"#func162 kotlin.IndexOutOfBoundsException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func161 kotlin.IndexOutOfBoundsException.<init>" (@name "kotlin.IndexOutOfBoundsException.<init>") (;161;) (type $"#type237 ") (param $<this> (ref null $kotlin.IndexOutOfBoundsException)) (param $message (ref null $kotlin.String)) (result (ref null $kotlin.IndexOutOfBoundsException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.IndexOutOfBoundsException.vtable
      ref.null struct
      i32.const 1456
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.IndexOutOfBoundsException
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    call $"#func166 kotlin.RuntimeException.<init>"
    drop
    local.get $<this>
    call $"#func162 kotlin.IndexOutOfBoundsException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func162 kotlin.IndexOutOfBoundsException.<init>" (@name "kotlin.IndexOutOfBoundsException.<init>") (;162;) (type $"#type236 ") (param $<this> (ref null $kotlin.IndexOutOfBoundsException)) (result (ref null $kotlin.IndexOutOfBoundsException))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.AssertionError.<init> (;163;) (type $"#type238 ") (param $<this> (ref null $kotlin.AssertionError)) (param $message (ref null $kotlin.Any)) (result (ref null $kotlin.AssertionError))
    (local $tmp0_safe_receiver (ref null $kotlin.Any))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.AssertionError.vtable
      ref.null struct
      i32.const 1488
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.AssertionError
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    local.tee $tmp0_safe_receiver
    ref.is_null
    if (result (ref null $kotlin.String)) ;; label = @1
      ref.null none
    else
      local.get $tmp0_safe_receiver
      local.get $tmp0_safe_receiver
      struct.get $kotlin.Any $vtable
      struct.get $kotlin.Any.vtable $toString
      call_ref $"#type170 "
    end
    local.get $message
    ref.is_null
    if (result i32) ;; label = @1
      i32.const 0
    else
      local.get $message
      ref.test (ref $kotlin.Throwable)
    end
    if (result (ref null $kotlin.Throwable)) ;; label = @1
      local.get $message
      ref.cast (ref null $kotlin.Throwable)
    else
      ref.null none
    end
    call $"#func169 kotlin.Error.<init>"
    drop
    local.get $<this>
    call $"#func164 kotlin.AssertionError.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func164 kotlin.AssertionError.<init>" (@name "kotlin.AssertionError.<init>") (;164;) (type $"#type239 ") (param $<this> (ref null $kotlin.AssertionError)) (result (ref null $kotlin.AssertionError))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.RuntimeException.<init> (;165;) (type $"#type240 ") (param $<this> (ref null $kotlin.RuntimeException)) (result (ref null $kotlin.RuntimeException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.RuntimeException.vtable
      ref.null struct
      i32.const 1520
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.RuntimeException
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.Exception.<init>
    drop
    local.get $<this>
    call $"#func167 kotlin.RuntimeException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func166 kotlin.RuntimeException.<init>" (@name "kotlin.RuntimeException.<init>") (;166;) (type $"#type241 ") (param $<this> (ref null $kotlin.RuntimeException)) (param $message (ref null $kotlin.String)) (result (ref null $kotlin.RuntimeException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.RuntimeException.vtable
      ref.null struct
      i32.const 1520
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.RuntimeException
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    call $"#func172 kotlin.Exception.<init>"
    drop
    local.get $<this>
    call $"#func167 kotlin.RuntimeException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func167 kotlin.RuntimeException.<init>" (@name "kotlin.RuntimeException.<init>") (;167;) (type $"#type240 ") (param $<this> (ref null $kotlin.RuntimeException)) (result (ref null $kotlin.RuntimeException))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Error.<init> (;168;) (type $"#type242 ") (param $<this> (ref null $kotlin.Error)) (result (ref null $kotlin.Error))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Error.vtable
      ref.null struct
      i32.const 1552
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.Error
      local.set $<this>
    end
    local.get $<this>
    call $"#func199 kotlin.Throwable.<init>"
    drop
    local.get $<this>
    call $"#func170 kotlin.Error.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func169 kotlin.Error.<init>" (@name "kotlin.Error.<init>") (;169;) (type $"#type243 ") (param $<this> (ref null $kotlin.Error)) (param $message (ref null $kotlin.String)) (param $cause (ref null $kotlin.Throwable)) (result (ref null $kotlin.Error))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Error.vtable
      ref.null struct
      i32.const 1552
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.Error
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    local.get $cause
    call $kotlin.Throwable.<init>
    drop
    local.get $<this>
    call $"#func170 kotlin.Error.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func170 kotlin.Error.<init>" (@name "kotlin.Error.<init>") (;170;) (type $"#type242 ") (param $<this> (ref null $kotlin.Error)) (result (ref null $kotlin.Error))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Exception.<init> (;171;) (type $"#type244 ") (param $<this> (ref null $kotlin.Exception)) (result (ref null $kotlin.Exception))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Exception.vtable
      ref.null struct
      i32.const 1584
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.Exception
      local.set $<this>
    end
    local.get $<this>
    call $"#func199 kotlin.Throwable.<init>"
    drop
    local.get $<this>
    call $"#func173 kotlin.Exception.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func172 kotlin.Exception.<init>" (@name "kotlin.Exception.<init>") (;172;) (type $"#type245 ") (param $<this> (ref null $kotlin.Exception)) (param $message (ref null $kotlin.String)) (result (ref null $kotlin.Exception))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Exception.vtable
      ref.null struct
      i32.const 1584
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.Exception
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    call $"#func198 kotlin.Throwable.<init>"
    drop
    local.get $<this>
    call $"#func173 kotlin.Exception.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func173 kotlin.Exception.<init>" (@name "kotlin.Exception.<init>") (;173;) (type $"#type244 ") (param $<this> (ref null $kotlin.Exception)) (result (ref null $kotlin.Exception))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.IllegalStateException.<init> (;174;) (type $"#type246 ") (param $<this> (ref null $kotlin.IllegalStateException)) (param $message (ref null $kotlin.String)) (result (ref null $kotlin.IllegalStateException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.IllegalStateException.vtable
      ref.null struct
      i32.const 1616
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.IllegalStateException
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    call $"#func166 kotlin.RuntimeException.<init>"
    drop
    local.get $<this>
    call $"#func175 kotlin.IllegalStateException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func175 kotlin.IllegalStateException.<init>" (@name "kotlin.IllegalStateException.<init>") (;175;) (type $"#type247 ") (param $<this> (ref null $kotlin.IllegalStateException)) (result (ref null $kotlin.IllegalStateException))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.OutOfMemoryError.<init> (;176;) (type $"#type248 ") (param $<this> (ref null $kotlin.OutOfMemoryError)) (result (ref null $kotlin.OutOfMemoryError))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.OutOfMemoryError.vtable
      ref.null struct
      i32.const 1648
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.OutOfMemoryError
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.Error.<init>
    drop
    local.get $<this>
    call $"#func177 kotlin.OutOfMemoryError.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func177 kotlin.OutOfMemoryError.<init>" (@name "kotlin.OutOfMemoryError.<init>") (;177;) (type $"#type248 ") (param $<this> (ref null $kotlin.OutOfMemoryError)) (result (ref null $kotlin.OutOfMemoryError))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.ConcurrentModificationException.<init> (;178;) (type $"#type249 ") (param $<this> (ref null $kotlin.ConcurrentModificationException)) (result (ref null $kotlin.ConcurrentModificationException))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.ConcurrentModificationException.vtable
      ref.null struct
      i32.const 1680
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.ConcurrentModificationException
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.RuntimeException.<init>
    drop
    local.get $<this>
    call $"#func179 kotlin.ConcurrentModificationException.<init>"
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func179 kotlin.ConcurrentModificationException.<init>" (@name "kotlin.ConcurrentModificationException.<init>") (;179;) (type $"#type249 ") (param $<this> (ref null $kotlin.ConcurrentModificationException)) (result (ref null $kotlin.ConcurrentModificationException))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Unit.<init> (;180;) (type $"#type250 ") (param $<this> (ref null $kotlin.Unit)) (result (ref null $kotlin.Unit))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Unit.vtable
      ref.null struct
      i32.const 1712
      i32.const 0
      struct.new $kotlin.Unit
      local.set $<this>
    end
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Unit.toString (;181;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    i32.const 71
    i32.const 1530
    i32.const 11
    call $kotlin.stringLiteral
    return
  )
  (func $kotlin.Unit_getInstance (;182;) (type $"#type251 ") (result (ref null $kotlin.Unit))
    global.get $kotlin.Unit_instance
    return
  )
  (func $kotlin.text.insertInt (;183;) (type $"#type252 ") (param $array (ref null $kotlin.CharArray)) (param $start i32) (param $value i32) (result i32)
    (local $valueString (ref null $kotlin.String)) (local $length i32)
    local.get $value
    call $kotlin.Int__toString-impl
    local.tee $valueString
    struct.get $kotlin.String $length
    local.set $length
    local.get $array
    local.get $start
    local.get $valueString
    i32.const 0
    local.get $length
    call $"#func185 kotlin.text.insertString"
    drop
    local.get $length
    return
  )
  (func $kotlin.text.unsafeStringFromCharArray (;184;) (type $"#type253 ") (param $array (ref null $kotlin.CharArray)) (param $start i32) (param $size i32) (result (ref null $kotlin.String))
    (local $copy (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp0 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp1 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp2 i32) (local $tmp3 i32) (local $tmp4 i32) (local $source (ref null $kotlin.wasm.internal.WasmCharArray)) (local $destination (ref null $kotlin.wasm.internal.WasmCharArray)) (local $sourceIndex i32) (local $destinationIndex i32) (local $length i32) (local $tmp5 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $this (ref null $kotlin.wasm.internal.WasmCharArray))
    local.get $size
    array.new_default $kotlin.wasm.internal.WasmCharArray
    local.set $copy
    local.get $array
    struct.get $kotlin.CharArray $storage
    local.set $tmp0
    local.get $copy
    local.set $tmp1
    local.get $start
    local.set $tmp2
    i32.const 0
    local.set $tmp3
    local.get $size
    local.set $tmp4
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp0
      local.set $source
      local.get $tmp1
      local.set $destination
      local.get $tmp2
      local.set $sourceIndex
      local.get $tmp3
      local.set $destinationIndex
      local.get $tmp4
      local.set $length
      local.get $destination
      local.get $destinationIndex
      local.get $source
      local.get $sourceIndex
      local.get $length
      array.copy $kotlin.wasm.internal.WasmCharArray $kotlin.wasm.internal.WasmCharArray
      global.get $kotlin.Unit_instance
    end
    drop
    local.get $copy
    local.set $tmp5
    block (result (ref null $kotlin.String)) ;; label = @1
      nop
      local.get $tmp5
      local.set $this
      global.get $kotlin.String.vtable
      global.get $kotlin.String.classITable
      i32.const 1276
      i32.const 0
      ref.null none
      local.get $this
      array.len
      local.get $this
      struct.new $kotlin.String
      br 0 (;@1;)
    end
    return
  )
  (func $"#func185 kotlin.text.insertString" (@name "kotlin.text.insertString") (;185;) (type $"#type254 ") (param $array (ref null $kotlin.CharArray)) (param $destinationIndex i32) (param $value (ref null $kotlin.String)) (param $sourceIndex i32) (param $count i32) (result i32)
    (local $tmp1 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp0 (ref null $kotlin.String)) (local $this (ref null $kotlin.String)) (local $tmp2 (ref null $kotlin.wasm.internal.WasmCharArray)) (local $tmp3 i32) (local $tmp4 i32) (local $tmp5 i32) (local $source (ref null $kotlin.wasm.internal.WasmCharArray)) (local $destination (ref null $kotlin.wasm.internal.WasmCharArray)) (local $"#local14 sourceIndex" (@name "sourceIndex") i32) (local $"#local15 destinationIndex" (@name "destinationIndex") i32) (local $length i32)
    local.get $value
    local.set $tmp0
    block (result (ref null $kotlin.wasm.internal.WasmCharArray)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      struct.get $kotlin.String $leftIfInSum
      ref.is_null
      i32.eqz
      if ;; label = @2
        local.get $this
        call $kotlin.String.foldChars
      else
      end
      local.get $this
      struct.get $kotlin.String $_chars
      br 0 (;@1;)
    end
    local.set $tmp1
    local.get $array
    struct.get $kotlin.CharArray $storage
    local.set $tmp2
    local.get $sourceIndex
    local.set $tmp3
    local.get $destinationIndex
    local.set $tmp4
    local.get $count
    local.set $tmp5
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp1
      local.set $source
      local.get $tmp2
      local.set $destination
      local.get $tmp3
      local.set $"#local14 sourceIndex"
      local.get $tmp4
      local.set $"#local15 destinationIndex"
      local.get $tmp5
      local.set $length
      local.get $destination
      local.get $"#local15 destinationIndex"
      local.get $source
      local.get $"#local14 sourceIndex"
      local.get $length
      array.copy $kotlin.wasm.internal.WasmCharArray $kotlin.wasm.internal.WasmCharArray
      global.get $kotlin.Unit_instance
    end
    drop
    local.get $count
    return
  )
  (func $kotlin.wasm.unsafe.<Pointer__<init>-impl> (;186;) (type 2) (param $address i32) (result i32)
    local.get $address
    return
  )
  (func $kotlin.wasm.unsafe.<Pointer__<get-address>-impl> (;187;) (type 2) (param $$this i32) (result i32)
    local.get $$this
    return
  )
  (func $kotlin.wasm.unsafe.Pointer__toString-impl (;188;) (type $"#type205 ") (param $$this i32) (result (ref null $kotlin.String))
    (local $tmp (ref null $kotlin.text.StringBuilder))
    ref.null none
    call $"#func29 kotlin.text.StringBuilder.<init>"
    local.tee $tmp
    i32.const 72
    i32.const 1552
    i32.const 16
    call $kotlin.stringLiteral
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    global.get $kotlin.UInt.vtable
    global.get $kotlin.UInt.classITable
    i32.const 784
    i32.const 0
    local.get $$this
    struct.new $kotlin.UInt
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    i32.const 73
    i32.const 1584
    i32.const 1
    call $kotlin.stringLiteral
    call $"#func38 kotlin.text.StringBuilder.append"
    drop
    local.get $tmp
    local.get $tmp
    struct.get $kotlin.Any $vtable
    struct.get $kotlin.Any.vtable $toString
    call_ref $"#type170 "
    return
  )
  (func $kotlin.wasm.unsafe.Pointer.toString (;189;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> i32)
    local.get $<this>
    ref.cast (ref $kotlin.wasm.unsafe.Pointer)
    struct.get $kotlin.wasm.unsafe.Pointer $address
    local.tee $tmp0_<this>
    call $kotlin.wasm.unsafe.Pointer__toString-impl
    return
  )
  (func $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init> (;190;) (type $"#type255 ") (param $<this> (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (param $startAddress i32) (param $parent (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.wasm.unsafe.ScopedMemoryAllocator.vtable
      ref.null struct
      i32.const 1776
      i32.const 0
      ref.null $kotlin.wasm.unsafe.ScopedMemoryAllocator
      i32.const 0
      i32.const 0
      i32.const 0
      struct.new $kotlin.wasm.unsafe.ScopedMemoryAllocator
      local.set $<this>
    end
    local.get $<this>
    call $kotlin.wasm.unsafe.MemoryAllocator.<init>
    drop
    local.get $<this>
    local.get $parent
    struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $parent
    local.get $<this>
    i32.const 0
    struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $destroyed
    local.get $<this>
    i32.const 0
    struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $suspended
    local.get $<this>
    local.get $startAddress
    struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
    nop
    local.get $<this>
    return
  )
  (func $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate (;191;) (type $"#type154 ") (param $<this> (ref null $kotlin.Any)) (param $size i32) (result i32)
    (local $tmp0_<this> (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (local $tmp0 i32) (local $value i32) (local $message (ref null $kotlin.String)) (local $tmp1 i32) (local $"#local7 value" (@name "value") i32) (local $"#local8 message" (@name "message") (ref null $kotlin.String)) (local $align i32) (local $result i32) (local $tmp2 i32) (local $this i32) (local $tmp3 i32) (local $"#local14 value" (@name "value") i32) (local $"#local15 message" (@name "message") (ref null $kotlin.String)) (local $tmp4 (ref null $kotlin.String)) (local $"#local17 message" (@name "message") (ref null $kotlin.String)) (local $currentMaxSize i32) (local $numPagesToGrow i32) (local $tmp5 (ref null $kotlin.String)) (local $"#local21 message" (@name "message") (ref null $kotlin.String)) (local $tmp6 i32) (local $"#local23 value" (@name "value") i32) (local $tmp7 i32) (local $"#local25 this" (@name "this") i32)
    local.get $<this>
    ref.cast (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)
    local.tee $tmp0_<this>
    struct.get_s $kotlin.wasm.unsafe.ScopedMemoryAllocator $destroyed
    i32.eqz
    local.set $tmp0
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $value
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        block (result (ref null $kotlin.String)) ;; label = @3
          nop
          i32.const 76
          i32.const 1642
          i32.const 52
          call $kotlin.stringLiteral
          br 0 (;@3;)
        end
        local.set $message
        ref.null none
        local.get $message
        call $kotlin.IllegalStateException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    local.get $tmp0_<this>
    struct.get_s $kotlin.wasm.unsafe.ScopedMemoryAllocator $suspended
    i32.eqz
    local.set $tmp1
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp1
      local.tee $"#local7 value"
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        block (result (ref null $kotlin.String)) ;; label = @3
          nop
          i32.const 77
          i32.const 1746
          i32.const 66
          call $kotlin.stringLiteral
          br 0 (;@3;)
        end
        local.set $"#local8 message"
        ref.null none
        local.get $"#local8 message"
        call $kotlin.IllegalStateException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    i32.const 8
    local.set $align
    local.get $tmp0_<this>
    struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
    local.get $align
    i32.add
    i32.const 1
    i32.sub
    local.get $align
    i32.const 1
    i32.sub
    local.set $tmp2
    block (result i32) ;; label = @1
      nop
      local.get $tmp2
      local.tee $this
      i32.const -1
      i32.xor
      br 0 (;@1;)
    end
    i32.and
    local.tee $result
    i32.const 0
    i32.gt_s
    if (result i32) ;; label = @1
      local.get $result
      local.get $align
      i32.rem_s
      i32.const 0
      i32.eq
    else
      i32.const 0
    end
    local.set $tmp3
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp3
      local.tee $"#local14 value"
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        block (result (ref null $kotlin.String)) ;; label = @3
          nop
          i32.const 78
          i32.const 1878
          i32.const 37
          call $kotlin.stringLiteral
          br 0 (;@3;)
        end
        local.set $"#local15 message"
        ref.null none
        local.get $"#local15 message"
        call $kotlin.IllegalStateException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    i32.const 2147483647
    local.get $tmp0_<this>
    struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
    i32.sub
    local.get $size
    i32.lt_s
    if ;; label = @1
      i32.const 79
      i32.const 1952
      i32.const 64
      call $kotlin.stringLiteral
      local.set $tmp4
      block ;; label = @2
        nop
        local.get $tmp4
        local.set $"#local17 message"
        ref.null none
        local.get $"#local17 message"
        call $kotlin.IllegalStateException.<init>
        throw 0
      end
      unreachable
    else
    end
    local.get $tmp0_<this>
    local.get $result
    local.get $size
    i32.add
    struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
    memory.size
    i32.const 65536
    i32.mul
    local.set $currentMaxSize
    local.get $tmp0_<this>
    struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
    local.get $currentMaxSize
    i32.ge_s
    if ;; label = @1
      local.get $tmp0_<this>
      struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
      local.get $currentMaxSize
      i32.sub
      i32.const 65536
      call $kotlin.Int__div-impl
      i32.const 2
      i32.add
      local.tee $numPagesToGrow
      memory.grow
      i32.const -1
      i32.eq
      if ;; label = @2
        i32.const 80
        i32.const 2080
        i32.const 45
        call $kotlin.stringLiteral
        local.set $tmp5
        block ;; label = @3
          nop
          local.get $tmp5
          local.set $"#local21 message"
          ref.null none
          local.get $"#local21 message"
          call $kotlin.IllegalStateException.<init>
          throw 0
        end
        unreachable
      else
      end
    else
    end
    local.get $tmp0_<this>
    struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
    memory.size
    i32.const 65536
    i32.mul
    i32.lt_s
    local.set $tmp6
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp6
      local.tee $"#local23 value"
      i32.eqz
      if (result (ref null $kotlin.Unit)) ;; label = @2
        ref.null none
        i32.const 48
        i32.const 1014
        i32.const 13
        call $kotlin.stringLiteral
        call $kotlin.IllegalStateException.<init>
        throw 0
      else
        global.get $kotlin.Unit_instance
      end
    end
    drop
    local.get $result
    local.set $tmp7
    block (result i32) ;; label = @1
      nop
      local.get $tmp7
      local.tee $"#local25 this"
      call $kotlin.<UInt__<init>-impl>
      br 0 (;@1;)
    end
    call $kotlin.wasm.unsafe.<Pointer__<init>-impl>
    return
  )
  (func $kotlin.wasm.unsafe.ScopedMemoryAllocator.createChild (;192;) (type $"#type256 ") (param $<this> (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))
    (local $child (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (local $tmp0 i32) (local $this i32)
    ref.null none
    local.get $<this>
    struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $availableAddress
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      br 0 (;@1;)
    end
    local.get $<this>
    call $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init>
    local.set $child
    local.get $<this>
    i32.const 1
    struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $suspended
    local.get $child
    return
  )
  (func $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy (;193;) (type $"#type257 ") (param $<this> (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))
    (local $tmp0_safe_receiver (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))
    local.get $<this>
    i32.const 1
    struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $destroyed
    local.get $<this>
    struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $parent
    local.tee $tmp0_safe_receiver
    ref.is_null
    if ;; label = @1
    else
      local.get $tmp0_safe_receiver
      i32.const 0
      struct.set $kotlin.wasm.unsafe.ScopedMemoryAllocator $suspended
    end
    nop
  )
  (func $kotlin.wasm.unsafe.MemoryAllocator.<init> (;194;) (type $"#type258 ") (param $<this> (ref null $kotlin.wasm.unsafe.MemoryAllocator)) (result (ref null $kotlin.wasm.unsafe.MemoryAllocator))
    nop
    local.get $<this>
    return
  )
  (func $kotlin.wasm.unsafe.createAllocatorInTheNewScope (;195;) (type $"#type259 ") (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))
    (local $allocator (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (local $tmp1_elvis_lhs (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (local $tmp0_safe_receiver (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator))
    global.get $kotlin.wasm.unsafe.currentAllocator
    local.tee $tmp0_safe_receiver
    ref.is_null
    if (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) ;; label = @1
      ref.null none
    else
      local.get $tmp0_safe_receiver
      call $kotlin.wasm.unsafe.ScopedMemoryAllocator.createChild
    end
    local.tee $tmp1_elvis_lhs
    ref.is_null
    if (result (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) ;; label = @1
      ref.null none
      i32.const 2012
      ref.null none
      call $kotlin.wasm.unsafe.ScopedMemoryAllocator.<init>
    else
      local.get $tmp1_elvis_lhs
    end
    local.tee $allocator
    global.set $kotlin.wasm.unsafe.currentAllocator
    local.get $allocator
    return
  )
  (func $kotlin.Throwable.<init> (;196;) (type $"#type260 ") (param $<this> (ref null $kotlin.Throwable)) (param $message (ref null $kotlin.String)) (param $cause (ref null $kotlin.Throwable)) (result (ref null $kotlin.Throwable))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Throwable.vtable
      ref.null struct
      i32.const 1836
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.Throwable
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    struct.set $kotlin.Throwable $message
    local.get $<this>
    local.get $cause
    struct.set $kotlin.Throwable $cause
    local.get $<this>
    ref.null none
    struct.set $kotlin.Throwable $suppressedExceptionsList
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Throwable.<get-message> (;197;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> (ref null $kotlin.Throwable))
    local.get $<this>
    ref.cast (ref null $kotlin.Throwable)
    local.tee $tmp0_<this>
    struct.get $kotlin.Throwable $message
    return
  )
  (func $"#func198 kotlin.Throwable.<init>" (@name "kotlin.Throwable.<init>") (;198;) (type $"#type261 ") (param $<this> (ref null $kotlin.Throwable)) (param $message (ref null $kotlin.String)) (result (ref null $kotlin.Throwable))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Throwable.vtable
      ref.null struct
      i32.const 1836
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.Throwable
      local.set $<this>
    end
    local.get $<this>
    local.get $message
    ref.null none
    call $kotlin.Throwable.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $"#func199 kotlin.Throwable.<init>" (@name "kotlin.Throwable.<init>") (;199;) (type $"#type262 ") (param $<this> (ref null $kotlin.Throwable)) (result (ref null $kotlin.Throwable))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.Throwable.vtable
      ref.null struct
      i32.const 1836
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      struct.new $kotlin.Throwable
      local.set $<this>
    end
    local.get $<this>
    ref.null none
    ref.null none
    call $kotlin.Throwable.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.Throwable.toString (;200;) (type $"#type170 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.String))
    (local $tmp0_<this> (ref null $kotlin.Throwable)) (local $s (ref null $kotlin.String))
    local.get $<this>
    ref.cast (ref null $kotlin.Throwable)
    local.tee $tmp0_<this>
    struct.get $kotlin.Any $typeInfo
    call $kotlin.wasm.internal.getSimpleName
    local.set $s
    local.get $tmp0_<this>
    local.get $tmp0_<this>
    struct.get $kotlin.Throwable $vtable
    struct.get $kotlin.Throwable.vtable $<get-message>
    call_ref $"#type170 "
    ref.is_null
    i32.eqz
    if (result (ref null $kotlin.String)) ;; label = @1
      local.get $s
      i32.const 83
      i32.const 2218
      i32.const 2
      call $kotlin.stringLiteral
      call $kotlin.String.plus
      local.get $tmp0_<this>
      local.get $tmp0_<this>
      struct.get $kotlin.Throwable $vtable
      struct.get $kotlin.Throwable.vtable $<get-message>
      call_ref $"#type170 "
      call $kotlin.toString
      call $kotlin.String.plus
    else
      local.get $s
    end
    return
  )
  (func $kotlin.wasm.WasiError.<init> (;201;) (type $"#type263 ") (param $<this> (ref null $kotlin.wasm.WasiError)) (param $error (ref null $kotlin.wasm.WasiErrorCode)) (result (ref null $kotlin.wasm.WasiError))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.wasm.WasiError.vtable
      ref.null struct
      i32.const 1868
      i32.const 0
      ref.null $kotlin.String
      ref.null $kotlin.Throwable
      ref.null $kotlin.Any
      ref.null $kotlin.wasm.WasiErrorCode
      struct.new $kotlin.wasm.WasiError
      local.set $<this>
    end
    local.get $<this>
    i32.const 85
    i32.const 2240
    i32.const 22
    call $kotlin.stringLiteral
    local.get $error
    call $kotlin.String.plus
    call $"#func198 kotlin.Throwable.<init>"
    drop
    local.get $<this>
    local.get $error
    struct.set $kotlin.wasm.WasiError $error
    nop
    local.get $<this>
    return
  )
  (func $kotlin.wasm.values (;202;) (type $"#type264 ") (result (ref null $kotlin.Array))
    global.get $kotlin.Array.vtable
    ref.null struct
    i32.const 920
    i32.const 0
    call $kotlin.wasm.WasiErrorCode_SUCCESS_getInstance
    call $kotlin.wasm.WasiErrorCode__2BIG_getInstance
    call $kotlin.wasm.WasiErrorCode_ACCES_getInstance
    call $kotlin.wasm.WasiErrorCode_ADDRINUSE_getInstance
    call $kotlin.wasm.WasiErrorCode_ADDRNOTAVAIL_getInstance
    call $kotlin.wasm.WasiErrorCode_AFNOSUPPORT_getInstance
    call $kotlin.wasm.WasiErrorCode_AGAIN_getInstance
    call $kotlin.wasm.WasiErrorCode_ALREADY_getInstance
    call $kotlin.wasm.WasiErrorCode_BADF_getInstance
    call $kotlin.wasm.WasiErrorCode_BADMSG_getInstance
    call $kotlin.wasm.WasiErrorCode_BUSY_getInstance
    call $kotlin.wasm.WasiErrorCode_CANCELED_getInstance
    call $kotlin.wasm.WasiErrorCode_CHILD_getInstance
    call $kotlin.wasm.WasiErrorCode_CONNABORTED_getInstance
    call $kotlin.wasm.WasiErrorCode_CONNREFUSED_getInstance
    call $kotlin.wasm.WasiErrorCode_CONNRESET_getInstance
    call $kotlin.wasm.WasiErrorCode_DEADLK_getInstance
    call $kotlin.wasm.WasiErrorCode_DESTADDRREQ_getInstance
    call $kotlin.wasm.WasiErrorCode_DOM_getInstance
    call $kotlin.wasm.WasiErrorCode_DQUOT_getInstance
    call $kotlin.wasm.WasiErrorCode_EXIST_getInstance
    call $kotlin.wasm.WasiErrorCode_FAULT_getInstance
    call $kotlin.wasm.WasiErrorCode_FBIG_getInstance
    call $kotlin.wasm.WasiErrorCode_HOSTUNREACH_getInstance
    call $kotlin.wasm.WasiErrorCode_IDRM_getInstance
    call $kotlin.wasm.WasiErrorCode_ILSEQ_getInstance
    call $kotlin.wasm.WasiErrorCode_INPROGRESS_getInstance
    call $kotlin.wasm.WasiErrorCode_INTR_getInstance
    call $kotlin.wasm.WasiErrorCode_INVAL_getInstance
    call $kotlin.wasm.WasiErrorCode_IO_getInstance
    call $kotlin.wasm.WasiErrorCode_ISCONN_getInstance
    call $kotlin.wasm.WasiErrorCode_ISDIR_getInstance
    call $kotlin.wasm.WasiErrorCode_LOOP_getInstance
    call $kotlin.wasm.WasiErrorCode_MFILE_getInstance
    call $kotlin.wasm.WasiErrorCode_MLINK_getInstance
    call $kotlin.wasm.WasiErrorCode_MSGSIZE_getInstance
    call $kotlin.wasm.WasiErrorCode_MULTIHOP_getInstance
    call $kotlin.wasm.WasiErrorCode_NAMETOOLONG_getInstance
    call $kotlin.wasm.WasiErrorCode_NETDOWN_getInstance
    call $kotlin.wasm.WasiErrorCode_NETRESET_getInstance
    call $kotlin.wasm.WasiErrorCode_NETUNREACH_getInstance
    call $kotlin.wasm.WasiErrorCode_NFILE_getInstance
    call $kotlin.wasm.WasiErrorCode_NOBUFS_getInstance
    call $kotlin.wasm.WasiErrorCode_NODEV_getInstance
    call $kotlin.wasm.WasiErrorCode_NOENT_getInstance
    call $kotlin.wasm.WasiErrorCode_NOEXEC_getInstance
    call $kotlin.wasm.WasiErrorCode_NOLCK_getInstance
    call $kotlin.wasm.WasiErrorCode_NOLINK_getInstance
    call $kotlin.wasm.WasiErrorCode_NOMEM_getInstance
    call $kotlin.wasm.WasiErrorCode_NOMSG_getInstance
    call $kotlin.wasm.WasiErrorCode_NOPROTOOPT_getInstance
    call $kotlin.wasm.WasiErrorCode_NOSPC_getInstance
    call $kotlin.wasm.WasiErrorCode_NOSYS_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTCONN_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTDIR_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTEMPTY_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTRECOVERABLE_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTSOCK_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTSUP_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTTY_getInstance
    call $kotlin.wasm.WasiErrorCode_NXIO_getInstance
    call $kotlin.wasm.WasiErrorCode_OVERFLOW_getInstance
    call $kotlin.wasm.WasiErrorCode_OWNERDEAD_getInstance
    call $kotlin.wasm.WasiErrorCode_PERM_getInstance
    call $kotlin.wasm.WasiErrorCode_PIPE_getInstance
    call $kotlin.wasm.WasiErrorCode_PROTO_getInstance
    call $kotlin.wasm.WasiErrorCode_PROTONOSUPPORT_getInstance
    call $kotlin.wasm.WasiErrorCode_PROTOTYPE_getInstance
    call $kotlin.wasm.WasiErrorCode_RANGE_getInstance
    call $kotlin.wasm.WasiErrorCode_ROFS_getInstance
    call $kotlin.wasm.WasiErrorCode_SPIPE_getInstance
    call $kotlin.wasm.WasiErrorCode_SRCH_getInstance
    call $kotlin.wasm.WasiErrorCode_STALE_getInstance
    call $kotlin.wasm.WasiErrorCode_TIMEDOUT_getInstance
    call $kotlin.wasm.WasiErrorCode_TXTBSY_getInstance
    call $kotlin.wasm.WasiErrorCode_XDEV_getInstance
    call $kotlin.wasm.WasiErrorCode_NOTCAPABLE_getInstance
    array.new_fixed $kotlin.wasm.internal.WasmAnyArray 77
    struct.new $kotlin.Array
    return
  )
  (func $kotlin.wasm.<get-entries> (;203;) (type $"#type265 ") (result (ref null $kotlin.Any))
    global.get $kotlin.wasm.$ENTRIES
    ref.is_null
    if ;; label = @1
      call $kotlin.wasm.values
      call $kotlin.enums.enumEntries
      global.set $kotlin.wasm.$ENTRIES
    else
    end
    global.get $kotlin.wasm.$ENTRIES
    return
  )
  (func $kotlin.wasm.WasiErrorCode_initEntries (;204;) (type 6)
    global.get $kotlin.wasm.WasiErrorCode_entriesInitialized
    if ;; label = @1
      return
    else
    end
    i32.const 1
    global.set $kotlin.wasm.WasiErrorCode_entriesInitialized
    ref.null none
    i32.const 86
    i32.const 2284
    i32.const 7
    call $kotlin.stringLiteral
    i32.const 0
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_SUCCESS_instance
    ref.null none
    i32.const 87
    i32.const 2298
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 1
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode__2BIG_instance
    ref.null none
    i32.const 88
    i32.const 2308
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 2
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ACCES_instance
    ref.null none
    i32.const 89
    i32.const 2318
    i32.const 9
    call $kotlin.stringLiteral
    i32.const 3
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ADDRINUSE_instance
    ref.null none
    i32.const 90
    i32.const 2336
    i32.const 12
    call $kotlin.stringLiteral
    i32.const 4
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ADDRNOTAVAIL_instance
    ref.null none
    i32.const 91
    i32.const 2360
    i32.const 11
    call $kotlin.stringLiteral
    i32.const 5
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_AFNOSUPPORT_instance
    ref.null none
    i32.const 92
    i32.const 2382
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 6
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_AGAIN_instance
    ref.null none
    i32.const 93
    i32.const 2392
    i32.const 7
    call $kotlin.stringLiteral
    i32.const 7
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ALREADY_instance
    ref.null none
    i32.const 94
    i32.const 2406
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 8
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_BADF_instance
    ref.null none
    i32.const 95
    i32.const 2414
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 9
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_BADMSG_instance
    ref.null none
    i32.const 96
    i32.const 2426
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 10
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_BUSY_instance
    ref.null none
    i32.const 97
    i32.const 2434
    i32.const 8
    call $kotlin.stringLiteral
    i32.const 11
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_CANCELED_instance
    ref.null none
    i32.const 98
    i32.const 2450
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 12
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_CHILD_instance
    ref.null none
    i32.const 99
    i32.const 2460
    i32.const 11
    call $kotlin.stringLiteral
    i32.const 13
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_CONNABORTED_instance
    ref.null none
    i32.const 100
    i32.const 2482
    i32.const 11
    call $kotlin.stringLiteral
    i32.const 14
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_CONNREFUSED_instance
    ref.null none
    i32.const 101
    i32.const 2504
    i32.const 9
    call $kotlin.stringLiteral
    i32.const 15
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_CONNRESET_instance
    ref.null none
    i32.const 102
    i32.const 2522
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 16
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_DEADLK_instance
    ref.null none
    i32.const 103
    i32.const 2534
    i32.const 11
    call $kotlin.stringLiteral
    i32.const 17
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_DESTADDRREQ_instance
    ref.null none
    i32.const 104
    i32.const 2556
    i32.const 3
    call $kotlin.stringLiteral
    i32.const 18
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_DOM_instance
    ref.null none
    i32.const 105
    i32.const 2562
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 19
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_DQUOT_instance
    ref.null none
    i32.const 106
    i32.const 2572
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 20
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_EXIST_instance
    ref.null none
    i32.const 107
    i32.const 2582
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 21
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_FAULT_instance
    ref.null none
    i32.const 108
    i32.const 2592
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 22
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_FBIG_instance
    ref.null none
    i32.const 109
    i32.const 2600
    i32.const 11
    call $kotlin.stringLiteral
    i32.const 23
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_HOSTUNREACH_instance
    ref.null none
    i32.const 110
    i32.const 2622
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 24
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_IDRM_instance
    ref.null none
    i32.const 111
    i32.const 2630
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 25
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ILSEQ_instance
    ref.null none
    i32.const 112
    i32.const 2640
    i32.const 10
    call $kotlin.stringLiteral
    i32.const 26
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_INPROGRESS_instance
    ref.null none
    i32.const 113
    i32.const 2660
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 27
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_INTR_instance
    ref.null none
    i32.const 114
    i32.const 2668
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 28
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_INVAL_instance
    ref.null none
    i32.const 115
    i32.const 2678
    i32.const 2
    call $kotlin.stringLiteral
    i32.const 29
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_IO_instance
    ref.null none
    i32.const 116
    i32.const 2682
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 30
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ISCONN_instance
    ref.null none
    i32.const 117
    i32.const 2694
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 31
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ISDIR_instance
    ref.null none
    i32.const 118
    i32.const 2704
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 32
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_LOOP_instance
    ref.null none
    i32.const 119
    i32.const 2712
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 33
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_MFILE_instance
    ref.null none
    i32.const 120
    i32.const 2722
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 34
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_MLINK_instance
    ref.null none
    i32.const 121
    i32.const 2732
    i32.const 7
    call $kotlin.stringLiteral
    i32.const 35
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_MSGSIZE_instance
    ref.null none
    i32.const 122
    i32.const 2746
    i32.const 8
    call $kotlin.stringLiteral
    i32.const 36
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_MULTIHOP_instance
    ref.null none
    i32.const 123
    i32.const 2762
    i32.const 11
    call $kotlin.stringLiteral
    i32.const 37
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NAMETOOLONG_instance
    ref.null none
    i32.const 124
    i32.const 2784
    i32.const 7
    call $kotlin.stringLiteral
    i32.const 38
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NETDOWN_instance
    ref.null none
    i32.const 125
    i32.const 2798
    i32.const 8
    call $kotlin.stringLiteral
    i32.const 39
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NETRESET_instance
    ref.null none
    i32.const 126
    i32.const 2814
    i32.const 10
    call $kotlin.stringLiteral
    i32.const 40
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NETUNREACH_instance
    ref.null none
    i32.const 127
    i32.const 2834
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 41
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NFILE_instance
    ref.null none
    i32.const 128
    i32.const 2844
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 42
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOBUFS_instance
    ref.null none
    i32.const 129
    i32.const 2856
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 43
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NODEV_instance
    ref.null none
    i32.const 130
    i32.const 2866
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 44
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOENT_instance
    ref.null none
    i32.const 131
    i32.const 2876
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 45
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOEXEC_instance
    ref.null none
    i32.const 132
    i32.const 2888
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 46
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOLCK_instance
    ref.null none
    i32.const 133
    i32.const 2898
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 47
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOLINK_instance
    ref.null none
    i32.const 134
    i32.const 2910
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 48
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOMEM_instance
    ref.null none
    i32.const 135
    i32.const 2920
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 49
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOMSG_instance
    ref.null none
    i32.const 136
    i32.const 2930
    i32.const 10
    call $kotlin.stringLiteral
    i32.const 50
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOPROTOOPT_instance
    ref.null none
    i32.const 137
    i32.const 2950
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 51
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOSPC_instance
    ref.null none
    i32.const 138
    i32.const 2960
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 52
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOSYS_instance
    ref.null none
    i32.const 139
    i32.const 2970
    i32.const 7
    call $kotlin.stringLiteral
    i32.const 53
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTCONN_instance
    ref.null none
    i32.const 140
    i32.const 2984
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 54
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTDIR_instance
    ref.null none
    i32.const 141
    i32.const 2996
    i32.const 8
    call $kotlin.stringLiteral
    i32.const 55
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTEMPTY_instance
    ref.null none
    i32.const 142
    i32.const 3012
    i32.const 14
    call $kotlin.stringLiteral
    i32.const 56
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTRECOVERABLE_instance
    ref.null none
    i32.const 143
    i32.const 3040
    i32.const 7
    call $kotlin.stringLiteral
    i32.const 57
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTSOCK_instance
    ref.null none
    i32.const 144
    i32.const 3054
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 58
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTSUP_instance
    ref.null none
    i32.const 145
    i32.const 3066
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 59
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTTY_instance
    ref.null none
    i32.const 146
    i32.const 3076
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 60
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NXIO_instance
    ref.null none
    i32.const 147
    i32.const 3084
    i32.const 8
    call $kotlin.stringLiteral
    i32.const 61
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_OVERFLOW_instance
    ref.null none
    i32.const 148
    i32.const 3100
    i32.const 9
    call $kotlin.stringLiteral
    i32.const 62
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_OWNERDEAD_instance
    ref.null none
    i32.const 149
    i32.const 3118
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 63
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_PERM_instance
    ref.null none
    i32.const 150
    i32.const 3126
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 64
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_PIPE_instance
    ref.null none
    i32.const 151
    i32.const 3134
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 65
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_PROTO_instance
    ref.null none
    i32.const 152
    i32.const 3144
    i32.const 14
    call $kotlin.stringLiteral
    i32.const 66
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_PROTONOSUPPORT_instance
    ref.null none
    i32.const 153
    i32.const 3172
    i32.const 9
    call $kotlin.stringLiteral
    i32.const 67
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_PROTOTYPE_instance
    ref.null none
    i32.const 154
    i32.const 3190
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 68
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_RANGE_instance
    ref.null none
    i32.const 155
    i32.const 3200
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 69
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_ROFS_instance
    ref.null none
    i32.const 156
    i32.const 3208
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 70
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_SPIPE_instance
    ref.null none
    i32.const 157
    i32.const 3218
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 71
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_SRCH_instance
    ref.null none
    i32.const 158
    i32.const 3226
    i32.const 5
    call $kotlin.stringLiteral
    i32.const 72
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_STALE_instance
    ref.null none
    i32.const 159
    i32.const 3236
    i32.const 8
    call $kotlin.stringLiteral
    i32.const 73
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_TIMEDOUT_instance
    ref.null none
    i32.const 160
    i32.const 3252
    i32.const 6
    call $kotlin.stringLiteral
    i32.const 74
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_TXTBSY_instance
    ref.null none
    i32.const 161
    i32.const 3264
    i32.const 4
    call $kotlin.stringLiteral
    i32.const 75
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_XDEV_instance
    ref.null none
    i32.const 162
    i32.const 3272
    i32.const 10
    call $kotlin.stringLiteral
    i32.const 76
    call $kotlin.wasm.WasiErrorCode.<init>
    global.set $kotlin.wasm.WasiErrorCode_NOTCAPABLE_instance
    nop
  )
  (func $kotlin.wasm.WasiErrorCode.<init> (;205;) (type $"#type266 ") (param $<this> (ref null $kotlin.wasm.WasiErrorCode)) (param $name (ref null $kotlin.String)) (param $ordinal i32) (result (ref null $kotlin.wasm.WasiErrorCode))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $kotlin.wasm.WasiErrorCode.vtable
      global.get $kotlin.wasm.WasiErrorCode.classITable
      i32.const 1900
      i32.const 0
      ref.null $kotlin.String
      i32.const 0
      struct.new $kotlin.wasm.WasiErrorCode
      local.set $<this>
    end
    local.get $<this>
    local.get $name
    local.get $ordinal
    call $kotlin.Enum.<init>
    drop
    nop
    local.get $<this>
    return
  )
  (func $kotlin.wasm.WasiErrorCode_SUCCESS_getInstance (;206;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_SUCCESS_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode__2BIG_getInstance (;207;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode__2BIG_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ACCES_getInstance (;208;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ACCES_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ADDRINUSE_getInstance (;209;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ADDRINUSE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ADDRNOTAVAIL_getInstance (;210;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ADDRNOTAVAIL_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_AFNOSUPPORT_getInstance (;211;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_AFNOSUPPORT_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_AGAIN_getInstance (;212;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_AGAIN_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ALREADY_getInstance (;213;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ALREADY_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_BADF_getInstance (;214;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_BADF_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_BADMSG_getInstance (;215;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_BADMSG_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_BUSY_getInstance (;216;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_BUSY_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_CANCELED_getInstance (;217;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_CANCELED_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_CHILD_getInstance (;218;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_CHILD_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_CONNABORTED_getInstance (;219;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_CONNABORTED_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_CONNREFUSED_getInstance (;220;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_CONNREFUSED_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_CONNRESET_getInstance (;221;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_CONNRESET_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_DEADLK_getInstance (;222;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_DEADLK_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_DESTADDRREQ_getInstance (;223;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_DESTADDRREQ_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_DOM_getInstance (;224;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_DOM_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_DQUOT_getInstance (;225;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_DQUOT_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_EXIST_getInstance (;226;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_EXIST_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_FAULT_getInstance (;227;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_FAULT_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_FBIG_getInstance (;228;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_FBIG_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_HOSTUNREACH_getInstance (;229;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_HOSTUNREACH_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_IDRM_getInstance (;230;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_IDRM_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ILSEQ_getInstance (;231;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ILSEQ_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_INPROGRESS_getInstance (;232;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_INPROGRESS_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_INTR_getInstance (;233;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_INTR_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_INVAL_getInstance (;234;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_INVAL_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_IO_getInstance (;235;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_IO_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ISCONN_getInstance (;236;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ISCONN_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ISDIR_getInstance (;237;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ISDIR_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_LOOP_getInstance (;238;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_LOOP_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_MFILE_getInstance (;239;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_MFILE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_MLINK_getInstance (;240;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_MLINK_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_MSGSIZE_getInstance (;241;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_MSGSIZE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_MULTIHOP_getInstance (;242;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_MULTIHOP_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NAMETOOLONG_getInstance (;243;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NAMETOOLONG_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NETDOWN_getInstance (;244;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NETDOWN_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NETRESET_getInstance (;245;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NETRESET_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NETUNREACH_getInstance (;246;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NETUNREACH_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NFILE_getInstance (;247;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NFILE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOBUFS_getInstance (;248;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOBUFS_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NODEV_getInstance (;249;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NODEV_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOENT_getInstance (;250;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOENT_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOEXEC_getInstance (;251;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOEXEC_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOLCK_getInstance (;252;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOLCK_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOLINK_getInstance (;253;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOLINK_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOMEM_getInstance (;254;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOMEM_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOMSG_getInstance (;255;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOMSG_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOPROTOOPT_getInstance (;256;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOPROTOOPT_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOSPC_getInstance (;257;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOSPC_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOSYS_getInstance (;258;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOSYS_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTCONN_getInstance (;259;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTCONN_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTDIR_getInstance (;260;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTDIR_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTEMPTY_getInstance (;261;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTEMPTY_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTRECOVERABLE_getInstance (;262;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTRECOVERABLE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTSOCK_getInstance (;263;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTSOCK_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTSUP_getInstance (;264;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTSUP_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTTY_getInstance (;265;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTTY_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NXIO_getInstance (;266;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NXIO_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_OVERFLOW_getInstance (;267;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_OVERFLOW_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_OWNERDEAD_getInstance (;268;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_OWNERDEAD_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_PERM_getInstance (;269;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_PERM_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_PIPE_getInstance (;270;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_PIPE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_PROTO_getInstance (;271;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_PROTO_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_PROTONOSUPPORT_getInstance (;272;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_PROTONOSUPPORT_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_PROTOTYPE_getInstance (;273;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_PROTOTYPE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_RANGE_getInstance (;274;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_RANGE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_ROFS_getInstance (;275;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_ROFS_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_SPIPE_getInstance (;276;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_SPIPE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_SRCH_getInstance (;277;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_SRCH_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_STALE_getInstance (;278;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_STALE_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_TIMEDOUT_getInstance (;279;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_TIMEDOUT_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_TXTBSY_getInstance (;280;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_TXTBSY_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_XDEV_getInstance (;281;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_XDEV_instance
    return
  )
  (func $kotlin.wasm.WasiErrorCode_NOTCAPABLE_getInstance (;282;) (type $"#type267 ") (result (ref null $kotlin.wasm.WasiErrorCode))
    call $kotlin.wasm.WasiErrorCode_initEntries
    global.get $kotlin.wasm.WasiErrorCode_NOTCAPABLE_instance
    return
  )
  (func $kotlin.wasm.internal.invokeOnExportedFunctionExit (;283;) (type 6)
    (local $tmp0_safe_receiver (ref null $kotlin.Any))
    global.get $kotlin.wasm.internal.onExportedFunctionExit
    local.tee $tmp0_safe_receiver
    ref.is_null
    if ;; label = @1
    else
      local.get $tmp0_safe_receiver
      local.get $tmp0_safe_receiver
      struct.get $kotlin.Any $itable
      ref.cast (ref $"#type40 classITable")
      struct.get $"#type40 classITable" $"#field2 kotlin.itable"
      struct.get $kotlin.Function0.itable $invoke
      call_ref $"#type161 "
      ref.cast (ref null $kotlin.Unit)
      drop
    end
    nop
  )
  (func $kotlin.random.defaultPlatformRandom (;284;) (type $"#type268 ") (result (ref null $kotlin.random.Random))
    call $kotlin.random.wasiRandomGet
    call $kotlin.random.Random
    return
  )
  (func $kotlin.random.wasiRandomGet (;285;) (type 9) (result i64)
    (local $allocator (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) (local $result (ref null $kotlin.Any)) (local $tmp (ref null $kotlin.Any)) (local $"#local3 tmp" (@name "tmp") i64) (local $tmp0 (ref null $kotlin.wasm.unsafe.MemoryAllocator)) (local $"#local5 allocator" (@name "allocator") (ref null $kotlin.wasm.unsafe.MemoryAllocator)) (local $memory i32) (local $ret i32) (local $"#local8 tmp0" (@name "tmp0") i32) (local $this i32) (local $"#local10 tmp" (@name "tmp") (ref null $kotlin.Any))
    block ;; label = @1
      nop
      call $kotlin.wasm.unsafe.createAllocatorInTheNewScope
      local.set $allocator
      block (result (ref null $kotlin.Any)) ;; label = @2
        block (result i64) ;; label = @3
          block ;; label = @4
            block (result exnref) ;; label = @5
              try_table (result exnref) (catch_all_ref 0 (;@5;)) ;; label = @6
                local.get $allocator
                local.set $tmp0
                block (result (ref null $kotlin.Any)) ;; label = @7
                  nop
                  local.get $tmp0
                  local.tee $"#local5 allocator"
                  i32.const 8
                  local.get $"#local5 allocator"
                  struct.get $kotlin.wasm.unsafe.MemoryAllocator $vtable
                  struct.get $kotlin.wasm.unsafe.MemoryAllocator.vtable $allocate
                  call_ref $"#type154 "
                  local.tee $memory
                  call $kotlin.wasm.unsafe.<Pointer__<get-address>-impl>
                  local.set $"#local8 tmp0"
                  block (result i32) ;; label = @8
                    nop
                    local.get $"#local8 tmp0"
                    local.tee $this
                    call $kotlin.<UInt__<get-data>-impl>
                    br 0 (;@8;)
                  end
                  i32.const 8
                  call 0
                  local.tee $ret
                  i32.const 0
                  i32.eq
                  if (result i64) ;; label = @8
                    local.get $memory
                    i64.load align=1
                  else
                    ref.null none
                    call $kotlin.wasm.<get-entries>
                    local.tee $"#local10 tmp"
                    local.get $ret
                    local.get $"#local10 tmp"
                    struct.get $kotlin.Any $itable
                    ref.cast (ref $"#type38 classITable")
                    struct.get $"#type38 classITable" $"#field2 kotlin.collections.itable"
                    struct.get $kotlin.collections.List.itable $get
                    call_ref $"#type169 "
                    ref.cast (ref null $kotlin.wasm.WasiErrorCode)
                    call $kotlin.wasm.WasiError.<init>
                    throw 0
                  end
                  br 4 (;@3;)
                end
                br 4 (;@2;)
              end
            end
            local.get $allocator
            call $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy
            local.get $allocator
            struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $parent
            global.set $kotlin.wasm.unsafe.currentAllocator
            throw_ref
          end
          unreachable
        end
        local.set $"#local3 tmp"
        local.get $allocator
        call $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy
        local.get $allocator
        struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $parent
        global.set $kotlin.wasm.unsafe.currentAllocator
        local.get $"#local3 tmp"
        return
      end
      local.set $tmp
      local.get $allocator
      call $kotlin.wasm.unsafe.ScopedMemoryAllocator.destroy
      local.get $allocator
      struct.get $kotlin.wasm.unsafe.ScopedMemoryAllocator $parent
      global.set $kotlin.wasm.unsafe.currentAllocator
      local.get $tmp
      local.set $result
      unreachable
    end
    unreachable
  )
  (func $main (;286;) (type 6)
    (local $currentIsNotFirstWasmExportCall i32) (local $merged_catch_param (ref null $kotlin.Throwable)) (local $e (ref null $kotlin.RuntimeException))
    global.get $kotlin.wasm.internal.isNotFirstWasmExportCall
    local.set $currentIsNotFirstWasmExportCall
    block (result (ref null $kotlin.Unit)) ;; label = @1
      block ;; label = @2
        block (result exnref) ;; label = @3
          try_table (result exnref) (catch_all_ref 0 (;@3;)) ;; label = @4
            i32.const 1
            global.set $kotlin.wasm.internal.isNotFirstWasmExportCall
            block (result (ref null $kotlin.Unit)) ;; label = @5
              block (result (ref null $kotlin.Throwable)) ;; label = @6
                try_table (result (ref null $kotlin.Throwable)) (catch 0 0 (;@6;)) ;; label = @7
                  global.get $main$lambda_instance
                  call $myAll
                  global.get $kotlin.Unit_instance
                  br 2 (;@5;)
                end
              end
              local.tee $merged_catch_param
              ref.test (ref $kotlin.RuntimeException)
              if (result (ref null $kotlin.Unit)) ;; label = @6
                local.get $merged_catch_param
                ref.cast (ref null $kotlin.RuntimeException)
                local.set $e
                global.get $kotlin.Unit_instance
              else
                local.get $merged_catch_param
                throw 0
              end
            end
            br 3 (;@1;)
          end
        end
        local.get $currentIsNotFirstWasmExportCall
        global.set $kotlin.wasm.internal.isNotFirstWasmExportCall
        local.get $currentIsNotFirstWasmExportCall
        i32.eqz
        if ;; label = @3
          call $kotlin.wasm.internal.invokeOnExportedFunctionExit
        else
        end
        throw_ref
      end
      unreachable
    end
    drop
    local.get $currentIsNotFirstWasmExportCall
    global.set $kotlin.wasm.internal.isNotFirstWasmExportCall
    local.get $currentIsNotFirstWasmExportCall
    i32.eqz
    if ;; label = @1
      call $kotlin.wasm.internal.invokeOnExportedFunctionExit
    else
    end
    nop
  )
  (func $myAll (;287;) (type $"#type269 ") (param $body (ref null $kotlin.Any))
    (local $tmp0 (ref null $kotlin.Any)) (local $this (ref null $kotlin.Any)) (local $tmp1 (ref null $SoftFailure)) (local $sf (ref null $SoftFailure))
    ref.null none
    call $SoftFailure.<init>
    local.set $tmp0
    block (result (ref null $kotlin.Unit)) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      ref.cast (ref null $SoftFailure)
      local.set $tmp1
      block (result (ref null $kotlin.Any)) ;; label = @2
        nop
        local.get $tmp1
        local.set $sf
        block (result (ref null $kotlin.Unit)) ;; label = @3
          block ;; label = @4
            block (result exnref) ;; label = @5
              try_table (result exnref) (catch_all_ref 0 (;@5;)) ;; label = @6
                local.get $body
                local.get $body
                struct.get $kotlin.Any $itable
                ref.cast (ref $"#type40 classITable")
                struct.get $"#type40 classITable" $"#field2 kotlin.itable"
                struct.get $kotlin.Function0.itable $invoke
                call_ref $"#type161 "
                ref.cast (ref null $kotlin.Unit)
                br 3 (;@3;)
              end
            end
            local.get $sf
            call $SoftFailure.invoke
            throw_ref
          end
          unreachable
        end
        drop
        local.get $sf
        call $SoftFailure.invoke
        global.get $kotlin.Unit_instance
      end
      drop
      global.get $kotlin.Unit_instance
      br 0 (;@1;)
    end
    drop
    nop
  )
  (func $SoftFailure.<init> (;288;) (type $"#type270 ") (param $<this> (ref null $SoftFailure)) (result (ref null $SoftFailure))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $SoftFailure.vtable
      global.get $SoftFailure.classITable
      i32.const 1936
      i32.const 0
      ref.null $kotlin.Any
      struct.new $SoftFailure
      local.set $<this>
    end
    local.get $<this>
    ref.null none
    call $"#func20 kotlin.collections.ArrayList.<init>"
    struct.set $SoftFailure $failures
    nop
    local.get $<this>
    return
  )
  (func $SoftFailure.invoke (;289;) (type $"#type269 ") (param $<this> (ref null $kotlin.Any))
    (local $tmp0_<this> (ref null $SoftFailure)) (local $tmp0 (ref null $kotlin.Any)) (local $this (ref null $kotlin.Any))
    local.get $<this>
    ref.cast (ref null $SoftFailure)
    local.tee $tmp0_<this>
    struct.get $SoftFailure $failures
    local.set $tmp0
    block (result i32) ;; label = @1
      nop
      local.get $tmp0
      local.tee $this
      local.get $this
      struct.get $kotlin.Any $itable
      ref.cast (ref $"#type38 classITable")
      struct.get $"#type38 classITable" $"#field1 kotlin.collections.itable"
      struct.get $kotlin.collections.Collection.itable $isEmpty
      call_ref $"#type153 "
      i32.eqz
      br 0 (;@1;)
    end
    if ;; label = @1
    else
    end
    nop
  )
  (func $dummy (;290;) (type 6)
    nop
  )
  (func $main$lambda.<init> (;291;) (type $"#type271 ") (param $<this> (ref null $main$lambda)) (result (ref null $main$lambda))
    local.get $<this>
    ref.is_null
    if ;; label = @1
      global.get $main$lambda.vtable
      global.get $main$lambda.classITable
      i32.const 1972
      i32.const 0
      struct.new $main$lambda
      local.set $<this>
    end
    nop
    local.get $<this>
    return
  )
  (func $main$lambda.invoke (;292;) (type $"#type269 ") (param $<this> (ref null $kotlin.Any))
    ref.null none
    call $kotlin.RuntimeException.<init>
    throw 0
  )
  (func $"#func293 main$lambda.invoke" (@name "main$lambda.invoke") (;293;) (type $"#type161 ") (param $<this> (ref null $kotlin.Any)) (result (ref null $kotlin.Any))
    local.get $<this>
    call $main$lambda.invoke
    global.get $kotlin.Unit_instance
    return
  )
  (func $_fieldInitialize (;294;) (type 10)
    ref.null none
    i32.const 166
    call $kotlin.Array.<init>
    global.set $kotlin.wasm.internal.stringPool
    ref.null none
    call $main$lambda.<init>
    global.set $main$lambda_instance
    ref.null none
    call $kotlin.Unit.<init>
    global.set $kotlin.Unit_instance
    ref.null none
    call $kotlin.assert$lambda.<init>
    global.set $kotlin.assert$lambda_instance
    ref.null none
    call $"#func122 kotlin.Companion.<init>"
    global.set $"#global9 kotlin.Companion_instance"
    ref.null none
    call $"#func114 kotlin.Companion.<init>"
    global.set $"#global8 kotlin.Companion_instance"
    ref.null none
    call $"#func110 kotlin.Companion.<init>"
    global.set $"#global7 kotlin.Companion_instance"
    ref.null none
    call $"#func108 kotlin.Companion.<init>"
    global.set $"#global6 kotlin.Companion_instance"
    ref.null none
    call $"#func94 kotlin.Companion.<init>"
    global.set $"#global5 kotlin.Companion_instance"
    ref.null none
    call $kotlin.Companion.<init>
    global.set $kotlin.Companion_instance
    ref.null none
    call $kotlin.random.Companion.<init>
    global.set $kotlin.random.Companion_instance
    ref.null none
    call $"#func55 kotlin.collections.Companion.<init>"
    global.set $"#global1 kotlin.collections.Companion_instance"
  )
  (func $_initialize (;295;) (type 10)
    call $kotlin.Unit_getInstance
    call $_fieldInitialize
    call $main
    return
  )
  (func $kotlin.test.startUnitTests (;296;) (type 10))
  (memory (;0;) 1)
  (tag (;0;) (type $"#type272 ") (param (ref null $kotlin.Throwable)))
  (global $kotlin.collections.Companion_instance (;0;) (mut (ref null $kotlin.collections.Companion)) ref.null $kotlin.collections.Companion)
  (global $"#global1 kotlin.collections.Companion_instance" (@name "kotlin.collections.Companion_instance") (;1;) (mut (ref null $"#type82 kotlin.collections.Companion")) ref.null $"#type82 kotlin.collections.Companion")
  (global $kotlin.random.Default_instance (;2;) (mut (ref null $kotlin.random.Default)) ref.null $kotlin.random.Default)
  (global $kotlin.random.Companion_instance (;3;) (mut (ref null $kotlin.random.Companion)) ref.null $kotlin.random.Companion)
  (global $kotlin.Companion_instance (;4;) (mut (ref null $kotlin.Companion)) ref.null $kotlin.Companion)
  (global $"#global5 kotlin.Companion_instance" (@name "kotlin.Companion_instance") (;5;) (mut (ref null $"#type87 kotlin.Companion")) ref.null $"#type87 kotlin.Companion")
  (global $"#global6 kotlin.Companion_instance" (@name "kotlin.Companion_instance") (;6;) (mut (ref null $"#type93 kotlin.Companion")) ref.null $"#type93 kotlin.Companion")
  (global $"#global7 kotlin.Companion_instance" (@name "kotlin.Companion_instance") (;7;) (mut (ref null $"#type95 kotlin.Companion")) ref.null $"#type95 kotlin.Companion")
  (global $"#global8 kotlin.Companion_instance" (@name "kotlin.Companion_instance") (;8;) (mut (ref null $"#type97 kotlin.Companion")) ref.null $"#type97 kotlin.Companion")
  (global $"#global9 kotlin.Companion_instance" (@name "kotlin.Companion_instance") (;9;) (mut (ref null $"#type98 kotlin.Companion")) ref.null $"#type98 kotlin.Companion")
  (global $kotlin.wasm.internal.isNotFirstWasmExportCall (;10;) (mut i32) i32.const 0)
  (global $kotlin.wasm.internal._K (;11;) (mut i32) i32.const 0)
  (global $kotlin.wasm.internal._exp (;12;) (mut i32) i32.const 0)
  (global $kotlin.wasm.internal._frc_minus (;13;) (mut i64) i64.const 0)
  (global $kotlin.wasm.internal._frc_plus (;14;) (mut i64) i64.const 0)
  (global $kotlin.wasm.internal._frc_pow (;15;) (mut i64) i64.const 0)
  (global $kotlin.wasm.internal._exp_pow (;16;) (mut i32) i32.const 0)
  (global $kotlin.wasm.internal.EXP_POWERS (;17;) (mut (ref null $kotlin.ShortArray)) ref.null $kotlin.ShortArray)
  (global $kotlin.wasm.internal.FRC_POWERS (;18;) (mut (ref null $kotlin.LongArray)) ref.null $kotlin.LongArray)
  (global $kotlin.wasm.internal.CharCodes_PLUS_instance (;19;) (mut (ref null $kotlin.wasm.internal.CharCodes)) ref.null $kotlin.wasm.internal.CharCodes)
  (global $kotlin.wasm.internal.CharCodes_MINUS_instance (;20;) (mut (ref null $kotlin.wasm.internal.CharCodes)) ref.null $kotlin.wasm.internal.CharCodes)
  (global $kotlin.wasm.internal.CharCodes_DOT_instance (;21;) (mut (ref null $kotlin.wasm.internal.CharCodes)) ref.null $kotlin.wasm.internal.CharCodes)
  (global $kotlin.wasm.internal.CharCodes__0_instance (;22;) (mut (ref null $kotlin.wasm.internal.CharCodes)) ref.null $kotlin.wasm.internal.CharCodes)
  (global $kotlin.wasm.internal.CharCodes_e_instance (;23;) (mut (ref null $kotlin.wasm.internal.CharCodes)) ref.null $kotlin.wasm.internal.CharCodes)
  (global $kotlin.wasm.internal.CharCodes_entriesInitialized (;24;) (mut i32) i32.const 0)
  (global $"kotlin.wasm.internal.properties initialized Number2String.kt" (;25;) (mut i32) i32.const 0)
  (global $kotlin.wasm.internal.stringPool (;26;) (mut (ref null $kotlin.Array)) ref.null $kotlin.Array)
  (global $kotlin.assert$lambda_instance (;27;) (mut (ref null $kotlin.assert$lambda)) ref.null $kotlin.assert$lambda)
  (global $kotlin.Unit_instance (;28;) (mut (ref null $kotlin.Unit)) ref.null $kotlin.Unit)
  (global $kotlin.wasm.unsafe.currentAllocator (;29;) (mut (ref null $kotlin.wasm.unsafe.ScopedMemoryAllocator)) ref.null none)
  (global $kotlin.wasm.WasiErrorCode_SUCCESS_instance (;30;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode__2BIG_instance (;31;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ACCES_instance (;32;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ADDRINUSE_instance (;33;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ADDRNOTAVAIL_instance (;34;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_AFNOSUPPORT_instance (;35;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_AGAIN_instance (;36;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ALREADY_instance (;37;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_BADF_instance (;38;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_BADMSG_instance (;39;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_BUSY_instance (;40;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_CANCELED_instance (;41;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_CHILD_instance (;42;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_CONNABORTED_instance (;43;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_CONNREFUSED_instance (;44;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_CONNRESET_instance (;45;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_DEADLK_instance (;46;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_DESTADDRREQ_instance (;47;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_DOM_instance (;48;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_DQUOT_instance (;49;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_EXIST_instance (;50;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_FAULT_instance (;51;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_FBIG_instance (;52;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_HOSTUNREACH_instance (;53;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_IDRM_instance (;54;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ILSEQ_instance (;55;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_INPROGRESS_instance (;56;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_INTR_instance (;57;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_INVAL_instance (;58;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_IO_instance (;59;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ISCONN_instance (;60;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ISDIR_instance (;61;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_LOOP_instance (;62;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_MFILE_instance (;63;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_MLINK_instance (;64;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_MSGSIZE_instance (;65;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_MULTIHOP_instance (;66;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NAMETOOLONG_instance (;67;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NETDOWN_instance (;68;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NETRESET_instance (;69;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NETUNREACH_instance (;70;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NFILE_instance (;71;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOBUFS_instance (;72;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NODEV_instance (;73;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOENT_instance (;74;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOEXEC_instance (;75;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOLCK_instance (;76;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOLINK_instance (;77;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOMEM_instance (;78;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOMSG_instance (;79;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOPROTOOPT_instance (;80;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOSPC_instance (;81;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOSYS_instance (;82;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTCONN_instance (;83;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTDIR_instance (;84;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTEMPTY_instance (;85;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTRECOVERABLE_instance (;86;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTSOCK_instance (;87;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTSUP_instance (;88;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTTY_instance (;89;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NXIO_instance (;90;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_OVERFLOW_instance (;91;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_OWNERDEAD_instance (;92;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_PERM_instance (;93;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_PIPE_instance (;94;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_PROTO_instance (;95;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_PROTONOSUPPORT_instance (;96;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_PROTOTYPE_instance (;97;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_RANGE_instance (;98;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_ROFS_instance (;99;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_SPIPE_instance (;100;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_SRCH_instance (;101;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_STALE_instance (;102;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_TIMEDOUT_instance (;103;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_TXTBSY_instance (;104;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_XDEV_instance (;105;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_NOTCAPABLE_instance (;106;) (mut (ref null $kotlin.wasm.WasiErrorCode)) ref.null $kotlin.wasm.WasiErrorCode)
  (global $kotlin.wasm.WasiErrorCode_entriesInitialized (;107;) (mut i32) i32.const 0)
  (global $kotlin.wasm.$ENTRIES (;108;) (mut (ref null $kotlin.Any)) ref.null $kotlin.Any)
  (global $kotlin.wasm.internal.onExportedFunctionExit (;109;) (mut (ref null $kotlin.Any)) ref.null none)
  (global $kotlin.collections.IteratorImpl.vtable (;110;) (ref $kotlin.collections.IteratorImpl.vtable) ref.func $kotlin.Any.toString ref.func $kotlin.collections.IteratorImpl.hasNext ref.func $kotlin.collections.IteratorImpl.next struct.new $kotlin.collections.IteratorImpl.vtable)
  (global $kotlin.collections.Companion.vtable (;111;) (ref $kotlin.collections.Companion.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.collections.Companion.vtable)
  (global $kotlin.collections.Itr.vtable (;112;) (ref $kotlin.collections.Itr.vtable) ref.func $kotlin.Any.toString ref.func $kotlin.collections.Itr.hasNext ref.func $kotlin.collections.Itr.next struct.new $kotlin.collections.Itr.vtable)
  (global $kotlin.collections.ArrayList.vtable (;113;) (ref $kotlin.collections.ArrayList.vtable) ref.func $kotlin.collections.ArrayList.toString ref.func $kotlin.collections.ArrayList.<get-size> ref.func $"#func25 kotlin.collections.ArrayList.iterator" ref.func $kotlin.collections.ArrayList.isEmpty ref.func $kotlin.collections.ArrayList.iterator ref.func $kotlin.collections.ArrayList.get ref.func $kotlin.collections.ArrayList.listIterator struct.new $kotlin.collections.ArrayList.vtable)
  (global $kotlin.text.StringBuilder.vtable (;114;) (ref $kotlin.text.StringBuilder.vtable) ref.func $kotlin.text.StringBuilder.toString ref.func $kotlin.text.StringBuilder.<get-length> ref.func $kotlin.text.StringBuilder.get ref.func $kotlin.text.StringBuilder.append ref.func $"#func34 kotlin.text.StringBuilder.append" ref.func $"#func35 kotlin.text.StringBuilder.append" ref.func $"#func36 kotlin.text.StringBuilder.append" ref.func $"#func37 kotlin.text.StringBuilder.append" struct.new $kotlin.text.StringBuilder.vtable)
  (global $kotlin.collections.AbstractCollection$toString$lambda.vtable (;115;) (ref $kotlin.collections.AbstractCollection$toString$lambda.vtable) ref.func $kotlin.Any.toString ref.func $kotlin.collections.AbstractCollection$toString$lambda.invoke ref.func $"#func48 kotlin.collections.AbstractCollection$toString$lambda.invoke" struct.new $kotlin.collections.AbstractCollection$toString$lambda.vtable)
  (global $"#global116 kotlin.collections.IteratorImpl.vtable" (@name "kotlin.collections.IteratorImpl.vtable") (;116;) (ref $"#type50 kotlin.collections.IteratorImpl.vtable") ref.func $kotlin.Any.toString ref.func $"#func53 kotlin.collections.IteratorImpl.hasNext" ref.func $"#func54 kotlin.collections.IteratorImpl.next" struct.new $"#type50 kotlin.collections.IteratorImpl.vtable")
  (global $"#global117 kotlin.collections.Companion.vtable" (@name "kotlin.collections.Companion.vtable") (;117;) (ref $"#type51 kotlin.collections.Companion.vtable") ref.func $kotlin.Any.toString struct.new $"#type51 kotlin.collections.Companion.vtable")
  (global $kotlin.enums.EnumEntriesList.vtable (;118;) (ref $kotlin.enums.EnumEntriesList.vtable) ref.func $kotlin.collections.AbstractCollection.toString ref.func $kotlin.enums.EnumEntriesList.<get-size> ref.func $kotlin.collections.AbstractList.iterator ref.func $kotlin.collections.AbstractCollection.isEmpty ref.func $"#func67 kotlin.enums.EnumEntriesList.get" ref.func $kotlin.enums.EnumEntriesList.get struct.new $kotlin.enums.EnumEntriesList.vtable)
  (global $kotlin.random.Default.vtable (;119;) (ref $kotlin.random.Default.vtable) ref.func $kotlin.Any.toString ref.func $kotlin.random.Default.nextBits ref.func $kotlin.random.Default.nextInt ref.func $"#func71 kotlin.random.Default.nextInt" struct.new $kotlin.random.Default.vtable)
  (global $kotlin.random.Companion.vtable (;120;) (ref $kotlin.random.Companion.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.random.Companion.vtable)
  (global $kotlin.random.XorWowRandom.vtable (;121;) (ref $kotlin.random.XorWowRandom.vtable) ref.func $kotlin.Any.toString ref.func $kotlin.random.XorWowRandom.nextBits ref.func $kotlin.random.XorWowRandom.nextInt ref.func $"#func75 kotlin.random.Random.nextInt" struct.new $kotlin.random.XorWowRandom.vtable)
  (global $kotlin.Companion.vtable (;122;) (ref $kotlin.Companion.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.Companion.vtable)
  (global $kotlin.UInt.vtable (;123;) (ref $kotlin.UInt.vtable) ref.func $kotlin.UInt.toString struct.new $kotlin.UInt.vtable)
  (global $"#global124 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;124;) (ref $"#type56 kotlin.Companion.vtable") ref.func $kotlin.Any.toString struct.new $"#type56 kotlin.Companion.vtable")
  (global $kotlin.ULong.vtable (;125;) (ref $kotlin.ULong.vtable) ref.func $kotlin.ULong.toString struct.new $kotlin.ULong.vtable)
  (global $kotlin.Any.vtable (;126;) (ref $kotlin.Any.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.Any.vtable)
  (global $kotlin.Array.vtable (;127;) (ref $kotlin.Array.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.Array.vtable)
  (global $kotlin.LongArray.vtable (;128;) (ref $kotlin.LongArray.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.LongArray.vtable)
  (global $kotlin.ShortArray.vtable (;129;) (ref $kotlin.ShortArray.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.ShortArray.vtable)
  (global $kotlin.CharArray.vtable (;130;) (ref $kotlin.CharArray.vtable) ref.func $kotlin.Any.toString struct.new $kotlin.CharArray.vtable)
  (global $"#global131 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;131;) (ref $"#type62 kotlin.Companion.vtable") ref.func $kotlin.Any.toString struct.new $"#type62 kotlin.Companion.vtable")
  (global $kotlin.Char.vtable (;132;) (ref $kotlin.Char.vtable) ref.func $kotlin.Char.toString struct.new $kotlin.Char.vtable)
  (global $"#global133 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;133;) (ref $"#type64 kotlin.Companion.vtable") ref.func $kotlin.Any.toString struct.new $"#type64 kotlin.Companion.vtable")
  (global $"#global134 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;134;) (ref $"#type66 kotlin.Companion.vtable") ref.func $kotlin.Any.toString struct.new $"#type66 kotlin.Companion.vtable")
  (global $kotlin.Int.vtable (;135;) (ref $kotlin.Int.vtable) ref.func $kotlin.Int.toString struct.new $kotlin.Int.vtable)
  (global $"#global136 kotlin.Companion.vtable" (@name "kotlin.Companion.vtable") (;136;) (ref $"#type67 kotlin.Companion.vtable") ref.func $kotlin.Any.toString struct.new $"#type67 kotlin.Companion.vtable")
  (global $kotlin.String.vtable (;137;) (ref $kotlin.String.vtable) ref.func $kotlin.String.toString ref.func $kotlin.String.<get-length> ref.func $kotlin.String.get struct.new $kotlin.String.vtable)
  (global $kotlin.wasm.internal.CharCodes.vtable (;138;) (ref $kotlin.wasm.internal.CharCodes.vtable) ref.func $kotlin.Enum.toString struct.new $kotlin.wasm.internal.CharCodes.vtable)
  (global $kotlin.assert$lambda.vtable (;139;) (ref $kotlin.assert$lambda.vtable) ref.func $kotlin.Any.toString ref.func $kotlin.assert$lambda.invoke ref.func $"#func155 kotlin.assert$lambda.invoke" struct.new $kotlin.assert$lambda.vtable)
  (global $kotlin.IllegalArgumentException.vtable (;140;) (ref $kotlin.IllegalArgumentException.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.IllegalArgumentException.vtable)
  (global $kotlin.NoSuchElementException.vtable (;141;) (ref $kotlin.NoSuchElementException.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.NoSuchElementException.vtable)
  (global $kotlin.IndexOutOfBoundsException.vtable (;142;) (ref $kotlin.IndexOutOfBoundsException.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.IndexOutOfBoundsException.vtable)
  (global $kotlin.AssertionError.vtable (;143;) (ref $kotlin.AssertionError.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.AssertionError.vtable)
  (global $kotlin.RuntimeException.vtable (;144;) (ref $kotlin.RuntimeException.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.RuntimeException.vtable)
  (global $kotlin.Error.vtable (;145;) (ref $kotlin.Error.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.Error.vtable)
  (global $kotlin.Exception.vtable (;146;) (ref $kotlin.Exception.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.Exception.vtable)
  (global $kotlin.IllegalStateException.vtable (;147;) (ref $kotlin.IllegalStateException.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.IllegalStateException.vtable)
  (global $kotlin.OutOfMemoryError.vtable (;148;) (ref $kotlin.OutOfMemoryError.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.OutOfMemoryError.vtable)
  (global $kotlin.ConcurrentModificationException.vtable (;149;) (ref $kotlin.ConcurrentModificationException.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.ConcurrentModificationException.vtable)
  (global $kotlin.Unit.vtable (;150;) (ref $kotlin.Unit.vtable) ref.func $kotlin.Unit.toString struct.new $kotlin.Unit.vtable)
  (global $kotlin.wasm.unsafe.Pointer.vtable (;151;) (ref $kotlin.wasm.unsafe.Pointer.vtable) ref.func $kotlin.wasm.unsafe.Pointer.toString struct.new $kotlin.wasm.unsafe.Pointer.vtable)
  (global $kotlin.wasm.unsafe.ScopedMemoryAllocator.vtable (;152;) (ref $kotlin.wasm.unsafe.ScopedMemoryAllocator.vtable) ref.func $kotlin.Any.toString ref.func $kotlin.wasm.unsafe.ScopedMemoryAllocator.allocate struct.new $kotlin.wasm.unsafe.ScopedMemoryAllocator.vtable)
  (global $kotlin.Throwable.vtable (;153;) (ref $kotlin.Throwable.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.Throwable.vtable)
  (global $kotlin.wasm.WasiError.vtable (;154;) (ref $kotlin.wasm.WasiError.vtable) ref.func $kotlin.Throwable.toString ref.func $kotlin.Throwable.<get-message> struct.new $kotlin.wasm.WasiError.vtable)
  (global $kotlin.wasm.WasiErrorCode.vtable (;155;) (ref $kotlin.wasm.WasiErrorCode.vtable) ref.func $kotlin.Enum.toString struct.new $kotlin.wasm.WasiErrorCode.vtable)
  (global $kotlin.collections.IteratorImpl.classITable (;156;) (ref $"#type39 classITable") ref.func $kotlin.collections.IteratorImpl.next ref.func $kotlin.collections.IteratorImpl.hasNext struct.new $kotlin.collections.Iterator.itable struct.new $kotlin.collections.MutableIterator.itable ref.null none ref.null none struct.new $"#type39 classITable")
  (global $kotlin.collections.Itr.classITable (;157;) (ref $"#type39 classITable") ref.func $kotlin.collections.Itr.next ref.func $kotlin.collections.Itr.hasNext struct.new $kotlin.collections.Iterator.itable struct.new $kotlin.collections.MutableIterator.itable struct.new $kotlin.collections.ListIterator.itable struct.new $kotlin.collections.MutableListIterator.itable struct.new $"#type39 classITable")
  (global $kotlin.collections.ArrayList.classITable (;158;) (ref $"#type38 classITable") ref.func $"#func25 kotlin.collections.ArrayList.iterator" struct.new $kotlin.collections.Iterable.itable ref.func $kotlin.collections.ArrayList.<get-size> ref.func $kotlin.collections.ArrayList.isEmpty struct.new $kotlin.collections.Collection.itable ref.func $kotlin.collections.ArrayList.<get-size> ref.func $kotlin.collections.ArrayList.isEmpty ref.func $"#func25 kotlin.collections.ArrayList.iterator" ref.func $kotlin.collections.ArrayList.get struct.new $kotlin.collections.List.itable struct.new $kotlin.collections.MutableIterable.itable ref.func $kotlin.collections.ArrayList.iterator struct.new $kotlin.collections.MutableCollection.itable struct.new $kotlin.collections.MutableList.itable struct.new $kotlin.collections.RandomAccess.itable ref.null none ref.null none struct.new $"#type38 classITable")
  (global $kotlin.text.StringBuilder.classITable (;159;) (ref $classITable) ref.func $kotlin.text.StringBuilder.<get-length> ref.func $kotlin.text.StringBuilder.get struct.new $kotlin.CharSequence.itable ref.func $"#func34 kotlin.text.StringBuilder.append" ref.func $"#func36 kotlin.text.StringBuilder.append" struct.new $kotlin.text.Appendable.itable ref.null none struct.new $classITable)
  (global $kotlin.collections.AbstractCollection$toString$lambda.classITable (;160;) (ref $"#type40 classITable") struct.new $kotlin.Function.itable ref.func $"#func48 kotlin.collections.AbstractCollection$toString$lambda.invoke" struct.new $kotlin.Function1.itable ref.null none struct.new $"#type40 classITable")
  (global $"#global161 kotlin.collections.IteratorImpl.classITable" (@name "kotlin.collections.IteratorImpl.classITable") (;161;) (ref $"#type39 classITable") ref.func $"#func54 kotlin.collections.IteratorImpl.next" ref.func $"#func53 kotlin.collections.IteratorImpl.hasNext" struct.new $kotlin.collections.Iterator.itable ref.null none ref.null none ref.null none struct.new $"#type39 classITable")
  (global $kotlin.enums.EnumEntriesList.classITable (;162;) (ref $"#type38 classITable") ref.func $kotlin.collections.AbstractList.iterator struct.new $kotlin.collections.Iterable.itable ref.func $kotlin.enums.EnumEntriesList.<get-size> ref.func $kotlin.collections.AbstractCollection.isEmpty struct.new $kotlin.collections.Collection.itable ref.func $kotlin.enums.EnumEntriesList.<get-size> ref.func $kotlin.collections.AbstractCollection.isEmpty ref.func $kotlin.collections.AbstractList.iterator ref.func $"#func67 kotlin.enums.EnumEntriesList.get" struct.new $kotlin.collections.List.itable ref.null none ref.null none ref.null none ref.null none struct.new $kotlin.enums.EnumEntries.itable struct.new $kotlin.io.Serializable.itable struct.new $"#type38 classITable")
  (global $kotlin.random.Default.classITable (;163;) (ref $"#type38 classITable") ref.null none ref.null none ref.null none ref.null none ref.null none ref.null none ref.null none ref.null none struct.new $kotlin.io.Serializable.itable struct.new $"#type38 classITable")
  (global $kotlin.random.XorWowRandom.classITable (;164;) (ref $"#type38 classITable") ref.null none ref.null none ref.null none ref.null none ref.null none ref.null none ref.null none ref.null none struct.new $kotlin.io.Serializable.itable struct.new $"#type38 classITable")
  (global $kotlin.UInt.classITable (;165;) (ref $classITable) ref.null none ref.null none struct.new $kotlin.Comparable.itable struct.new $classITable)
  (global $kotlin.ULong.classITable (;166;) (ref $classITable) ref.null none ref.null none struct.new $kotlin.Comparable.itable struct.new $classITable)
  (global $kotlin.Char.classITable (;167;) (ref $classITable) ref.null none ref.null none struct.new $kotlin.Comparable.itable struct.new $classITable)
  (global $kotlin.Int.classITable (;168;) (ref $classITable) ref.null none ref.null none struct.new $kotlin.Comparable.itable struct.new $classITable)
  (global $kotlin.String.classITable (;169;) (ref $classITable) ref.func $kotlin.String.<get-length> ref.func $kotlin.String.get struct.new $kotlin.CharSequence.itable ref.null none struct.new $kotlin.Comparable.itable struct.new $classITable)
  (global $kotlin.wasm.internal.CharCodes.classITable (;170;) (ref $classITable) ref.null none ref.null none struct.new $kotlin.Comparable.itable struct.new $classITable)
  (global $kotlin.assert$lambda.classITable (;171;) (ref $"#type40 classITable") struct.new $kotlin.Function.itable ref.null none ref.func $"#func155 kotlin.assert$lambda.invoke" struct.new $kotlin.Function0.itable struct.new $"#type40 classITable")
  (global $kotlin.wasm.WasiErrorCode.classITable (;172;) (ref $classITable) ref.null none ref.null none struct.new $kotlin.Comparable.itable struct.new $classITable)
  (global $main$lambda_instance (;173;) (mut (ref null $main$lambda)) ref.null $main$lambda)
  (global $SoftFailure.vtable (;174;) (ref $SoftFailure.vtable) ref.func $kotlin.Any.toString ref.func $SoftFailure.invoke struct.new $SoftFailure.vtable)
  (global $main$lambda.vtable (;175;) (ref $main$lambda.vtable) ref.func $kotlin.Any.toString ref.func $main$lambda.invoke ref.func $"#func293 main$lambda.invoke" struct.new $main$lambda.vtable)
  (global $SoftFailure.classITable (;176;) (ref $"#type42 classITable") struct.new $Failure.itable struct.new $"#type42 classITable")
  (global $main$lambda.classITable (;177;) (ref $"#type40 classITable") struct.new $kotlin.Function.itable ref.null none ref.func $"#func293 main$lambda.invoke" struct.new $kotlin.Function0.itable struct.new $"#type40 classITable")
  (export "main" (func $main))
  (export "dummy" (func $dummy))
  (export "memory" (memory 0))
  (export "_initialize" (func $_initialize))
  (export "startUnitTests" (func $kotlin.test.startUnitTests))
  (data (;0;) "N\00u\00m\00b\00e\00r\00,\00 \00.\00.\00.\00A\00b\00s\00t\00r\00a\00c\00t\00M\00u\00t\00a\00b\00l\00e\00C\00o\00l\00l\00e\00c\00t\00i\00o\00n\00I\00t\00e\00r\00a\00t\00o\00r\00I\00m\00p\00l\00A\00b\00s\00t\00r\00a\00c\00t\00M\00u\00t\00a\00b\00l\00e\00L\00i\00s\00t\00C\00o\00m\00p\00a\00n\00i\00o\00n\00I\00t\00r\00A\00r\00r\00a\00y\00L\00i\00s\00t\00c\00a\00p\00a\00c\00i\00t\00y\00 \00m\00u\00s\00t\00 \00b\00e\00 \00n\00o\00n\00-\00n\00e\00g\00a\00t\00i\00v\00e\00.\00[\00(\00t\00h\00i\00s\00 \00C\00o\00l\00l\00e\00c\00t\00i\00o\00n\00)\00]\00S\00t\00r\00i\00n\00g\00B\00u\00i\00l\00d\00e\00r\00n\00u\00l\00l\00A\00b\00s\00t\00r\00a\00c\00t\00C\00o\00l\00l\00e\00c\00t\00i\00o\00n\00$\00t\00o\00S\00t\00r\00i\00n\00g\00$\00l\00a\00m\00b\00d\00a\00A\00b\00s\00t\00r\00a\00c\00t\00C\00o\00l\00l\00e\00c\00t\00i\00o\00n\00i\00n\00d\00e\00x\00:\00 \00,\00 \00s\00i\00z\00e\00:\00 \00f\00r\00o\00m\00I\00n\00d\00e\00x\00:\00 \00,\00 \00t\00o\00I\00n\00d\00e\00x\00:\00 \00 \00>\00 \00t\00o\00I\00n\00d\00e\00x\00:\00 \00s\00t\00a\00r\00t\00I\00n\00d\00e\00x\00:\00 \00,\00 \00e\00n\00d\00I\00n\00d\00e\00x\00:\00 \00 \00>\00 \00e\00n\00d\00I\00n\00d\00e\00x\00:\00 \00A\00b\00s\00t\00r\00a\00c\00t\00L\00i\00s\00t\00E\00n\00u\00m\00E\00n\00t\00r\00i\00e\00s\00L\00i\00s\00t\00D\00e\00f\00a\00u\00l\00t\00R\00a\00n\00d\00o\00m\00R\00a\00n\00d\00o\00m\00 \00r\00a\00n\00g\00e\00 \00i\00s\00 \00e\00m\00p\00t\00y\00:\00 \00[\00)\00.\00X\00o\00r\00W\00o\00w\00R\00a\00n\00d\00o\00m\00I\00n\00i\00t\00i\00a\00l\00 \00s\00t\00a\00t\00e\00 \00m\00u\00s\00t\00 \00h\00a\00v\00e\00 \00a\00t\00 \00l\00e\00a\00s\00t\00 \00o\00n\00e\00 \00n\00o\00n\00-\00z\00e\00r\00o\00 \00e\00l\00e\00m\00e\00n\00t\00.\00U\00I\00n\00t\00U\00L\00o\00n\00g\00A\00n\00y\00.\00@\00A\00r\00r\00a\00y\00N\00e\00g\00a\00t\00i\00v\00e\00 \00a\00r\00r\00a\00y\00 \00s\00i\00z\00e\00L\00o\00n\00g\00A\00r\00r\00a\00y\00S\00h\00o\00r\00t\00A\00r\00r\00a\00y\00C\00h\00a\00r\00A\00r\00r\00a\00y\00C\00h\00a\00r\00E\00n\00u\00m\00I\00n\00t\00S\00t\00r\00i\00n\00g\00C\00h\00e\00c\00k\00 \00f\00a\00i\00l\00e\00d\00.\000\00-\00P\00L\00U\00S\00M\00I\00N\00U\00S\00D\00O\00T\00_\000\00e\00C\00h\00a\00r\00C\00o\00d\00e\00s\00 \00>\00 \00a\00s\00s\00e\00r\00t\00$\00l\00a\00m\00b\00d\00a\00A\00s\00s\00e\00r\00t\00i\00o\00n\00 \00f\00a\00i\00l\00e\00d\00I\00l\00l\00e\00g\00a\00l\00A\00r\00g\00u\00m\00e\00n\00t\00E\00x\00c\00e\00p\00t\00i\00o\00n\00N\00o\00S\00u\00c\00h\00E\00l\00e\00m\00e\00n\00t\00E\00x\00c\00e\00p\00t\00i\00o\00n\00I\00n\00d\00e\00x\00O\00u\00t\00O\00f\00B\00o\00u\00n\00d\00s\00E\00x\00c\00e\00p\00t\00i\00o\00n\00A\00s\00s\00e\00r\00t\00i\00o\00n\00E\00r\00r\00o\00r\00R\00u\00n\00t\00i\00m\00e\00E\00x\00c\00e\00p\00t\00i\00o\00n\00E\00r\00r\00o\00r\00E\00x\00c\00e\00p\00t\00i\00o\00n\00I\00l\00l\00e\00g\00a\00l\00S\00t\00a\00t\00e\00E\00x\00c\00e\00p\00t\00i\00o\00n\00O\00u\00t\00O\00f\00M\00e\00m\00o\00r\00y\00E\00r\00r\00o\00r\00C\00o\00n\00c\00u\00r\00r\00e\00n\00t\00M\00o\00d\00i\00f\00i\00c\00a\00t\00i\00o\00n\00E\00x\00c\00e\00p\00t\00i\00o\00n\00U\00n\00i\00t\00k\00o\00t\00l\00i\00n\00.\00U\00n\00i\00t\00P\00o\00i\00n\00t\00e\00r\00(\00a\00d\00d\00r\00e\00s\00s\00=\00)\00P\00o\00i\00n\00t\00e\00r\00S\00c\00o\00p\00e\00d\00M\00e\00m\00o\00r\00y\00A\00l\00l\00o\00c\00a\00t\00o\00r\00S\00c\00o\00p\00e\00d\00M\00e\00m\00o\00r\00y\00A\00l\00l\00o\00c\00a\00t\00o\00r\00 \00i\00s\00 \00d\00e\00s\00t\00r\00o\00y\00e\00d\00 \00w\00h\00e\00n\00 \00o\00u\00t\00 \00o\00f\00 \00s\00c\00o\00p\00e\00S\00c\00o\00p\00e\00d\00M\00e\00m\00o\00r\00y\00A\00l\00l\00o\00c\00a\00t\00o\00r\00 \00i\00s\00 \00s\00u\00s\00p\00e\00n\00d\00e\00d\00 \00w\00h\00e\00n\00 \00n\00e\00s\00t\00e\00d\00 \00a\00l\00l\00o\00c\00a\00t\00o\00r\00s\00 \00a\00r\00e\00 \00u\00s\00e\00d\00r\00e\00s\00u\00l\00t\00 \00m\00u\00s\00t\00 \00b\00e\00 \00>\00 \000\00 \00a\00n\00d\00 \008\00-\00b\00y\00t\00e\00 \00a\00l\00i\00g\00n\00e\00d\00O\00u\00t\00 \00o\00f\00 \00l\00i\00n\00e\00a\00r\00 \00m\00e\00m\00o\00r\00y\00.\00 \00A\00l\00l\00 \00a\00v\00a\00i\00l\00a\00b\00l\00e\00 \00a\00d\00d\00r\00e\00s\00s\00 \00s\00p\00a\00c\00e\00 \00(\002\00g\00b\00)\00 \00i\00s\00 \00u\00s\00e\00d\00.\00O\00u\00t\00 \00o\00f\00 \00l\00i\00n\00e\00a\00r\00 \00m\00e\00m\00o\00r\00y\00.\00 \00m\00e\00m\00o\00r\00y\00.\00g\00r\00o\00w\00 \00r\00e\00t\00u\00r\00n\00e\00d\00 \00-\001\00M\00e\00m\00o\00r\00y\00A\00l\00l\00o\00c\00a\00t\00o\00r\00T\00h\00r\00o\00w\00a\00b\00l\00e\00:\00 \00W\00a\00s\00i\00E\00r\00r\00o\00r\00W\00A\00S\00I\00 \00c\00a\00l\00l\00 \00f\00a\00i\00l\00e\00d\00 \00w\00i\00t\00h\00 \00S\00U\00C\00C\00E\00S\00S\00_\002\00B\00I\00G\00A\00C\00C\00E\00S\00A\00D\00D\00R\00I\00N\00U\00S\00E\00A\00D\00D\00R\00N\00O\00T\00A\00V\00A\00I\00L\00A\00F\00N\00O\00S\00U\00P\00P\00O\00R\00T\00A\00G\00A\00I\00N\00A\00L\00R\00E\00A\00D\00Y\00B\00A\00D\00F\00B\00A\00D\00M\00S\00G\00B\00U\00S\00Y\00C\00A\00N\00C\00E\00L\00E\00D\00C\00H\00I\00L\00D\00C\00O\00N\00N\00A\00B\00O\00R\00T\00E\00D\00C\00O\00N\00N\00R\00E\00F\00U\00S\00E\00D\00C\00O\00N\00N\00R\00E\00S\00E\00T\00D\00E\00A\00D\00L\00K\00D\00E\00S\00T\00A\00D\00D\00R\00R\00E\00Q\00D\00O\00M\00D\00Q\00U\00O\00T\00E\00X\00I\00S\00T\00F\00A\00U\00L\00T\00F\00B\00I\00G\00H\00O\00S\00T\00U\00N\00R\00E\00A\00C\00H\00I\00D\00R\00M\00I\00L\00S\00E\00Q\00I\00N\00P\00R\00O\00G\00R\00E\00S\00S\00I\00N\00T\00R\00I\00N\00V\00A\00L\00I\00O\00I\00S\00C\00O\00N\00N\00I\00S\00D\00I\00R\00L\00O\00O\00P\00M\00F\00I\00L\00E\00M\00L\00I\00N\00K\00M\00S\00G\00S\00I\00Z\00E\00M\00U\00L\00T\00I\00H\00O\00P\00N\00A\00M\00E\00T\00O\00O\00L\00O\00N\00G\00N\00E\00T\00D\00O\00W\00N\00N\00E\00T\00R\00E\00S\00E\00T\00N\00E\00T\00U\00N\00R\00E\00A\00C\00H\00N\00F\00I\00L\00E\00N\00O\00B\00U\00F\00S\00N\00O\00D\00E\00V\00N\00O\00E\00N\00T\00N\00O\00E\00X\00E\00C\00N\00O\00L\00C\00K\00N\00O\00L\00I\00N\00K\00N\00O\00M\00E\00M\00N\00O\00M\00S\00G\00N\00O\00P\00R\00O\00T\00O\00O\00P\00T\00N\00O\00S\00P\00C\00N\00O\00S\00Y\00S\00N\00O\00T\00C\00O\00N\00N\00N\00O\00T\00D\00I\00R\00N\00O\00T\00E\00M\00P\00T\00Y\00N\00O\00T\00R\00E\00C\00O\00V\00E\00R\00A\00B\00L\00E\00N\00O\00T\00S\00O\00C\00K\00N\00O\00T\00S\00U\00P\00N\00O\00T\00T\00Y\00N\00X\00I\00O\00O\00V\00E\00R\00F\00L\00O\00W\00O\00W\00N\00E\00R\00D\00E\00A\00D\00P\00E\00R\00M\00P\00I\00P\00E\00P\00R\00O\00T\00O\00P\00R\00O\00T\00O\00N\00O\00S\00U\00P\00P\00O\00R\00T\00P\00R\00O\00T\00O\00T\00Y\00P\00E\00R\00A\00N\00G\00E\00R\00O\00F\00S\00S\00P\00I\00P\00E\00S\00R\00C\00H\00S\00T\00A\00L\00E\00T\00I\00M\00E\00D\00O\00U\00T\00T\00X\00T\00B\00S\00Y\00X\00D\00E\00V\00N\00O\00T\00C\00A\00P\00A\00B\00L\00E\00W\00a\00s\00i\00E\00r\00r\00o\00r\00C\00o\00d\00e\00S\00o\00f\00t\00F\00a\00i\00l\00u\00r\00e\00m\00a\00i\00n\00$\00l\00a\00m\00b\00d\00a\00")
  (data (;1;) "<\fbW\fbr\fb\8c\fb\a7\fb\c1\fb\dc\fb\f6\fb\11\fc,\fcF\fca\fc{\fc\96\fc\b1\fc\cb\fc\e6\fc\00\fd\1b\fd5\fdP\fdk\fd\85\fd\a0\fd\ba\fd\d5\fd\ef\fd\0a\fe%\fe?\feZ\fet\fe\8f\fe\a9\fe\c4\fe\df\fe\f9\fe\14\ff.\ffI\ffc\ff~\ff\99\ff\b3\ff\ce\ff\e8\ff\03\00\1e\008\00S\00m\00\88\00\a2\00\bd\00\d8\00\f2\00\0d\01'\01B\01\5c\01w\01\92\01\ac\01\c7\01\e1\01\fc\01\16\021\02L\02f\02\81\02\9b\02\b6\02\d0\02\eb\02\06\03 \03;\03U\03p\03\8b\03\a5\03\c0\03\da\03\f5\03\0f\04*\04")
  (data (;2;) (i32.const 0) "\00\00\00\00\00\00\00\00\00\00\00\00\06\00\00\00\01\00\00\00\00\00\00\00x\03\00\00")
  (data (;3;) (i32.const 28) "\00\00\00\00\00\00\00\00\00\00\00\00\19\00\00\00\04\00\00\00\16\00\00\00\a8\01\00\00")
  (data (;4;) (i32.const 56) "\00\00\00\00\00\00\00\00\00\00\00\00\0c\00\00\00\05\00\00\00H\00\00\00x\03\00\00\02\00\00\00\00\00\00\00\ff\ff\ff\ff")
  (data (;5;) (i32.const 96) "\00\00\00\00\00\00\00\00\00\00\00\00\13\00\00\00\06\00\00\00`\00\00\00\1c\00\00\00")
  (data (;6;) (i32.const 124) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;7;) (i32.const 156) "\00\00\00\00\00\00\00\00\00\00\00\00\03\00\00\00\08\00\00\00\98\00\00\00x\03\00\00\05\00\00\00\00\00\00\00\fe\ff\ff\ff\00\00\00\00\ff\ff\ff\ff\fd\ff\ff\ff")
  (data (;8;) (i32.const 208) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\09\00\00\00\9e\00\00\00`\00\00\00\1a\00\00\00\fc\ff\ff\ff\fb\ff\ff\ff\fa\ff\ff\ff\fc\ff\ff\ff\fb\ff\ff\ff\fc\ff\ff\ff\f9\ff\ff\ff\f8\ff\ff\ff\f7\ff\ff\ff\f6\ff\ff\ff\fc\ff\ff\ff\fb\ff\ff\ff\fc\ff\ff\ff\f9\ff\ff\ff\f8\ff\ff\ff\fc\ff\ff\ff\fb\ff\ff\ff\fc\ff\ff\ff\fb\ff\ff\ff\fa\ff\ff\ff\fc\ff\ff\ff\fb\ff\ff\ff\fc\ff\ff\ff\f9\ff\ff\ff\f8\ff\ff\ff\f7\ff\ff\ff")
  (data (;9;) (i32.const 344) "\00\00\00\00\00\00\00\00\00\00\00\00\0d\00\00\00\0e\00\00\00\12\01\00\00x\03\00\00\02\00\00\00\f5\ff\ff\ff\f4\ff\ff\ff")
  (data (;10;) (i32.const 384) "\00\00\00\00\00\00\00\00\00\00\00\00\22\00\00\00\10\00\00\004\01\00\00x\03\00\00\02\00\00\00\f3\ff\ff\ff\f2\ff\ff\ff")
  (data (;11;) (i32.const 424) "\00\00\00\00\00\00\00\00\00\00\00\00\12\00\00\00\11\00\00\00x\01\00\00x\03\00\00")
  (data (;12;) (i32.const 452) "\00\00\00\00\00\00\00\00\00\00\00\00\0c\00\00\00\05\00\00\00H\00\00\00x\03\00\00\01\00\00\00\00\00\00\00")
  (data (;13;) (i32.const 488) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;14;) (i32.const 520) "\00\00\00\00\00\00\00\00\00\00\00\00\0c\00\00\00\1a\00\00\00H\02\00\00\a8\01\00\00")
  (data (;15;) (i32.const 548) "\00\00\00\00\00\00\00\00\00\00\00\00\0f\00\00\00\1b\00\00\00`\02\00\00\08\02\00\00\0a\00\00\00\fc\ff\ff\ff\fb\ff\ff\ff\fa\ff\ff\ff\f1\ff\ff\ff\fc\ff\ff\ff\fb\ff\ff\ff\fc\ff\ff\ff\fb\ff\ff\ff\fa\ff\ff\ff\f0\ff\ff\ff")
  (data (;16;) (i32.const 620) "\00\00\00\00\00\00\00\00\00\00\00\00\07\00\00\00\1c\00\00\00~\02\00\00\90\02\00\00\01\00\00\00\f0\ff\ff\ff")
  (data (;17;) (i32.const 656) "\00\00\00\00\00\00\00\00\00\00\00\00\06\00\00\00\1d\00\00\00\8c\02\00\00x\03\00\00")
  (data (;18;) (i32.const 684) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;19;) (i32.const 716) "\00\00\00\00\00\00\00\00\00\00\00\00\0c\00\00\00 \00\00\00\cc\02\00\00\90\02\00\00\01\00\00\00\f0\ff\ff\ff")
  (data (;20;) (i32.const 752) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;21;) (i32.const 784) "\00\00\00\00\00\00\00\00\00\00\00\00\04\00\00\00\22\00\00\00P\03\00\00x\03\00\00\01\00\00\00\ef\ff\ff\ff")
  (data (;22;) (i32.const 820) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;23;) (i32.const 852) "\00\00\00\00\00\00\00\00\00\00\00\00\05\00\00\00#\00\00\00X\03\00\00x\03\00\00\01\00\00\00\ef\ff\ff\ff")
  (data (;24;) (i32.const 888) "\00\00\00\00\00\00\00\00\00\00\00\00\03\00\00\00$\00\00\00b\03\00\00\ff\ff\ff\ff\00\00\00\00")
  (data (;25;) (i32.const 920) "\00\00\00\00\00\00\00\00\00\00\00\00\05\00\00\00'\00\00\00l\03\00\00x\03\00\00\00\00\00\00")
  (data (;26;) (i32.const 952) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00)\00\00\00\9c\03\00\00x\03\00\00\00\00\00\00")
  (data (;27;) (i32.const 984) "\00\00\00\00\00\00\00\00\00\00\00\00\0a\00\00\00*\00\00\00\ae\03\00\00x\03\00\00\00\00\00\00")
  (data (;28;) (i32.const 1016) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00+\00\00\00\c2\03\00\00x\03\00\00\00\00\00\00")
  (data (;29;) (i32.const 1048) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;30;) (i32.const 1080) "\00\00\00\00\00\00\00\00\00\00\00\00\04\00\00\00,\00\00\00\d4\03\00\00x\03\00\00\01\00\00\00\ef\ff\ff\ff")
  (data (;31;) (i32.const 1116) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;32;) (i32.const 1148) "\00\00\00\00\00\00\00\00\00\00\00\00\04\00\00\00-\00\00\00\dc\03\00\00x\03\00\00")
  (data (;33;) (i32.const 1176) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;34;) (i32.const 1208) "\00\00\00\00\00\00\00\00\00\00\00\00\03\00\00\00.\00\00\00\e4\03\00\00\00\00\00\00\01\00\00\00\ef\ff\ff\ff")
  (data (;35;) (i32.const 1244) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00\07\00\00\00\86\00\00\00x\03\00\00\00\00\00\00")
  (data (;36;) (i32.const 1276) "\00\00\00\00\00\00\00\00\00\00\00\00\06\00\00\00/\00\00\00\ea\03\00\00x\03\00\00\02\00\00\00\ef\ff\ff\ff\f5\ff\ff\ff")
  (data (;37;) (i32.const 1316) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\008\00\00\002\04\00\00|\04\00\00\01\00\00\00\ef\ff\ff\ff")
  (data (;38;) (i32.const 1352) "\00\00\00\00\00\00\00\00\00\00\00\00\0d\00\00\00:\00\00\00J\04\00\00x\03\00\00\02\00\00\00\f3\ff\ff\ff\ee\ff\ff\ff")
  (data (;39;) (i32.const 1392) "\00\00\00\00\00\00\00\00\00\00\00\00\18\00\00\00<\00\00\00\84\04\00\00\f0\05\00\00\00\00\00\00")
  (data (;40;) (i32.const 1424) "\00\00\00\00\00\00\00\00\00\00\00\00\16\00\00\00=\00\00\00\b4\04\00\00\f0\05\00\00\00\00\00\00")
  (data (;41;) (i32.const 1456) "\00\00\00\00\00\00\00\00\00\00\00\00\19\00\00\00>\00\00\00\e0\04\00\00\f0\05\00\00\00\00\00\00")
  (data (;42;) (i32.const 1488) "\00\00\00\00\00\00\00\00\00\00\00\00\0e\00\00\00?\00\00\00\12\05\00\00\10\06\00\00\00\00\00\00")
  (data (;43;) (i32.const 1520) "\00\00\00\00\00\00\00\00\00\00\00\00\10\00\00\00@\00\00\00.\05\00\000\06\00\00\00\00\00\00")
  (data (;44;) (i32.const 1552) "\00\00\00\00\00\00\00\00\00\00\00\00\05\00\00\00A\00\00\00N\05\00\00,\07\00\00\00\00\00\00")
  (data (;45;) (i32.const 1584) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00B\00\00\00X\05\00\00,\07\00\00\00\00\00\00")
  (data (;46;) (i32.const 1616) "\00\00\00\00\00\00\00\00\00\00\00\00\15\00\00\00C\00\00\00j\05\00\00\f0\05\00\00\00\00\00\00")
  (data (;47;) (i32.const 1648) "\00\00\00\00\00\00\00\00\00\00\00\00\10\00\00\00D\00\00\00\94\05\00\00\10\06\00\00\00\00\00\00")
  (data (;48;) (i32.const 1680) "\00\00\00\00\00\00\00\00\00\00\00\00\1f\00\00\00E\00\00\00\b4\05\00\00\f0\05\00\00\00\00\00\00")
  (data (;49;) (i32.const 1712) "\00\00\00\00\00\00\00\00\00\00\00\00\04\00\00\00F\00\00\00\f2\05\00\00x\03\00\00\00\00\00\00")
  (data (;50;) (i32.const 1744) "\00\00\00\00\00\00\00\00\00\00\00\00\07\00\00\00J\00\00\002\06\00\00x\03\00\00\00\00\00\00")
  (data (;51;) (i32.const 1776) "\00\00\00\00\00\00\00\00\00\00\00\00\15\00\00\00K\00\00\00@\06\00\00\10\07\00\00\00\00\00\00")
  (data (;52;) (i32.const 1808) "\00\00\00\00\00\00\00\00\00\00\00\00\0f\00\00\00Q\00\00\00z\08\00\00x\03\00\00")
  (data (;53;) (i32.const 1836) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00R\00\00\00\98\08\00\00x\03\00\00\00\00\00\00")
  (data (;54;) (i32.const 1868) "\00\00\00\00\00\00\00\00\00\00\00\00\09\00\00\00T\00\00\00\ae\08\00\00,\07\00\00\00\00\00\00")
  (data (;55;) (i32.const 1900) "\00\00\00\00\00\00\00\00\00\00\00\00\0d\00\00\00\a3\00\00\00\dc\0c\00\00|\04\00\00\01\00\00\00\ef\ff\ff\ff")
  (data (;56;) (i32.const 1936) "\00\00\00\00\00\00\00\00\00\00\00\00\0b\00\00\00\a4\00\00\00\f6\0c\00\00x\03\00\00\01\00\00\00\ed\ff\ff\ff")
  (data (;57;) (i32.const 1972) "\00\00\00\00\00\00\00\00\00\00\00\00\0b\00\00\00\a5\00\00\00\0c\0d\00\00x\03\00\00\02\00\00\00\f3\ff\ff\ff\ee\ff\ff\ff")
  (@custom "sourceMappingURL" (after data) "1kotlin210inlinebug-wasmAssertk-wasm-wasi.wasm.map")
)
