package examples;
public class MyNodePush {
    @specification.Unique
    examples.Node root;

    public  MyStack(@specification.Free
    examples.Node root) {
        this.root = root;
    }

    public void push(@specification.Free
    java.lang.Object value) {
        examples.Node r;
        examples.Node n;
        r = this.root;// save root in r

        this.root = null;// nullify root

        n = new examples.Node(value, r);// create new root

        this.root = n;// replace root

    }
}