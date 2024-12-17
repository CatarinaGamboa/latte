package context;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PermissionEnvironment {
    
    private static PermissionEnvironment instance;

    private Stack<Map<SymbolicValue, UniquenessAnnotation>> map;


    /**
     * Singleton instance
     * @return
     */
    public static PermissionEnvironment getInstance() {
        if (instance == null) instance = new PermissionEnvironment();
        return instance;
    }

    private PermissionEnvironment() {
        map = new Stack<Map<SymbolicValue, UniquenessAnnotation>>(); 
    }

    public void add(SymbolicValue symb, UniquenessAnnotation ann) {
        map.peek().put(symb, ann);
    }

    public UniquenessAnnotation get(SymbolicValue symb) {
        for (int i = map.size() - 1; i >= 0; i--) {
            if (map.get(i).containsKey(symb)) {
                return map.get(i).get(symb);
            }
        }
        return null;
    }

    /**
	 * Enter a new scope
	 */
	public void enterScope() {
		map.push(new HashMap<SymbolicValue, UniquenessAnnotation>());
	}
	
	/**
	 * Exit the current scope
	 */
	public void exitScope() {
		map.pop();
	}
}
