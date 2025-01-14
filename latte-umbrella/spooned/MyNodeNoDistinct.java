public class MyNodeNoDistinct {
    @specification.Unique
    Node root;

    Node shared_node;

    public  MyStack(@specification.Free
    Node root) {
    }

    public void test(@specification.Free
    java.lang.Object value) {
        Node r;
        Node n;
        r = this.root;
        // this.root = null;
        n = new Node(this.root, r);
    }
}