package context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

public class ClassLevelMaps {
   
    static ClassLevelMaps instance;
    Map<CtTypeReference<?>, CtClass<?>> typeClassMap;
    Map<CtClass<?>, Map<String, CtField<?>>> classFields;


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
    }

    public CtClass<?> getClassFrom(CtTypeReference<?> type) {
        return typeClassMap.get(type);
    }

    public void addTypeClass(CtTypeReference<?> type, CtClass<?> klass) {
        typeClassMap.put(type, klass);
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

    public static void simplify(SymbolicEnvironment symbEnv, PermissionEnvironment permEnv) {
        // 1) get all the symbolic values v that are unique in permEnv
        List<SymbolicValue> lsv = permEnv.getUniqueValues();

        // 2) for each of lsv, check if any of the values in symbEnv are equal to v
       for (SymbolicValue v : lsv) {
            if (!symbEnv.hasValue(v)){
                // 3) if no, v can be free in permEnv
                permEnv.add(v, new UniquenessAnnotation(Uniqueness.FREE));
            }
       }
    }

}
