package examples;

import specification.Unique;

public class Node {
	
	@Unique Object value;
	
	@Unique Node next;

	
	public Node (@Unique Object value, @Unique Node next) {
		this.value = value;
		this.next = next;
	}
}
