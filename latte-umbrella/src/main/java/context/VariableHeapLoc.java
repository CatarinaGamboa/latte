package context;

/**
 * Represents a variable or a field in the heap
 */
public abstract class VariableHeapLoc {}

/**
 * Represents a field in the heap
 */
class FieldHeapLoc extends VariableHeapLoc{

	SymbolicValue heapLoc;
    Variable field;

    public FieldHeapLoc(SymbolicValue heapLoc, Variable field) {
        this.heapLoc = heapLoc;
		this.field = field;
    }

	public FieldHeapLoc(SymbolicValue heapLoc, String field) {
        this.heapLoc = heapLoc;
		this.field = new Variable(field);
    }

	@Override
	public int hashCode() {
		return heapLoc.value + field.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FieldHeapLoc && 
			((FieldHeapLoc) obj).field.equals(field) && 
			((FieldHeapLoc) obj).heapLoc.equals(heapLoc);
	}

	@Override
	public String toString() {
		return heapLoc.toString() + "." + field.toString();
	}
}

/*
 * Represents a variable
 */
class Variable extends VariableHeapLoc{

    String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Variable{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Variable && ((Variable) obj).name.equals(name);
    }

}
