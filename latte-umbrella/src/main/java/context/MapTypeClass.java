package context;

import java.util.HashMap;
import java.util.Map;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;

public class MapTypeClass {
   
    static MapTypeClass instance;
    Map<CtTypeReference<?>, CtClass<?>> map;

	/**
	 * Singleton instance
	 * @return
	 */
	public static MapTypeClass getInstance() {
		if (instance == null) instance = new MapTypeClass();
		return instance;
	}

    private MapTypeClass() {
        map = new HashMap<CtTypeReference<?>, CtClass<?>>();
    }

    public CtClass<?> getClassFrom(CtTypeReference<?> type) {
        return map.get(type);
    }

    public void add(CtTypeReference<?> type, CtClass<?> klass) {
        map.put(type, klass);
    } 
}
