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
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtConstructorCallImpl;
import spoon.support.reflect.code.CtThisAccessImpl;
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

		// Assume 'this' is a parameter always borrowed
		SymbolicValue thv = symbEnv.addVariable(THIS);
		permEnv.add(thv, new UniquenessAnnotation(Uniqueness.BORROWED));

		super.visitCtConstructor(c);

		exitScopes();
	}
	
	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		logInfo("Visiting method: "+ m.getSimpleName());
		enterScopes();

		// Assume 'this' is a parameter always borrowed
		SymbolicValue thv = symbEnv.addVariable(THIS);
		permEnv.add(thv, new UniquenessAnnotation(Uniqueness.BORROWED));

		super.visitCtMethod(m);
		exitScopes();
	}
	
	
	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		logInfo("Visiting parameter: "+ parameter.getSimpleName());
		loggingSpaces++;
		super.visitCtParameter(parameter);
		
		SymbolicValue sv = symbEnv.addVariable(parameter.getSimpleName());
		UniquenessAnnotation ua = new UniquenessAnnotation(parameter);
		permEnv.add(sv, ua);
		logInfo(parameter.getSimpleName() + ": "+ sv);
		logInfo(sv + ": "+ ua);

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
		CtElement value = localVariable.getAssignment();
		if (value != null){
			SymbolicValue vValue = (SymbolicValue) value.getMetadata("symbolic_value");
			if (vValue == null) 
				logWarning(String.format("Local variable %s = %s has assignment with null symbolic value", name, 
					localVariable.getAssignment().toString()));
			else{
				if (value instanceof CtConstructorCallImpl ){
					CtConstructorCallImpl<?> constCall = (CtConstructorCallImpl<?>) value;
					if (constCall.getArguments().size() == 0){
						SymbolicValue vv = symbEnv.addVariable(localVariable.getSimpleName());
						permEnv.add(vv, new UniquenessAnnotation(Uniqueness.FREE));
						ClassLevelMaps.simplify(symbEnv, permEnv);
					} else {
						logWarning("TODO: Handle constructor call with arguments");
					}	
				} else {
					symbEnv.addVarSymbolicValue(localVariable.getSimpleName(), vValue);

					ClassLevelMaps.simplify(symbEnv, permEnv);
				}
			}
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

		if ( target instanceof CtVariableReadImpl || target instanceof CtThisAccessImpl){
			SymbolicValue sv;
			CtTypeReference<?> type;
			String name;
			if(target instanceof CtVariableReadImpl){
				CtVariableReadImpl<?> x = (CtVariableReadImpl<?>) target;
				type = x.getType();
				name = x.getVariable().getSimpleName();
				sv = symbEnv.get(x.getVariable().getSimpleName());
			} else {
				type = target.getType();
				sv = symbEnv.get(THIS);
				name = THIS;
			}

			// Δ(𝑥) = 𝜈 
			
			UniquenessAnnotation ua = permEnv.get(sv);
			// EVAL UNIQUE FIELD
			if ( ua.isGreaterEqualThan(Uniqueness.UNIQUE)) {
				logInfo("Eval Unique Field");
				SymbolicValue vp = symbEnv.get(sv, f.getSimpleName());
				// 𝜈.𝑓 ∉ Δ
				if (vp == null){
					//field(Γ(𝑥), 𝑓 ) = 𝛼 𝐶
					UniquenessAnnotation fieldUA = maps.getFieldAnnotation(f.getSimpleName(), type);
					if (fieldUA == null) logWarning(String.format("field annotation not found for %s", f.getSimpleName()));
					//----------------
					//𝜈.𝑓 : 𝜈′, Δ; 𝜈′: 𝛼, Σ   fresh 𝜈
					SymbolicValue vv = symbEnv.addField(sv, f.getSimpleName());
					permEnv.add(vv, fieldUA);

					// 𝑥 .𝑓 ⇓ 𝜈′
					fieldRead.putMetadata("symbolic_value", vv);
					logInfo(String.format("UniqueField read %s.%s: %s", name, f.getSimpleName(), vv));
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
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		logInfo("Visiting field write "+ fieldWrite.toStringDebug());
		super.visitCtFieldWrite(fieldWrite);
		CtExpression<?> ce = fieldWrite.getTarget();
		if (ce instanceof CtVariableReadImpl){
			CtVariableReadImpl<?> x = (CtVariableReadImpl<?>) ce;
			SymbolicValue v = symbEnv.get(x.getVariable().getSimpleName());
			ce.putMetadata("symbolic_value", v);
			logInfo("Field write target "+ x.getVariable().getSimpleName() + ": "+ v);
		} else if (ce instanceof CtThisAccessImpl){
			SymbolicValue v = symbEnv.get(THIS);
			ce.putMetadata("symbolic_value", v);
			logInfo("Field write target this: "+ v);
		} else {
			logWarning("Field write target not found");
		}
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
				logWarning("TODO: Handle constructor call with arguments");
			}
			

		} else if (target instanceof CtVariableWriteImpl){
			SymbolicValue v = (SymbolicValue) value.getMetadata("symbolic_value");
			if (v == null)
				logWarning("Symbolic value for assignment not found");
			symbEnv.addVarSymbolicValue(target.toString(), v);
			ClassLevelMaps.simplify(symbEnv, permEnv);

		} else if (target instanceof CtFieldWrite){ // CheckFieldAssign
			CtFieldWrite<?> fieldWrite = (CtFieldWrite<?>) target;
			logInfo("Visiting field write "+ fieldWrite.toStringDebug());
	
			CtExpression<?> x = fieldWrite.getTarget();
			CtFieldReference<?> f = fieldWrite.getVariable();
			CtTypeReference<?> ct = x.getType();
			// field(Γ(𝑥), 𝑓 ) = 𝛼 𝐶
			UniquenessAnnotation fieldPerm = maps.getFieldAnnotation(f.getSimpleName(), ct);
	
			// Γ; Δ; Σ ⊢ 𝑒 ⇓ 𝜈′ ⊣ Δ′; Σ′
			SymbolicValue vv = (SymbolicValue) value.getMetadata("symbolic_value");
			// Γ; Δ′; Σ′ ⊢ 𝑥 ⇓ 𝜈 ⊣ Δ′′; Σ′′
			SymbolicValue v = (SymbolicValue) x.getMetadata("symbolic_value"); 

			// Σ′′ ⊢ 𝜈′ : 𝛼 ⊣ Σ′′′
			UniquenessAnnotation vvPerm = permEnv.get(vv);
			// Check if we can use the permission of vv as

			if (!permEnv.usePermissionAs(v, vvPerm, fieldPerm))
				logError(String.format("Field %s has permission %s but value %s has permission %s", 
					f.getSimpleName(), fieldPerm, vv, vvPerm, assignment), assignment);
			
			// Δ′′ [𝜈.𝑓 → 𝜈′]; Σ′′′ ⪰ Δ′′′; Σ′′′′
			symbEnv.addFieldSymbolicValue(v, f.getSimpleName(), vv);
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
		logInfo("Binary operator "+ operator.toStringDebug() + ": "+ sv);
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
		logInfo("Unary operator "+ operator.toStringDebug() + ": "+ sv);
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
			logWarning(String.format("Symbolic value for local variable %s not found in the symbolic environment",
				reference.getSimpleName()));
		} else{
			UniquenessAnnotation ua = permEnv.get(sv);
			if (ua.isBottom()){
				logInfo(String.format("Symbolic value %s has bottom permission", sv));
			} else {
				reference.putMetadata("symbolic_value", sv);
				logInfo(String.format("Local variable reference %s: %s", reference.getSimpleName(), sv));
			}
		}
		loggingSpaces--;
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		loggingSpaces++;
		logInfo("Visiting variable read "+ variableRead.toString());
		super.visitCtVariableRead(variableRead);

		SymbolicValue sv = symbEnv.get(variableRead.getVariable().getSimpleName());
		variableRead.putMetadata("symbolic_value", sv);
		logInfo(variableRead.toString() + ": "+ sv);
		loggingSpaces--;
	}

	/**
	 * Rule EvalConst
	 * Visit a literal, add a symbolic value to the environment and a permission of shared
	 */
	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		logInfo("Visiting literal "+ literal.toString());
		
		super.visitCtLiteral(literal);

		// Get a fresh symbolic value and add it to the environment with a shared default value
		SymbolicValue sv = symbEnv.getFresh();
		UniquenessAnnotation ua = new UniquenessAnnotation(Uniqueness.SHARED);
		
		if (literal.getValue() == null){ // its a null literal
			ua = new UniquenessAnnotation(Uniqueness.UNIQUE);
		}

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);

		// Store the symbolic value in metadata
		literal.putMetadata("symbolic_value", sv);
		logInfo("Literal "+ literal.toString() + ": "+ sv);
	}

}
