/* This file is part of the Latte test suite. */
class MyNode {
    @Unique
    java.lang.Object value;

    @Unique
    Node next;

    /**
     * Constructor for the Node class using @Free value and next nodes
     *
     * @param value
     * @param next
     */
    public MyNode(@Free
    java.lang.Object value, @Borrowed
    Node next) {
        this.value = value;
        this.next = next;
    }
}