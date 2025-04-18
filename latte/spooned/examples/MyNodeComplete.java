package examples;
public class MyNodeComplete {
    @specification.Unique
    examples.Node root;

    public  MyStack(@specification.Free
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
            examples.Node r = root;
            value = r.value;
            examples.Node n;
            n = r.next;
            r.next = null;
            r.value = null;
            this.root = n;
        }
        return value;
    }

    @specification.Unique
    public java.lang.Object dequeue() {
        examples.Node r = this.root;
        // r : alias(this.root)
        if ((r == null) || (r.next == null)) {
            // In an empty or single-element stack, dequeue and pop
            // are equivalent
            return pop();
        } else {
            // `this` and `this.root` are effectively alias, thus
            // we cannot pass `this.root` to `this.dequeue` without
            // doing a destructive read
            this.root = null;
            // r : unique
            java.lang.Object value = dequeueHelper(r);
            // value : unique
            // Since `dequeue` only detaches the next node of the one
            // passed to it, it will never need to detach `root`, so
            // we can just restore it back to the original value.
            this.root = r;
            // r : alias(this.root)
            return value;
        }
    }

    @specification.Unique
    private java.lang.Object dequeueHelper(@specification.Borrowed
    examples.Node n) {
        // Look ahead two steps so that we can disconnect the *next*
        // node by mutating the node that will remain in the stack.
        examples.Node nn = n.next;
        // nn : alias(n.next)
        if (nn.next == null) {
            n.next = null;
            // nn : unique
            java.lang.Object value = nn.value;
            // value : alias(nn.value)
            nn.value = null;
            // value : unique
            return value;
        } else {
            return dequeueHelper(n.next);
        }
    }
}