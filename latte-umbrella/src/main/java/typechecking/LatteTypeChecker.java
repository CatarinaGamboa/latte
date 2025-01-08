package typechecking;

import context.ClassLevelMaps;
import context.Context;
import context.PermissionEnvironment;
import context.SymbolicEnvironment;
import context.SymbolicValue;
import context.TypeEnvironment;
import context.Uniqueness;
import context.UniquenessAnnotation;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtConstructorCallImpl;
import spoon.support.reflect.code.CtVariableReadImpl;
import spoon.support.reflect.code.CtVariableWriteImpl;

/**
 * In the type checker we go through the code, add metadata regarding the types and their permissions
 * and check if the code is well-typed
 * 
 * Metadata added:
 * "symbolic_value" -> SymbolicValue
 */
public class LatteTypeChecker  extends LatteProcessor {

    public LatteTypeChecker(Context context, TypeEnvironment typeEnv, SymbolicEnvironment symbEnv, 
							PermissionEnvironment permEnv, ClassLevelMaps mtc) {
		super(context, typeEnv, symbEnv, permEnv, mtc);
		logInfo("[ Latte Type checker initialized ]");
	}

	@Override
    public <T> void visitCtClass(CtClass<T> ctClass) {
		logInfo("Visiting class: " + ctClass.getSimpleName());
		enterScopes();
		super.visitCtClass(ctClass);
		exitScopes();
	}
			
	
	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		logInfo("Visiting constructor "+ c.getSimpleName());
		enterScopes();
		super.visitCtConstructor(c);
		exitScopes();
	}
	
	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		logInfo("Visiting method: "+ m.getSimpleName());
		// TODO Auto-generated method stub
		enterScopes();
		super.visitCtMethod(m);
		exitScopes();
	}
	
	
	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		logInfo("Visiting parameter: "+ parameter.getSimpleName());
		loggingSpaces++;
		super.visitCtParameter(parameter);
		// TODO Auto-generated method stub
		loggingSpaces--;
	}


	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		logInfo("Visiting local variable: "+ localVariable.getSimpleName());
		loggingSpaces++;
		// 1) Add the variable to the typing context
		CtTypeReference<?> t = localVariable.getType();
		String name = localVariable.getSimpleName();
		CtClass<?> ctClass = maps.getClassFrom(t);
		typeEnv.add(name, ctClass);
		SymbolicValue v = symbEnv.addVariable(name);
		permEnv.add(v, new UniquenessAnnotation(Uniqueness.BOTTOM));

		// 2) Visit
		super.visitCtLocalVariable(localVariable);

		// 3) Handle assignment
		CtElement element = localVariable.getAssignment();
		if (element == null){
			logInfo("Local variable "+name+" - No assignment");
		} else {
			Object o = element.getMetadata("symbolic_value");
			if (o == null) 
				logError(String.format("Local variable %s = %s has assignment with null symbolic value", name, 
					localVariable.getAssignment().toString()));
			else{
				symbEnv.addVarSymbolicValue(name, (SymbolicValue) o);
				ClassLevelMaps.simplify(symbEnv, permEnv);
				logInfo(String.format("Local variable %s = %s with symbolic value %s", name, 
				localVariable.getAssignment().toString(), element.getMetadata("symbolic_value")));
			}
				
			// TODO: what to do with the assignment
		}
		loggingSpaces--;
	}

	/**
	 * EvalField
		Δ(𝑥) = 𝜈   Δ(𝜈.𝑓 ) = 𝜈′   Σ(𝜈) ≠ ⊥   Σ(𝜈′) ≠ ⊥
		----------------------------------------------
		Γ; Δ; Σ ⊢ 𝑥 .𝑓 ⇓ 𝜈′ ⊣ Γ; Δ; Σ
	 */
	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		logInfo("Visiting field read "+ fieldRead.toStringDebug());
		loggingSpaces++;

		super.visitCtFieldRead(fieldRead);
		CtExpression<?> target = fieldRead.getTarget();
		CtFieldReference<?> f = fieldRead.getVariable();

		if ( target instanceof CtVariableReadImpl){
			// Δ(𝑥) = 𝜈 
			CtVariableReadImpl<?> x = (CtVariableReadImpl<?>) target;
			SymbolicValue sv = symbEnv.get(x.getVariable().getSimpleName());
			
			UniquenessAnnotation ua = permEnv.get(sv);
			// EVAL UNIQUE FIELD
			if ( ua.isGreaterEqualThan(Uniqueness.UNIQUE)) {
				logInfo("Eval Unique Field");
				SymbolicValue vp = symbEnv.get(sv, f.getSimpleName());
				// 𝜈.𝑓 ∉ Δ
				if (vp == null){
					//field(Γ(𝑥), 𝑓 ) = 𝛼 𝐶
					UniquenessAnnotation fieldUA = maps.getFieldAnnotation(f.getSimpleName(), x.getType());
					if (fieldUA == null) logError(String.format("field annotation not found for %s", f.getSimpleName()));
					//----------------
					//𝜈.𝑓 : 𝜈′, Δ; 𝜈′: 𝛼, Σ   fresh 𝜈
					SymbolicValue vv = symbEnv.addField(sv, f.getSimpleName());
					permEnv.add(vv, fieldUA);

					// 𝑥 .𝑓 ⇓ 𝜈′
					fieldRead.putMetadata("symbolic_value", vv);
					logInfo(String.format("UniqueField read %s.%s has symbolic value %s", x.getVariable().getSimpleName(), f.getSimpleName(), vv));
				}


			} else {
				// EVAL FIELD
				// // Σ(𝜈) ≠ ⊥ 

				// if (ua.isBottom()){
				// 	logError(String.format("Symbolic value %s has bottom permission", sv));
				// }
				
				// // Δ(𝜈.𝑓 ) = 𝜈′, if not present, add it 
				// SymbolicValue vp = symbEnv.get(sv, f.getSimpleName());
				// if (vp == null){
				// 	symbEnv.addField(vp, f.getSimpleName());
				// }

				// // Σ(𝜈′) ≠ ⊥
				// if (permEnv.get(vp).isBottom()){
				// 	logError(String.format("Symbolic value %s has bottom permission", vp));
				// }
			}


		}

		logInfo("getVariable() results " + fieldRead.getVariable().prettyprint());
		logInfo("fieldRead target " + fieldRead.getTarget().prettyprint());
		loggingSpaces--;
	}

	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignment) {
		logInfo("Visiting assignment "+ assignment.toStringDebug());
		loggingSpaces++;
		super.visitCtAssignment(assignment);

		CtExpression<?> target = assignment.getAssigned();
		CtExpression<?> value = assignment.getAssignment();


		//TODO: CheckCall

		//CheckNew
		if (value instanceof CtConstructorCallImpl && target instanceof CtVariableWriteImpl){
			CtVariableWriteImpl<?> y = (CtVariableWriteImpl<?>) target;
			CtConstructorCallImpl<?> constCall = (CtConstructorCallImpl<?>) value;

			if (constCall.getArguments().size() == 0){
				SymbolicValue vv = symbEnv.addVariable(y.getVariable().getSimpleName());
				permEnv.add(vv, new UniquenessAnnotation(Uniqueness.FREE));
				ClassLevelMaps.simplify(symbEnv, permEnv);
			} else {
				logError("TODO: Handle constructor call with arguments");
			}
			

		} else if (target instanceof CtVariableWriteImpl){
			SymbolicValue v = (SymbolicValue) value.getMetadata("symbolic_value");
			if (v == null)
				logError("Symbolic value for assignment not found");
			symbEnv.addVarSymbolicValue(target.toString(), v);
			ClassLevelMaps.simplify(symbEnv, permEnv);
		}

		loggingSpaces--;
	}


	/**
	 * Rule EvalBinary
	 */
	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		logInfo("Visiting binary operator "+ operator.toStringDebug());
		loggingSpaces++;
		super.visitCtBinaryOperator(operator);

		// Get a fresh symbolic value and add it to the environment with a shared default value
		SymbolicValue sv = symbEnv.getFresh();
		UniquenessAnnotation ua = new UniquenessAnnotation(Uniqueness.SHARED);

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);

		// Store the symbolic value in metadata
		operator.putMetadata("symbolic_value", sv);
		loggingSpaces--;
	}

	/**
	 * Rule EvalUnary
	 */
	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		logInfo("Visiting unary operator "+ operator.toStringDebug());
		loggingSpaces++;
		super.visitCtUnaryOperator(operator);

		// Get a fresh symbolic value and add it to the environment with a shared default value
		SymbolicValue sv = symbEnv.getFresh();
		UniquenessAnnotation ua = new UniquenessAnnotation(Uniqueness.SHARED);

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);
		
		// Store the symbolic value in metadata
		operator.putMetadata("symbolic_value", sv);
		loggingSpaces--;
	}

	/**
	 * Rule EvalVar
	 */
	@Override
	public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
		logInfo("Visiting local variable reference "+ reference.toString());
		loggingSpaces++;
		super.visitCtLocalVariableReference(reference);
		
		SymbolicValue sv = symbEnv.get(reference.getSimpleName());
		if (sv == null) {
			logError(String.format("Symbolic value for local variable %s not found in the symbolic environment",
				reference.getSimpleName()));
		} else{
			UniquenessAnnotation ua = permEnv.get(sv);
			if (ua.isBottom()){
				logInfo(String.format("Symbolic value %s has bottom permission", sv));
			} else {
				reference.putMetadata("symbolic_value", sv);
			}
		}
		loggingSpaces--;
	}

	/**
	 * Rule EvalConst
	 * Visit a literal, add a symbolic value to the environment and a permission of shared
	 */
	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		logInfo("Visiting literal"+ literal.toString());
		
		super.visitCtLiteral(literal);

		// Get a fresh symbolic value and add it to the environment with a shared default value
		SymbolicValue sv = symbEnv.getFresh();
		UniquenessAnnotation ua = new UniquenessAnnotation(Uniqueness.SHARED);

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);

		// Store the symbolic value in metadata
		literal.putMetadata("symbolic_value", sv);
	}

}
