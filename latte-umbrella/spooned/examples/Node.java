package examples;


public class Node {
    @specification.Unique
    java.lang.Object value;

    @specification.Unique
    examples.Node next;

    public Node(@specification.Unique
    java.lang.Object value, @specification.Unique
    examples.Node next) {
        this.value = value;
        this.next = next;
    }
}

