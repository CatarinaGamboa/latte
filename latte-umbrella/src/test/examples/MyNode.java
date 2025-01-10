package latte;

import specification.Borrowed;
import specification.Free;
import specification.Unique;

/*
 * This file is part of the Latte test suite.
 */
class MyNode {
	
	@Unique Object value;
	@Unique Node next;

	/**
	 * Constructor for the Node class using @Free value and next nodes
	 * @param value
	 * @param next
	 */
	public MyNode (@Free Object value, @Borrowed Node next) {
		this.value = value;
		this.next = next;
	}
}
