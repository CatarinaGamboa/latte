package context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

public class ClassLevelMaps {
   
    static ClassLevelMaps instance;
    Map<CtTypeReference<?>, CtClass<?>> typeClassMap;
    Map<CtClass<?>, Map<String, CtField<?>>> classFields;
    Map<CtClass<?>, Map<Integer, CtConstructor<?>>> classConstructors;
    Map<CtClass<?>, Map<Pair<String, Integer>, CtMethod<?>>> classMethods;
    


	/**
	 * Singleton instance
	 * @return
	 */
	public static ClassLevelMaps getInstance() {
		if (instance == null) instance = new ClassLevelMaps();
		return instance;
	}

    private ClassLevelMaps() {
        typeClassMap = new HashMap<CtTypeReference<?>, CtClass<?>>();
        classFields = new HashMap<CtClass<?>, Map<String, CtField<?>>>();
        classConstructors = new HashMap<>();
        classMethods = new HashMap<>();
    }

    public CtClass<?> getClassFrom(CtTypeReference<?> type) {
        return typeClassMap.get(type);
    }

    public void addTypeClass(CtTypeReference<?> type, CtClass<?> klass) {
        typeClassMap.put(type, klass);
    } 

    public void addConstructor(CtClass<?> klass, CtConstructor<?> constructor) {
        int params = constructor.getParameters().size();
        if (classConstructors.containsKey(klass)){
            Map<Integer, CtConstructor<?>> m = classConstructors.get(klass);
            m.put(params, constructor);
        } else {
            Map<Integer, CtConstructor<?>> m = new HashMap<Integer, CtConstructor<?>>();
            m.put(params, constructor);
            classConstructors.put(klass, m);
        }
    }

    public void addMethod(CtClass<?> klass, CtMethod<?> method) {
        Pair<String, Integer> mPair = Pair.of(method.getSimpleName(), method.getParameters().size());
        if (classMethods.containsKey(klass)){
            Map<Pair<String, Integer>, CtMethod<?>> m = classMethods.get(klass);
            m.put(mPair, method);
        } else {
            Map<Pair<String, Integer>, CtMethod<?>> m = new HashMap<Pair<String, Integer>, CtMethod<?>>();
            m.put(mPair, method);
            classMethods.put(klass, m);
        }
    }

    public void addFieldClass(CtField<?> field, CtClass<?> klass) {
        if (classFields.containsKey(klass)){
            Map<String, CtField<?>> m = classFields.get(klass);
            m.put(field.getSimpleName(), field);
        } else {
            Map<String, CtField<?>> m = new HashMap<String, CtField<?>>();
            m.put(field.getSimpleName(), field);
            classFields.put(klass, m);
        }
    }

    public UniquenessAnnotation getFieldAnnotation(String fieldName, CtTypeReference<?> type) {
        CtClass<?> klass = getClassFrom(type);
        if (classFields.containsKey(klass)){
            Map<String, CtField<?>> m = classFields.get(klass);
            if (m.containsKey(fieldName)){
                CtField<?> field = m.get(fieldName);
                UniquenessAnnotation annotation = new UniquenessAnnotation(field);
                return annotation;
            }
        }
        return null;
    }

    public CtConstructor<?> geCtConstructor (CtClass<?> klass, int numParams){
        if (classConstructors.containsKey(klass)){
            Map<Integer, CtConstructor<?>> l = classConstructors.get(klass);
            if (l.containsKey(numParams)){
                return l.get(numParams);
            }
        }
        return null;
    }

    public CtMethod<?> getCtMethod(CtClass<?> klass, String methodName, int numParams){
        Pair<String, Integer> mPair = Pair.of(methodName, numParams);
        if (classMethods.containsKey(klass)){
            Map<Pair<String, Integer>, CtMethod<?>> m = classMethods.get(klass);
            if (m.containsKey(mPair)){
                return m.get(mPair);
            }
        }
        return null;
    } 

    public static void simplify(SymbolicEnvironment symbEnv, PermissionEnvironment permEnv) {
        // 1) Remove unreachable values
        List<SymbolicValue> removed = symbEnv.removeUnreachableValues();

        // 2) Remove the values from permEnv
        permEnv.removeValues(removed);

        // Unique Values
        // 1) get all the symbolic values v that are unique in permEnv
        List<SymbolicValue> lsv = permEnv.getUniqueValues();

        // 2) for each of lsv, check if any of the values in symbEnv are equal to v
       for (SymbolicValue v : lsv) {
            // SimpUnique - no field with v as the symbolic value
            if (!symbEnv.hasFieldWithValue(v)){
                // 3) if no, v can be free in permEnv
                permEnv.add(v, new UniquenessAnnotation(Uniqueness.FREE));
            }
       }
    }


	public static void joinDropVar(SymbolicEnvironment symbEnv, SymbolicEnvironment  symb) {
		List<VariableHeapLoc> remove = new ArrayList<>(); 
		for (VariableHeapLoc v : symb.keySet()) 
			if (!symbEnv.contains(v))
				remove.add(v);
		for (VariableHeapLoc v : remove)
			symb.remove(v);
	}


    public static void joinDropField(SymbolicEnvironment symbEnv) {
        List<VariableHeapLoc> remove = new ArrayList<>(); 
        for (VariableHeapLoc v : symbEnv.keySet()) 
            if (v instanceof FieldHeapLoc)
                remove.add(v);
        for (VariableHeapLoc v : remove)
            symbEnv.remove(v);
    }
			
