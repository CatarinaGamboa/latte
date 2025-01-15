package examples;
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

    }

    @specification.Free
    java.lang.Object pop() {
        java.lang.Object value;
        if (this.root == null) {
            value = null;
        } else {
            examples.Node r = this.root;
            value = r.value;
            examples.Node n;
            n = r.next;
            r.next = null;
            r.value = null;
            this.root = n;
        }
        return value;
    }

    public static void main(java.lang.String[] args) {
        examples.Node n = new examples.Node(7, null);
        examples.MyStack s = new examples.MyStack(n);
    }
}