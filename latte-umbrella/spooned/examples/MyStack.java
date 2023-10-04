package examples;


public class MyStack {
    @specification.Unique
    examples.Node root;

    public MyStack(@specification.Unique
    examples.Node root) {
    }

    // 
    // void push( @Unique Object value) {
    // 
    // Node r;
    // Node n;
    // 
    // r = this.root; 			// save root in r
    // this.root = null; 		//nullify root
    // n = new Node(value, r); //create new root
    // this.root = n; 			//replace root
    // 
    // }
    public static void main(java.lang.String[] args) {
        examples.Node n = new examples.Node(7, null);
        examples.MyStack s = new examples.MyStack(n);
    }
}

