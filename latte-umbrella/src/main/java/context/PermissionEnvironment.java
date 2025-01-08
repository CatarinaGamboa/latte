package context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Permission Environment class to store the permissions of the variables in scope
 * Σ ::= ∅ | 𝜈: 𝛼 | 𝜈: ⊥
 */
public class PermissionEnvironment {
    
    private static PermissionEnvironment instance;

    private List<Map<SymbolicValue, UniquenessAnnotation>> map;


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

    /**
     * Add a new symbolic value to the environment
     * @param symb
     * @param ann
     */
    public void add(SymbolicValue symb, UniquenessAnnotation ann) {
        map.getFirst().put(symb, ann);
    }

    /**
     * Removes the symbolic value in the key from the environment
     * @param symb
     */
    public void remove(SymbolicValue symb) {
        for (Map<SymbolicValue, UniquenessAnnotation> innerMap : map) {
            if (innerMap.containsKey(symb)) {
                innerMap.remove(symb);
                return;
            }
        }
    }

    /**
     * Get the permission of the symbolic value
     * @param symb
     * @return the permission of the symbolic value or null if it does not exist
     */
    public UniquenessAnnotation get(SymbolicValue symb) {
        for (Map<SymbolicValue, UniquenessAnnotation> innerMap : map) {
            if (innerMap.containsKey(symb)) {
                return innerMap.get(symb);
            }
        }
        return null;
    }


    /**
     * Get all symbolic values with Unique as the permission
     * @return
     */
    public List<SymbolicValue> getUniqueValues() {
        List<SymbolicValue> values = new ArrayList<SymbolicValue>();
        map.forEach( innerMap -> {
            innerMap.keySet().forEach(key -> {
                if (innerMap.get(key).isUnique()) {
                    values.add(key);
                }
            });
        });
        return values;
    }

    /**
     * Remove the values of the given list from the environment
     * @param removed
     */
    public void removeValues(List<SymbolicValue> removed) {
        for (SymbolicValue symbolicValue : removed) 
            remove(symbolicValue);
    }

    /**
	 * Enter a new scope
	 */
	public void enterScope() {
		map.addFirst(new HashMap<SymbolicValue, UniquenessAnnotation>());
	}
	
	/**
	 * Exit the current scope
	 */
	public void exitScope() {
		map.removeFirst();
	}

    /**
     * String representation of the Permission Environment
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Symbolic Value to Uniqueness Annotations:\n");

        for (int i = 0; i < map.size(); i++) {
            Map<SymbolicValue, UniquenessAnnotation> currentMap = map.get(i);
            sb.append("  Map ").append(i + 1).append(":\n");

            for (Map.Entry<SymbolicValue, UniquenessAnnotation> entry : currentMap.entrySet()) {
                sb.append("    ")
                .append(entry.getKey().toString()) // Key
                .append(" -> ")
                .append(entry.getValue().toString()) // Value
                .append("\n");
            }
        }

        return sb.toString();
    }

}
