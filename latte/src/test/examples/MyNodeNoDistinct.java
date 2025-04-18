import specification.Borrowed;
import specification.Free;
import specification.Unique;

public class MyNodeNoDistinct {

		@Unique Node root;

		Node shared_node;
		
		
		public MyStack(@Free Node root) {
		}

		public void test(@Free Object value){

			Node r;
			Node n; 
			r = this.root;

			// this.root = null;

			n = new Node(this.root, r);
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
	public Node (@Borrowed Object value, @Borrowed Node next) {
		this.value = value;
		this.next = next;
	}
}
