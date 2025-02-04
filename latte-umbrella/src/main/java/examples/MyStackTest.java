package examples;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import specification.Borrowed;
import specification.Free;
import specification.Shared;
import specification.Unique;

class MyStackTest {


		// public static void test(@Borrowed ArrayList<String> list) {
		// 	// ArrayList<String> nl = list;
			
		// 	// byte[] t = nl.getFirst().getBytes();

		// 	// byte[] f = list.getFirst().getBytes();

		// 	Box b1 = new Box();
		// 	b1.add(123);



		// }
}


// class Box{
// 	@Unique int value;

// 	void add(@Borrowed int delta){
// 		this.value = this.value + delta;
// 	}

// 	// @Free Box2 makeBox2(){
// 	// 	return new Box2(this.value);
// 	// }

// }

// class Box2{
// 	@Shared int val;

// 	public Box2(@Free int v){
// 		this.val = v; 
// 	}
// }


// class Box {
// 	@Unique int value;
// 	void add(@Owned int delta) {
// 	// ERROR: RHS and LHS are not separately unique
// 	this.value = this.value + this.delta;
// 	}
// 	void test() {
// 	int x = 123;
// 	Box b1 = new Box(x);
// 	// ERROR: ‘x‘ is consumed since ‘value‘ is unique,
// 	// thus it is unaccessible
// 	Box b2 = new Box(x);
// 	}
// 	void makeBox2() {
// 	// ERROR: ‘this.value‘ is unique, thus cannot be
// 	// passed to a shared parameter
// 	return new Box2(this.value);
// 	}
// 	}
// 	class Box2 { @Shared int value; }



// 		HttpResponse response = r;
//         InputStream in = response.getEntity().getContent();
//         BufferedReader reader = new BufferedReader(
//                 new InputStreamReader(
//                         response.getEntity().getContent(), "UTF-8")); // Second call to getEntity()

    

// 		@Unique Node root;		
		
// 		public MyStack(@Free Node root) {
// 			this.root = root;
// 		}

		
// 		void push( @Free Object value) {	
// 			Node r;
// 			Node n;
			
// 			r = this.root; 			// save root in r
// 			this.root = null; 		//nullify root
// 			n = new Node(value, r); //create new root
// 			this.root = n; 			//replace root
// 		}


// 		@Free Object pop (){
// 			Object value;

// 			if (this.root == null) {
// 				value = null;
// 			} else {
// 				Node r = root;
// 				value = r.value;
// 				Node n;
// 				n = r.next;
// 				r.next = null;
// 				r.value = null;
// 				this.root = n;
// 			}
// 			return value;
// 		}
		
// 		// public @Unique Object dequeue() {
// 		// 	Node r = this.root;
// 		// 	//r : alias(this.root)
// 		// 	if (r == null || r.next == null) {
// 		// 	  // In an empty or single-element stack, dequeue and pop
// 		// 	  // are equivalent
// 		// 	  return pop();
// 		// 	} else {
			
// 		// 		// `this` and `this.root` are effectively alias, thus
// 		// 		// we cannot pass `this.root` to `this.dequeue` without
// 		// 		// doing a destructive read
// 		// 		this.root = null;
// 		// 		// r : unique
			
// 		// 		Object value = dequeueHelper(r);
// 		// 		// value : unique
			
// 		// 		// Since `dequeue` only detaches the next node of the one
// 		// 		// passed to it, it will never need to detach `root`, so
// 		// 		// we can just restore it back to the original value.
// 		// 		this.root = r;
// 		// 		// r : alias(this.root)
			
// 		// 		return value;
// 		// 	}
// 		// }
		
// 		// private @Free Object dequeueHelper(@Borrowed Node n) {
// 		// 	// Look ahead two steps so that we can disconnect the *next*
// 		// 	// node by mutating the node that will remain in the stack.
		
// 		// 	Node nn = n.next;
// 		// 	// nn : alias(n.next)
		
// 		// 	if (nn.next == null) {
// 		// 	  n.next = null;
// 		// 	  // nn : unique
			  
// 		// 	  Object value = nn.value;
// 		// 	  // value : alias(nn.value)
		
// 		// 	  nn.value = null;
// 		// 	  // value : unique
		
// 		// 	  return value;
// 		// 	} else {
// 		// 	  return dequeueHelper(n.next);
// 		// 	}
// 		// }
// }

// /**
//  * Node class for the stack example
//  * Uses @Unique annotations to specify that the value and next fields are unique
//  * in the scope of the Node class
//  * @author catarina gamboa
//  *
//  */
// class Node {
	
// 	@Unique Object value;
// 	@Unique Node next;

// 	/**
// 	 * Constructor for the Node class using @Free value and next nodes
// 	 * @param value
// 	 * @param next
// 	 */
// 	public Node (@Free Object value, @Free Node next) {
// 		this.value = value;
// 		this.next = next;
// 	}
// }
