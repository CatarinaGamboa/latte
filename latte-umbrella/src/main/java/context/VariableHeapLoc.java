package context;

abstract class VariableHeapLoc {}

class FieldHeapLoc extends VariableHeapLoc{

	SymbolicValue heapLoc;
    Variable field;

    public FieldHeapLoc(SymbolicValue heapLoc, Variable field) {
        this.heapLoc = heapLoc;
		this.field = field;
    }

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FieldHeapLoc && 
			((FieldHeapLoc) obj).field.equals(field) && 
			((FieldHeapLoc) obj).heapLoc.equals(heapLoc);
	}
}
