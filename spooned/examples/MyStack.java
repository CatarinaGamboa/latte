package examples;
// void push( @Free Object value) {
// Node r;
// Node n;
// r = this.root; 			// save root in r
// this.root = null; 		//nullify root
// n = new Node(value, r); //create new root
// this.root = n; 			//replace root
// }
// @Free Object pop (){
// Object value;
// if (this.root == null) {
// value = null;
// } else {
// Node r = this.root;
// value = r.value;
// Node n;
// n = r.next;
// r.next = null;
// r.value = null;
// this.root = n;
// }
// return value;
// }
// public static void main(String[] args) {
// Node n = new Node(7, null);
// MyStack s = new MyStack(n);
// }
public class MyStack {
    @specification.Unique
    examples.Node root;

    examples.Node shared_node;

    public MyStack(@specification.Free
    examples.Node root) {
    }

    public void test(@specification.Free
    java.lang.Object value) {
        examples.Node r;
        // Node n;
        r = this.root;
        this.root = null;
        // n = new Node(value, r);
    }
}