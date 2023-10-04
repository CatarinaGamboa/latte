package examples;

import specification.Unique;

public class MyStack {

		@Unique Node root;
		
		
		public MyStack(@Unique Node root) {
			this.root = root;
		}
//		
//		void push( @Unique Object value) {
//			
//			Node r;
//			Node n;
//			
//			r = this.root; 			// save root in r
//			this.root = null; 		//nullify root
//			n = new Node(value, r); //create new root
//			this.root = n; 			//replace root
//			
//		}
		
		
		public static void main(String[] args) {
			Node n = new Node(7, null);
			MyStack s = new MyStack(n);
		}

}
