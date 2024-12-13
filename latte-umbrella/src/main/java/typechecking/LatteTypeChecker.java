package typechecking;

import context.Context;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.TypeEnvironment;
import context.VariableT;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.visitor.CtScanner;

public class LatteTypeChecker  extends CtScanner {
	
	Context context;
	TypeEnvironment te;
	SymbolicEnvironment se;
	PermissionEnvironment pe;
	 
    public LatteTypeChecker(Context context, TypeEnvironment typeEnv, SymbolicEnvironment symbEnv, PermissionEnvironment permEnv) {
		this.context = context; 
		this.te = typeEnv;
		this.se = symbEnv;
		this.pe = permEnv;
	}

	@Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        System.out.println("CTCLASS:"+ctClass.getSimpleName());
        context.addClass(ctClass);
        enterScopes();
        super.visitCtClass(ctClass);
        exitScopes();
    }
	
	@Override
	public <T> void visitCtField(CtField<T> f) {
		// VariableT v = new VariableT(f);
		// context.addInScope(v.getName(), v);
		// System.out.println("with fields:\n" + context.toString());
		super.visitCtField(f);
	}
	
	
	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		enterScopes();
		super.visitCtConstructor(c);
		exitScopes();
	}
	
	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		// TODO Auto-generated method stub
		enterScopes();
		super.visitCtMethod(m);
		exitScopes();
	}
	
	
	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		VariableT v = new VariableT(parameter);
		// context.addInScope(v.getName(), v);
		// System.out.println("with param:\n" +context.toString());
		super.visitCtParameter(parameter);
	}

	private void enterScopes(){
		enterScopes();
		te.enterScope();
		se.enterScope();
		pe.enterScope();
	}

	private void exitScopes(){
		te.exitScope();
		se.exitScope();
		pe.exitScope();
	}
}
