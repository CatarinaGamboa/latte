package examples;

import specification.Free;
import specification.Unique;

/**
 * Node class for the stack example
 * Uses @Unique annotations to specify that the value and next fields are unique
 * in the scope of the Node class
 * @author catarina gamboa
 *
 */
public class Node {
	
	@Unique Object value;
	@Unique Node next;

	/**
	 * Constructor for the Node class using @Free value and next nodes
	 * @param value
	 * @param next
	 */
	public Node (@Free Object value, @Free Node next) {
		this.value = value;
		this.next = next;
	}
}
