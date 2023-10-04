package typechecking;

import context.Context;
import context.Variable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.CtScanner;

public class LatteTypeChecker  extends CtScanner {
	
	Context context;
	 
    public LatteTypeChecker(Context context) {
		this.context = context; 
	}

	@Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        System.out.println("CTCLASS:"+ctClass.getSimpleName());
        context.addClass(ctClass);
        context.enterScope();
        super.visitCtClass(ctClass);
        context.exitScope();
    }
	
	@Override
	public <T> void visitCtField(CtField<T> f) {
		Variable v = new Variable(f);
		context.addInScope(v.getName(), v);
		System.out.println("with fields:\n" + context.toString());
		super.visitCtField(f);
	}
	
	
	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		context.enterScope();
		super.visitCtConstructor(c);
		context.exitScope();
	}
	
	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		// TODO Auto-generated method stub
		context.enterScope();
		super.visitCtMethod(m);
		context.exitScope();
	}
	
	
	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		Variable v = new Variable(parameter);
		context.addInScope(v.getName(), v);
		System.out.println("with param:\n" +context.toString());
		super.visitCtParameter(parameter);
	}

}
