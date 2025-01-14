/**
 * Node class for the stack example
 * Uses @Unique annotations to specify that the value and next fields are unique
 * in the scope of the Node class
 *
 * @author catarina gamboa
 */
class Node {
    @specification.Unique
    java.lang.Object value;

    @specification.Unique
    Node next;

    /**
     * Constructor for the Node class using @Free value and next nodes
     *
     * @param value
     * @param next
     */
    public Node(@specification.Borrowed
    java.lang.Object value, @specification.Borrowed
    Node next) {
        this.value = value;
        this.next = next;
    }
}