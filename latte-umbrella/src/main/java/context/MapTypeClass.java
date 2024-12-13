package context;

import java.util.HashMap;
import java.util.Map;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.reference.CtTypeReference;

public class MapTypeClass {
   
    MapTypeClass instance;
    Map<CtTypeReference<?>, CtClass<?>> map;

	/**
	 * Singleton instance
	 * @return
	 */
	public MapTypeClass getInstance() {
		if (instance == null) instance = new MapTypeClass();
		return instance;
	}

    public MapTypeClass() {
        map = new HashMap<CtTypeReference<?>, CtClass<?>>();
    }

    public CtClass<?> getClassFrom(CtTypeReference<?> type) {
        return map.get(type);
    }
}
