package typechecking;

import java.util.ArrayList;
import java.util.List;

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
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
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
		logInfo("Visiting class: <" + ctClass.getSimpleName()+">");
		enterScopes();
		super.visitCtClass(ctClass);
		exitScopes();
	}
			
	
	@Override
	public <T> void visitCtConstructor(CtConstructor<T> c) {
		logInfo("Visiting constructor <"+ c.getSimpleName()+">");
		enterScopes();

		// Assume 'this' is a parameter always borrowed
		SymbolicValue thv = symbEnv.addVariable(THIS);
		permEnv.add(thv, new UniquenessAnnotation(Uniqueness.BORROWED));

		super.visitCtConstructor(c);

		exitScopes();
	}
	
	@Override
	public <T> void visitCtMethod(CtMethod<T> m) {
		logInfo("Visiting method <"+ m.getSimpleName()+">");
		enterScopes();

		// Assume 'this' is a parameter always borrowed
		SymbolicValue thv = symbEnv.addVariable(THIS);
		permEnv.add(thv, new UniquenessAnnotation(Uniqueness.BORROWED));

		super.visitCtMethod(m);
		exitScopes();
	}
	
	
	@Override
	public <T> void visitCtParameter(CtParameter<T> parameter) {
		logInfo("Visiting parameter <"+ parameter.getSimpleName()+">");
		loggingSpaces++;
		super.visitCtParameter(parameter);
		
		SymbolicValue sv = symbEnv.addVariable(parameter.getSimpleName());
		UniquenessAnnotation ua = new UniquenessAnnotation(parameter);
		permEnv.add(sv, ua);
		logInfo(parameter.getSimpleName() + ": "+ sv);
		logInfo(sv + ": "+ ua);

		loggingSpaces--;
	}


	/**
	 * Visit Local Variable that can have only the variable declaration, or also an assignment
	 * Rules: CheckVarDecl + CheckVarAssign + CheckNew
	 * CheckVarDecl
	 *                     fresh 𝜈
	 * -----------------------------------------------
	 * Γ; Δ; Σ ⊢ 𝐶 𝑥; ⊣ Γ[𝑥 ↦ → 𝐶]; 𝑥 : 𝜈, Δ; 𝑥 : ⊥, Σ
	 * 
	 * CheckVarAssign
	 * Γ(𝑥) = 𝐶 Γ ⊢ 𝑒 : 𝐶 Γ; Δ; Σ ⊢ 𝑒 ⇓ 𝜈 ⊣ Δ′; Σ′ Δ′ [𝑥 ↦ → 𝜈]; Σ′ ⪰ Δ′′; Σ′′
	 * ------------------------------------------------------------------------
	 *             Γ; Δ; Σ ⊢ 𝑥 = 𝑒; ⊣ Γ; Δ′′; Σ′′
	 * 
	 * 
	 * CheckNew
	 * ctor(𝐶) = 𝐶 (𝛼1 𝐶1 𝑥1, ..., 𝛼𝑛 𝐶𝑛 𝑥𝑛 )
	 * Γ ⊢ 𝑦 : 𝐶 Γ ⊢ 𝑒1, ..., 𝑒𝑛 : 𝐶1, ... , 𝐶𝑛
	 * Γ; Δ; Σ ⊢ 𝑒1, ... , 𝑒𝑛 ⇓ 𝜈1, ... , 𝜈𝑛 ⊣ Γ′; Δ′; Σ′ Σ′ ⊢ 𝑒1, ... , 𝑒𝑛 : 𝛼1, ... , 𝛼𝑛 ⊣ Σ′′
	 * distinct(Δ′, {𝜈𝑖 : borrowed ≤ 𝛼𝑖 }) fresh 𝜈′
	 * Δ′ [𝑦 ↦ → 𝜈′]; Σ′′ [𝜈 ↦ → free] ⪰ Δ′′; Σ′′′
	 * ------------------------------------------------------
	 * Γ; Δ; Σ ⊢ 𝑦 = new 𝐶 (𝑒1, ..., 𝑒𝑛 ); ⊣ Γ; Δ′′; Σ′′′
	 * 
	 * TODO: CheckCall
	 */
	@Override
	public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
		logInfo("Visiting local variable <"+ localVariable.getSimpleName() +">");
		loggingSpaces++;
		// CheckVarDecl
		// 1) Add the variable to the typing context
		CtTypeReference<?> t = localVariable.getType();
		String name = localVariable.getSimpleName();
		CtClass<?> ctClass = maps.getClassFrom(t);
		typeEnv.add(name, ctClass);
		SymbolicValue v = symbEnv.addVariable(name);
		permEnv.add(v, new UniquenessAnnotation(Uniqueness.BOTTOM));

		// 2) Visit
		super.visitCtLocalVariable(localVariable);

		// CheckVarAssign
		// 3) Handle assignment
		CtElement value = localVariable.getAssignment();
		if (value != null){
			SymbolicValue vValue = (SymbolicValue) value.getMetadata("symbolic_value");
			if (vValue == null) 
				logWarning(String.format("Local variable %s = %s has assignment with null symbolic value", name, 
					localVariable.getAssignment().toString()));
			else{
				// Constructor call - CheckNew
				if (value instanceof CtConstructorCallImpl ){
					CtConstructorCallImpl<?> constCall = (CtConstructorCallImpl<?>) value;
					// Check if all arguments follow the restrictions
					if (constCall.getArguments().size() > 0)
						handleConstructorArgs(constCall);
					// Add the variable to the environment
					SymbolicValue vv = symbEnv.addVariable(localVariable.getSimpleName());
					permEnv.add(vv, new UniquenessAnnotation(Uniqueness.FREE));
					ClassLevelMaps.simplify(symbEnv, permEnv);
					
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
		logInfo("Visiting field read <"+ fieldRead.toStringDebug()+">");
		loggingSpaces++;

		super.visitCtFieldRead(fieldRead);
		CtExpression<?> target = fieldRead.getTarget();
		CtFieldReference<?> f = fieldRead.getVariable();

		if ( target instanceof CtVariableReadImpl || target instanceof CtThisAccessImpl){
			SymbolicValue v;
			CtTypeReference<?> type = target.getType();
			v = (target instanceof CtVariableReadImpl) ? 
				symbEnv.get(((CtVariableReadImpl<?>)target).getVariable().getSimpleName()) : 
				symbEnv.get(THIS);

			// Δ(𝑥) = 𝜈 
			UniquenessAnnotation permV = permEnv.get(v);
			SymbolicValue vv = symbEnv.get(v, f.getSimpleName());
			// EVAL UNIQUE FIELD
			// 𝜈.𝑓 ∉ Δ
			if ( permV.isGreaterEqualThan(Uniqueness.UNIQUE) && vv == null) {
				//field(Γ(𝑥), 𝑓 ) = 𝛼 𝐶
				UniquenessAnnotation fieldUA = maps.getFieldAnnotation(f.getSimpleName(), type);
				if (fieldUA == null) logWarning(String.format("field annotation not found for %s", f.getSimpleName()));
				//----------------
				//𝜈.𝑓 : 𝜈′, Δ; 𝜈′: 𝛼, Σ   fresh 𝜈
				vv = symbEnv.addField(v, f.getSimpleName());
				permEnv.add(vv, fieldUA);

				// 𝑥 .𝑓 ⇓ 𝜈′
				fieldRead.putMetadata("symbolic_value", vv);
				logInfo(String.format("%s.%s: %s", v, f.getSimpleName(), vv));
			} else if ( permV.isGreaterEqualThan(Uniqueness.SHARED) && vv == null){
				// TODO: complete
			} else {
				//EVAL FIELD
				// Σ(𝜈) ≠ ⊥ 
				if (permV.isBottom()){
					logError(
						String.format("%s:%s is not accepted in field evaluation", v, permV)
						, fieldRead);
				}
				
				// Δ(𝜈.𝑓 ) = 𝜈′, if not present, add it 
				if (vv == null){
					symbEnv.addField(vv, f.getSimpleName());
					logError(String.format("Could not find symbolic value for %s.%s", v, f.getSimpleName())
						, fieldRead);
				}

				// Σ(𝜈′) ≠ ⊥
				UniquenessAnnotation permVV = permEnv.get(vv);
				if (permVV.isBottom()){
					logError(
						String.format("%s:%s is not accepted in field evaluation", vv, permVV)
						, fieldRead);
				}
				fieldRead.putMetadata("symbolic_value", vv);
				logInfo(String.format("%s.%s: %s", v, f.getSimpleName(), vv));
			}


		} 
		loggingSpaces--;
	}

	/**
	 * Visit a field write as a field assignment
	 * 
	 * CheckFieldAssign
	 * field(Γ(𝑥), 𝑓 ) = 𝛼 𝐶 Γ ⊢ 𝑒 : 𝐶 Γ; Δ; Σ ⊢ 𝑒 ⇓ 𝜈′ ⊣ Δ′; Σ′
	 * Γ; Δ′; Σ′ ⊢ 𝑥 ⇓ 𝜈 ⊣ Δ′′; Σ′′ Σ′′ ⊢ 𝜈′ : 𝛼 ⊣ Σ′′′ Δ′′ [𝜈.𝑓 ↦ → 𝜈′]; Σ′′′ ⪰ Δ′′′; Σ′′′′
	 * --------------------------------------------------------------------------------------
	 * Γ; Δ; Σ ⊢ 𝑥 .𝑓 = 𝑒; ⊣ Γ; Δ′′′; Σ′′′′
	 */
	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		logInfo("Visiting field write <"+ fieldWrite.toStringDebug()+">");
		super.visitCtFieldWrite(fieldWrite);
		CtExpression<?> ce = fieldWrite.getTarget();
		if (ce instanceof CtVariableReadImpl){
			CtVariableReadImpl<?> x = (CtVariableReadImpl<?>) ce;
			SymbolicValue v = symbEnv.get(x.getVariable().getSimpleName());
			ce.putMetadata("symbolic_value", v);
			logInfo(x.getVariable().getSimpleName() + ": "+ v);
		} else if (ce instanceof CtThisAccessImpl){
			SymbolicValue v = symbEnv.get(THIS);
			ce.putMetadata("symbolic_value", v);
			logInfo("this: "+ v);
		} else {
			logWarning("Field write target not found");
		}
	}

	/**
	 * Visit CTAssignment that can have a call, a new object, or an expression assignment
	 * Rules: CheckVarAssign + CheckNew + CheckCall
	 * 
	 * CheckVarAssign
	 * Γ(𝑥) = 𝐶 Γ ⊢ 𝑒 : 𝐶 Γ; Δ; Σ ⊢ 𝑒 ⇓ 𝜈 ⊣ Δ′; Σ′ Δ′ [𝑥 ↦ → 𝜈]; Σ′ ⪰ Δ′′; Σ′′
	 * ------------------------------------------------------------------------
	 *             Γ; Δ; Σ ⊢ 𝑥 = 𝑒; ⊣ Γ; Δ′′; Σ′′
	 * 
	 * 
	 * CheckNew
	 * ctor(𝐶) = 𝐶 (𝛼1 𝐶1 𝑥1, ..., 𝛼𝑛 𝐶𝑛 𝑥𝑛 )
	 * Γ ⊢ 𝑦 : 𝐶 Γ ⊢ 𝑒1, ..., 𝑒𝑛 : 𝐶1, ... , 𝐶𝑛
	 * Γ; Δ; Σ ⊢ 𝑒1, ... , 𝑒𝑛 ⇓ 𝜈1, ... , 𝜈𝑛 ⊣ Γ′; Δ′; Σ′ Σ′ ⊢ 𝑒1, ... , 𝑒𝑛 : 𝛼1, ... , 𝛼𝑛 ⊣ Σ′′
	 * distinct(Δ′, {𝜈𝑖 : borrowed ≤ 𝛼𝑖 }) fresh 𝜈′
	 * Δ′ [𝑦 ↦ → 𝜈′]; Σ′′ [𝜈 ↦ → free] ⪰ Δ′′; Σ′′′
	 * ------------------------------------------------------
	 * Γ; Δ; Σ ⊢ 𝑦 = new 𝐶 (𝑒1, ..., 𝑒𝑛 ); ⊣ Γ; Δ′′; Σ′′′
	 * 
	 * TODO: CheckCall
	 */
	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignment) {
		logInfo("Visiting assignment <"+ assignment.toStringDebug()+">");
		loggingSpaces++;
		super.visitCtAssignment(assignment);

		CtExpression<?> target = assignment.getAssigned();
		CtExpression<?> value = assignment.getAssignment();


		//TODO: CheckCall

		// Constructor Call - CheckNew
		// x = new C(e1, ..., en)
		if (value instanceof CtConstructorCallImpl && target instanceof CtVariableWriteImpl){
			CtVariableWriteImpl<?> y = (CtVariableWriteImpl<?>) target;
			CtConstructorCallImpl<?> constCall = (CtConstructorCallImpl<?>) value;

			// Check all arguments follow the restrictions
			if(constCall.getArguments().size() > 0)
				handleConstructorArgs(constCall);
			
			// Add a new variable for this assignment
			SymbolicValue vv = symbEnv.addVariable(y.getVariable().getSimpleName());
			permEnv.add(vv, new UniquenessAnnotation(Uniqueness.FREE));
			ClassLevelMaps.simplify(symbEnv, permEnv);

		// Variable Assignment - CheckVarAssign
		} else if (target instanceof CtVariableWriteImpl){
			SymbolicValue v = (SymbolicValue) value.getMetadata("symbolic_value");
			if (v == null)
				logWarning("Symbolic value for assignment not found");
			symbEnv.addVarSymbolicValue(target.toString(), v);
			ClassLevelMaps.simplify(symbEnv, permEnv);

		// Field Assignment - CheckFieldAssign
		} else if (target instanceof CtFieldWrite){
			CtFieldWrite<?> fieldWrite = (CtFieldWrite<?>) target;
			logInfo("Visiting field write <"+ fieldWrite.toStringDebug()+">");
	
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

			// Check if we can use the permission of vv as the permission of the field
			if (!permEnv.usePermissionAs(v, vvPerm, fieldPerm))
				logError(String.format("Field %s expected an assignment with permission %s but got %s from %s", 
					f.getSimpleName(), fieldPerm, vvPerm, vv, assignment), assignment);
			
			// Δ′′ [𝜈.𝑓 → 𝜈′]; Σ′′′ ⪰ Δ′′′; Σ′′′′
			symbEnv.addFieldSymbolicValue(v, f.getSimpleName(), vv);
			ClassLevelMaps.simplify(symbEnv, permEnv);
		}

		loggingSpaces--;
	}

	/**
	 * Handle the constructor with arguments
	 * 
	 * CheckNew
	 * ctor(𝐶) = 𝐶 (𝛼1 𝐶1 𝑥1, ..., 𝛼𝑛 𝐶𝑛 𝑥𝑛 )
	 * Γ ⊢ 𝑦 : 𝐶 Γ ⊢ 𝑒1, ..., 𝑒𝑛 : 𝐶1, ... , 𝐶𝑛
	 * Γ; Δ; Σ ⊢ 𝑒1, ... , 𝑒𝑛 ⇓ 𝜈1, ... , 𝜈𝑛 ⊣ Γ′; Δ′; Σ′ 
	 * Σ′ ⊢ 𝑒1, ... , 𝑒𝑛 : 𝛼1, ... , 𝛼𝑛 ⊣ Σ′′
	 * distinct(Δ′, {𝜈𝑖 : borrowed ≤ 𝛼𝑖 }) fresh 𝜈′
	 * Δ′ [𝑦 → 𝜈′]; Σ′′ [𝜈 ↦ → free] ⪰ Δ′′; Σ′′′
	 * ------------------------------------------------------
	 * Γ; Δ; Σ ⊢ 𝑦 = new 𝐶 (𝑒1, ..., 𝑒𝑛 ); ⊣ Γ; Δ′′; Σ′′′

	 * @param constCall
	 */
	private void handleConstructorArgs (CtConstructorCall<?> constCall){
		CtClass<?> klass = maps.getClassFrom(constCall.getType());
		int paramSize = constCall.getArguments().size();
		CtConstructor<?> c = maps.geCtConstructor(klass, paramSize);
		List<SymbolicValue> paramSymbValues = new ArrayList<>();
		for (int i = 0; i < paramSize; i++){
			CtExpression<?> arg = constCall.getArguments().get(i);
			// Γ; Δ; Σ ⊢ 𝑒1, ... , 𝑒𝑛 ⇓ 𝜈1, ... , 𝜈𝑛 ⊣ Γ′; Δ′; Σ′ 
			SymbolicValue vv = (SymbolicValue) arg.getMetadata("symbolic_value");
			if (vv == null) logWarning("Symbolic value for constructor argument not found");
			
			CtParameter<?> p = c.getParameters().get(i);
			UniquenessAnnotation expectedUA = new UniquenessAnnotation(p);
			UniquenessAnnotation vvPerm = permEnv.get(vv);
			// {𝜈𝑖 : borrowed ≤ 𝛼𝑖 }
			if (!vvPerm.isGreaterEqualThan(Uniqueness.BORROWED)){
				logError(String.format("Symbolic value %s:%s is not greater than BORROWED", vv, vvPerm), arg);
			}
			logInfo(String.format("Checking constructor argument %s:%s, %s <= %s", p.getSimpleName(), vv, vvPerm, expectedUA));
			// Σ′ ⊢ 𝑒1, ... , 𝑒𝑛 : 𝛼1, ... , 𝛼𝑛 ⊣ Σ′′
			if (!permEnv.usePermissionAs(vv, vvPerm, expectedUA))
				logError(String.format("Constructor argument %s expected an assignment with permission %s but got %s from %s", 
					p.getSimpleName(), expectedUA, permEnv.get(vv), vv), arg);
			paramSymbValues.add(vv);
		}

		// distinct(Δ′, {𝜈𝑖 : borrowed ≤ 𝛼𝑖 })
		//distinct(Δ, 𝑆) ⇐⇒ ∀𝜈, 𝜈′ ∈ 𝑆 : Δ ⊢ 𝜈 ⇝ 𝜈′ =⇒ 𝜈 = 𝜈′
		if (!symbEnv.distinct(paramSymbValues)){
			logError(String.format("Non-distinct parameters in constructor call of %s", klass.getSimpleName()), constCall);
		}
		logInfo("all distinct");
	}


	/**
	 * Rule EvalBinary
	 */
	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		logInfo("Visiting binary operator <"+ operator.toStringDebug()+">");
		loggingSpaces++;
		super.visitCtBinaryOperator(operator);

		// Get a fresh symbolic value and add it to the environment with a shared default value
		SymbolicValue sv = symbEnv.getFresh();
		UniquenessAnnotation ua = new UniquenessAnnotation(Uniqueness.SHARED);

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);

		// Store the symbolic value in metadata
		operator.putMetadata("symbolic_value", sv);
		logInfo(operator.toStringDebug() + ": "+ sv);
		loggingSpaces--;
	}

	/**
	 * Rule EvalUnary
	 */
	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		logInfo("Visiting unary operator <"+ operator.toStringDebug()+">");
		loggingSpaces++;
		super.visitCtUnaryOperator(operator);

		// Get a fresh symbolic value and add it to the environment with a shared default value
		SymbolicValue sv = symbEnv.getFresh();
		UniquenessAnnotation ua = new UniquenessAnnotation(Uniqueness.SHARED);

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);
		
		// Store the symbolic value in metadata
		operator.putMetadata("symbolic_value", sv);
		logInfo(operator.toStringDebug() + ": "+ sv);
		loggingSpaces--;
	}

	/**
	 * Rule EvalVar
	 */
	@Override
	public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
		logInfo("Visiting local variable reference <"+ reference.toString()+">");
		loggingSpaces++;
		super.visitCtLocalVariableReference(reference);
		
		SymbolicValue sv = symbEnv.get(reference.getSimpleName());
		if (sv == null) {
			logWarning(String.format("Symbolic value for local variable %s not found in the symbolic environment",
				reference.getSimpleName()));
		} else{
			UniquenessAnnotation ua = permEnv.get(sv);
			if (ua.isBottom()){
				logInfo(String.format("%s: %s", sv, ua));
			} else {
				reference.putMetadata("symbolic_value", sv);
				logInfo(String.format("%s: %s", reference.getSimpleName(), sv));
			}
		}
		loggingSpaces--;
	}

	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		loggingSpaces++;
		logInfo("Visiting variable read <"+ variableRead.toString()+">");
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
		logInfo("Visiting literal <"+ literal.toString()+">");
		
		super.visitCtLiteral(literal);

		// Get a fresh symbolic value and add it to the environment with a shared default value
		SymbolicValue sv = symbEnv.getFresh();
		UniquenessAnnotation ua = new UniquenessAnnotation(Uniqueness.SHARED);
		
		if (literal.getValue() == null){ // its a null literal
			ua = new UniquenessAnnotation(Uniqueness.FREE);
		}

		// Add the symbolic value to the environment with a shared default value
		permEnv.add(sv, ua);

		// Store the symbolic value in metadata
		literal.putMetadata("symbolic_value", sv);
		logInfo("Literal "+ literal.toString() + ": "+ sv);
	}

}
