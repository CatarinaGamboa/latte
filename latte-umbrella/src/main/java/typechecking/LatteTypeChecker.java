package typechecking;

import context.Context;
import context.MapTypeClass;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.SymbolicValue;
import context.TypeEnvironment;
import context.Uniqueness;
import context.UniquenessAnnotation;
import context.VariableT;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

/**
 * In the type checker we go through the code, add metadata regarding the types and their permissions
 * and check if the code is well-typed
 * 
 * Metadata added:
 * "symbolic_value" -> SymbolicValue
 */
public class LatteTypeChecker  extends CtScanner {
	
	Context context;
	TypeEnvironment typeEnv;
	SymbolicEnvironment symbEnv;
	PermissionEnvironment permEnv;
	MapTypeClass mapTypeClass;
	 
    public LatteTypeChecker(Context context, TypeEnvironment typeEnv, SymbolicEnvironment symbEnv, PermissionEnvironment permEnv, MapTypeClass mtc) {
		this.context = context; 
		this.typeEnv = typeEnv;
		this.symbEnv = symbEnv;
		this.permEnv = permEnv;
		this.mapTypeClass = mtc;
	}

	@Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
        System.out.println("CTCLASS:"+ctClass.getSimpleName());
        context.addClass(ctClass);
        enterScopes();

		// Add the class to the type reference and class map
		CtTypeReference<?> typeRef = ctClass.getReference();
		mapTypeClass.add(typeRef, ctClass);
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

	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		
		// 1) Add the variable to the typing context
		CtTypeReference<?> t = localVariable.getType();
		String name = localVariable.getSimpleName();
		CtClass<?> ctClass = mapTypeClass.getClassFrom(t);
		typeEnv.add(name, ctClass);
		CtElement element = localVariable.getAssignment();
		if (element == null){
			//TODO nothing
			System.out.println("No assignment");
		} else {
			System.out.println("Has assignment");			
			System.out.println(element.getMetadata("symbolic_value"));
		}

		super.visitCtLocalVariable(localVariable);
	}


	/**
	 * Visit a literal, add a symbolic value to the environment and a permission of shared
	 */
	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		SymbolicValue sv = symbEnv.getFree();
		UniquenessAnnotation ua = new UniquenessAnnotation(literal);

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);

		// Store the symbolic value in metadata
		literal.putMetadata("symbolic_value", sv);

		super.visitCtLiteral(literal);
	}

	private void enterScopes(){
		typeEnv.enterScope();
		symbEnv.enterScope();
		permEnv.enterScope();
	}

	private void exitScopes(){
		typeEnv.exitScope();
		symbEnv.exitScope();
		permEnv.exitScope();
	}
}