	public static void joinUnify( 
        SymbolicEnvironment symbEnv,     PermissionEnvironment permEnv,
        SymbolicEnvironment thenSymbEnv, PermissionEnvironment thenPermEnv, 
		SymbolicEnvironment elseSymbEnv, PermissionEnvironment elsePermEnv) {

        // System.out.println("symbolic env: " + symbEnv);
        // System.out.println("permission env: " + permEnv);

        // System.out.println("then symbolic env: " + thenSymbEnv);
        // System.out.println("then permission env: " + thenPermEnv);

        // System.out.println("else symbolic env: " + elseSymbEnv);
        // System.out.println("else permission env: " + elsePermEnv);
		
        // JoinUnifyVar
		for(VariableHeapLoc v: thenSymbEnv.keySet()){
			if(v instanceof Variable){
                Variable x = (Variable) v;
                if( elseSymbEnv.contains(x)){
                    SymbolicValue v1 = thenSymbEnv.get(x), 
                        v2 = elseSymbEnv.get(x);

                    // Σ1 (𝜈1) ∧ Σ2 (𝜈2) ⇛ 𝛼
                    UniquenessAnnotation freshUA = 
                        UniquenessAnnotation.unifyAnnotation(thenPermEnv.get(v1), elsePermEnv.get(v2));

                    // fresh 𝜈
                    SymbolicValue freshV = symbEnv.getFresh();
                    // Δ; Σ ⊢ Δ1 [𝜈/𝜈1]; 𝜈: 𝛼, Σ1 ∧ Δ2 [𝜈/𝜈2]; 𝜈: 𝛼, Σ2 ⇛ Δ′; Σ′
                    thenSymbEnv.update(x, freshV);
                    elseSymbEnv.update(x, freshV);

                    thenPermEnv.add(freshV, freshUA);
                    elsePermEnv.add(freshV, freshUA);
                }
            } else {
                // JoinUnifyField
                FieldHeapLoc x = (FieldHeapLoc) v;
                if( elseSymbEnv.contains(x)){
                    SymbolicValue v1 = thenSymbEnv.get(x), v2 = elseSymbEnv.get(x);
                    // Σ1 (𝜈1) ∧ Σ2 (𝜈2) ⇛ 𝛼
                    UniquenessAnnotation freshUA = 
                        UniquenessAnnotation.unifyAnnotation(thenPermEnv.get(v1), elsePermEnv.get(v2));

                    // fresh 𝜈
                    SymbolicValue freshV = symbEnv.getFresh();
                    // Δ; Σ ⊢ Δ1 [𝜈/𝜈1]; 𝜈: 𝛼, Σ1 ∧ Δ2 [𝜈/𝜈2]; 𝜈: 𝛼, Σ2 ⇛ Δ′; Σ′
                    thenSymbEnv.update(x, freshV);
                    elseSymbEnv.update(x, freshV);

                    thenPermEnv.add(freshV, freshUA);
                    elsePermEnv.add(freshV, freshUA);
                }
            }
		}
        // System.out.println("--------------------------------------------------");
        // System.out.println("--------------------------------------------------");
        // System.out.println("symbolic env: " + symbEnv);
        // System.out.println("permission env: " + permEnv);

        // System.out.println("then symbolic env: " + thenSymbEnv);
        // System.out.println("then permission env: " + thenPermEnv);

        // System.out.println("else symbolic env: " + elseSymbEnv);
        // System.out.println("else permission env: " + elsePermEnv);
	}

    public static void joinElim( 
        SymbolicEnvironment symbEnv,     PermissionEnvironment permEnv,
        SymbolicEnvironment thenSymbEnv, PermissionEnvironment thenPermEnv, 
		SymbolicEnvironment elseSymbEnv, PermissionEnvironment elsePermEnv) {
        
        // System.out.println("Started elim");
        
        for ( VariableHeapLoc v: thenSymbEnv.keySet()){
            if( v instanceof Variable){
                // joinElimVar
                Variable x = (Variable) v;
                if(elseSymbEnv.contains(x) && symbEnv.contains(x)){
                    SymbolicValue v1 = thenSymbEnv.get(x), v2 = elseSymbEnv.get(x);
                    UniquenessAnnotation ua1 = thenPermEnv.get(v1), ua2 = elsePermEnv.get(v2);
                    if (v1.equals(v2) && ua1.equals(ua2)){
                        symbEnv.update(x, v1);
                        permEnv.add(v1, ua1);
                    }
                }
            } else {
                // joinElimField
                FieldHeapLoc x = (FieldHeapLoc) v;
                if(elseSymbEnv.contains(x)){
                    SymbolicValue v1 = thenSymbEnv.get(x), v2 = elseSymbEnv.get(x);
                    UniquenessAnnotation ua1 = thenPermEnv.get(v1), ua2 = elsePermEnv.get(v2);
                    if (v1.equals(v2) && ua1.equals(ua2)){
                        symbEnv.add(x, v1);
                        permEnv.add(v1, ua1);
                    }
                } else {
                    // joinDropFieldInner
                    SymbolicValue v1 = thenSymbEnv.get(x);
                    permEnv.add(v1, new UniquenessAnnotation(Uniqueness.BOTTOM));                    
                }
            }
        }

        for ( VariableHeapLoc v: elseSymbEnv.keySet()){
            if(v instanceof FieldHeapLoc)
                if (!thenSymbEnv.contains(v)){
                    // joinDropFieldOuter
                    SymbolicValue v1 = elseSymbEnv.get(v);
                    permEnv.add(v1, new UniquenessAnnotation(Uniqueness.BOTTOM));
                }
        }

        // System.out.println("--------------------------------------------------");
        // System.out.println("--------------------------------------------------");
        // System.out.println("symbolic env: " + symbEnv);
        // System.out.println("permission env: " + permEnv);
	}



}
