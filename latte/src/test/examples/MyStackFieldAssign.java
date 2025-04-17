
import specification.Borrowed;
import specification.Free;
import specification.Unique;

public class MyStackFieldAssign {

	@Unique Node root;		
	
	public MyStack(@Free Node root) {
		this.root = root;
	}
	
	void push( @Free Object value) {	
		Node r;

		r = this.root; 			// save root in r
		this.root = null; 		//nullify root
		this.root = new Node(value, r);
	}

}

/**
 * Node class for the stack example
 * Uses @Unique annotations to specify that the value and next fields are unique
 * in the scope of the Node class
 * @author catarina gamboa
 *
 */
class Node {
	
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
