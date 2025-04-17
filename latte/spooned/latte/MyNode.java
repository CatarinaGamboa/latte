package latte;
/* This file is part of the Latte test suite. */
class MyNode {
    @specification.Unique
    java.lang.Object value;

    @specification.Unique
    latte.Node next;

    /**
     * Constructor for the Node class using @Free value and next nodes
     *
     * @param value
     * @param next
     */
    public MyNode(@specification.Free
    java.lang.Object value, @specification.Free
    latte.Node next) {
        this.value = value;
        this.next = next;
    }
}