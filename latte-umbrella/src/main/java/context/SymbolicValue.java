package context;

public class SymbolicValue {

    int value;

    public SymbolicValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SymbolicValue && ((SymbolicValue) obj).value == value;
    }
}
