package minic.front;

public enum Type {
    	Int,
    	Bool,
    	IntPtr, 
	BoolPtr;

    	boolean isPtr() { 
		return this == IntPtr || this == BoolPtr; 
	}
    	Type    deref() {
        	if (this == IntPtr)  return Int;
        	if (this == BoolPtr) return Bool;
        	throw new RuntimeException("deref non‑pointeur");
    	}
}
