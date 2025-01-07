package context;

public class Variable extends VariableHeapLoc{

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
