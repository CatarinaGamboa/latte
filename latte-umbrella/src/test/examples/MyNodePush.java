package examples;

import specification.Free;
import specification.Unique;

public class MyNodePush {

    @Unique Node root;		
    
    public MyStack(@Free Node root) {
        this.root = root;
    }

    public void push(@Free Object value){

        Node r;
        Node n; 

        r = this.root; 			// save root in r
        this.root = null; 		//nullify root
        n = new Node(value, r); //create new root
        this.root = n; 			//replace root
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
