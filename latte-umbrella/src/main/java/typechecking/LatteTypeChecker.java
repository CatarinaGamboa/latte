package typechecking;

import context.Context;
import context.Variable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.visitor.CtScanner;

public class LatteTypeChecker  extends CtScanner {
	
	Context context;
	 
    public LatteTypeChecker(Context context) {
		this.context = context; 
	}

	@Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        System.out.println("CTCLASS:"+ctClass.getSimpleName());
        super.visitCtClass(ctClass);
    }
	
	@Override
	public <T> void visitCtField(CtField<T> f) {
		Variable v = new Variable(f);
		System.out.println(v.toString());

		super.visitCtField(f);
	}

}
