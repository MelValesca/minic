package minic.front;

public enum Type {
    	Int,
    	Bool,
    	/* Pointeurs scalaires */
    	IntPtrScalar,
    	BoolPtrScalar,

    	/* Pointeurs tableaux */
    	IntPtrArray,
    	BoolPtrArray;


    	boolean isPtr() { 
		return ordinal() >= IntPtrScalar.ordinal();
	}

	public Type deref() {
		switch (this) {
			case IntPtrScalar:
			case IntPtrArray:	return Int;
			case BoolPtrScalar:
			case BoolPtrArray:	return Bool;
			default:			throw new RuntimeException("deref sur non‑pointeur");
		}
	}

	public boolean isScalarPtr() {
		return this == IntPtrScalar || this == BoolPtrScalar;
	}

	public boolean isArrayPtr() {
		return this == IntPtrArray || this == BoolPtrArray;
	}
}
