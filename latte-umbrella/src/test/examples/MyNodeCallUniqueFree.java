
import specification.Free;
import specification.Unique;


public class MyNodeCallUniqueFree {
    @Unique Node root;		
            
    public MyNodeCallUniqueFree(@Free Node root) {
        this.root = root;
    }


    void push( @Free Object value) {
        
        Node r;
        Node n;
        
        r = this.root; 			// save root in r
        this.root = null; 		//nullify root
        n = new Node(value, r); //create new root
        this.root = n; 			//replace root

        Node  n1 = this.getNew(n);
        
    }


    @Free Node getNew(@Free Node value) {
        return new Node(new Object(), value);	
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