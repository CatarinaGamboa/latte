
import specification.Free;
import specification.Shared;
import specification.Unique;


public class FieldAccessRightNoThis {

	@Unique Object value;

	public FieldAccessRightNoThis(){
		Object v = value;

		Box2 b2 = new Box2(v);


	}
}

class Box2{
	@Shared Object val;

	public Box2(@Free Object v){
		this.val = v; 
	}
}
    

