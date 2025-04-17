public class MyNodeCallUniqueFree {
    @specification.Unique
    Node root;

    public MyNodeCallUniqueFree(@specification.Free
    Node root) {
        this.root = root;
    }

    void push(@specification.Free
    java.lang.Object value) {
        Node r;
        Node n;
        r = this.root;// save root in r

        this.root = null;// nullify root

        n = new Node(value, r);// create new root

        this.root = n;// replace root

        Node n1 = this.getNew(n);
    }

    @specification.Free
    Node getNew(@specification.Free
    Node value) {
        return new Node(new java.lang.Object(), value);
    }
}