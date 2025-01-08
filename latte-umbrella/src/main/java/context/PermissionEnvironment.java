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

    public void add(SymbolicValue symb, UniquenessAnnotation ann) {
        map.getLast().put(symb, ann);
    }

    public void remove(SymbolicValue symb) {
        //removes the symbolic value with the key symb
        map.getLast().remove(symb);
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
     * Get all unique values 
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

    public void removeValues(List<SymbolicValue> removed) {
        for (SymbolicValue symbolicValue : removed) 
            remove(symbolicValue);
    }

    /**
	 * Enter a new scope
	 */
	public void enterScope() {
		map.add(new HashMap<SymbolicValue, UniquenessAnnotation>());
	}
	
	/**
	 * Exit the current scope
	 */
	public void exitScope() {
		map.removeLast();
	}

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
