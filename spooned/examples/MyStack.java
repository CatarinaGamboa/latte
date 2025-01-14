package examples;
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

    public MyStack(@specification.Free
    examples.Node root) {
        this.root = root;
    }

    void push(@specification.Free
    java.lang.Object value) {
        examples.Node r;
        examples.Node n;
        r = this.root;// save root in r

        this.root = null;// nullify root

        n = new examples.Node(value, r);// create new root

        this.root = n;// replace root

        examples.Node n1 = getNew(n);
    }

    @specification.Free
    examples.Node getNew(@specification.Free
    examples.Node value) {
        return new examples.Node(new java.lang.Object(), value);
    }
}