package context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.javatuples.Pair;

/**
 * Symbolic Environment class to store the variables to their symbolic values
 * Δ ::= ∅ | 𝑥: 𝜈, Δ | 𝜈.𝑓 : 𝜈, Δ
 */
public class SymbolicEnvironment {
	
	// Singleton instance
	private static SymbolicEnvironment instance;

	int symbolic_counter = 0;

    private LinkedList<Map<VariableHeapLoc, SymbolicValue>> symbEnv;

	/**
	 * Singleton instance
	 * @return
	 */
	public static SymbolicEnvironment getInstance() {
		if (instance == null) instance = new SymbolicEnvironment();
		return instance;
	}

	private SymbolicEnvironment() {
		symbEnv = new LinkedList<Map<VariableHeapLoc, SymbolicValue>>();
	}

	public SymbolicValue addVariable(String var) {
		Variable v = new Variable(var);
		return add(v);
	}

	public SymbolicValue addField(SymbolicValue symb, String field) {
		return add(new FieldHeapLoc(symb, new Variable(field)));
	}


	/**
	 * Add a new variable or heap location to the environment
	 * @param var
	 * @return
	 */
	private SymbolicValue add(VariableHeapLoc var) {
		SymbolicValue symb = getFresh();
		symbEnv.getFirst().put(var, symb);
		return symb;
	}

	/**
	 * Add a new variable to the environment with a given symbolic value
	 * @param var
	 * @param symb
	 */
	public void addVarSymbolicValue(String var, SymbolicValue symb) {
		Variable v = new Variable(var);
		symbEnv.getFirst().put(v, symb);
	}

	/**
	 * Add a new field to the environment with a given symbolic value
	 * @param v
	 * @param simpleName
	 * @param vv
	 */
	public void addFieldSymbolicValue(SymbolicValue v, String simpleName, SymbolicValue vv) {
		FieldHeapLoc f = new FieldHeapLoc(v, simpleName);
		symbEnv.getFirst().put(f, vv);
	}

	/**
	 * Add a new field to the environment with a given symbolic value
	 * @param v
	 * @param simpleName
	 * @param vv
	 */
	void add(VariableHeapLoc v, SymbolicValue vv) {
		symbEnv.getFirst().put(v, vv);
	}

	void update(VariableHeapLoc v, SymbolicValue vv) {
		for (Map<VariableHeapLoc, SymbolicValue> map : symbEnv) {
			if (map.containsKey(v)) {
				map.put(v, vv);
				return;
			}
		}
	}

	/**
	 * Returns a fresh symbolic value
	 * @return
	 */
	public SymbolicValue getFresh(){
		return new SymbolicValue(symbolic_counter++);
	}

	/**
	 * Get the symbolic value of the variable with a given name
	 * @param name
	 * @return
	 */
	public SymbolicValue get(String name) {
		return get(new Variable(name));
	}

	/**
	 * Get the symbolic value of the field
	 * @param symbolicValue
	 * @param field
	 * @return
	 */
	public SymbolicValue get(SymbolicValue symbolicValue, String field) {
		return get(new FieldHeapLoc(symbolicValue, field));
	}

	/**
	 * Returns the symbolic value of the variable or symbolic value and field
	 * @param var
	 * @return
	 */
	SymbolicValue get(VariableHeapLoc var) {
		for(Map<VariableHeapLoc, SymbolicValue> map : symbEnv) {
			if (map.containsKey(var)) {
				return map.get(var);
			}
		}
		return null;
	}

	public Set<VariableHeapLoc> keySet(){
		return symbEnv.stream()
				.map(Map::keySet)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	public void remove(VariableHeapLoc key){
		for( Map<VariableHeapLoc, SymbolicValue> map : symbEnv)
			if(map.containsKey(key))
				map.remove(key);
	}

	public boolean hasValue(SymbolicValue v) {
		return symbEnv.stream()
				.map(innerMap -> innerMap.containsValue(v))
				.reduce(false, (a, b) -> a || b);
	}

	public boolean hasFieldWithValue(SymbolicValue v){
		return symbEnv.stream()
				.map(innerMap -> innerMap.entrySet().stream()
						.anyMatch(entry -> entry.getKey() instanceof FieldHeapLoc && entry.getValue().equals(v)))
				.reduce(false, (a, b) -> a || b);
	}


	/**
	 * Remove unreachable values
	 * TODO: Test this
	 * @return	List of removed symbolic values
	 */
	public List<SymbolicValue> removeUnreachableValues() {
		// 1) get all symbolic values in the keys that are part of fields in the heap
		List<FieldHeapLoc> keys = new ArrayList<>();
		List<SymbolicValue> returns = new ArrayList<>();
		for (Map<VariableHeapLoc, SymbolicValue> map : symbEnv) {
			map.keySet().forEach(k -> {
				if (k instanceof FieldHeapLoc) {
					keys.add((FieldHeapLoc)k);
				}
			});
		}

		// 2) for each key, check if the symbolic value is still reachable, if it isn't, remove it
		// and add it to the list of removed values, as well as its symbolic value in the map
		for (FieldHeapLoc key : keys) {
			SymbolicValue v = key.heapLoc;
			if (!hasValue(v)) {
				for (Map<VariableHeapLoc, SymbolicValue> map : symbEnv) {
					returns.add(v);
					returns.add(map.get(key));
					map.remove(key);
				}
			}
		}
		return returns;
	}

	/**
	 * Enter a new scope
	 */
	public void enterScope() {
		symbEnv.addFirst(new HashMap<VariableHeapLoc, SymbolicValue>());
	}
	
	/**
	 * Exit the current scope
	 */
	public void exitScope() {
		symbEnv.removeFirst();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Symbolic Environment:\n");
	
		for (int i = 0; i < symbEnv.size(); i++) {
			Map<VariableHeapLoc, SymbolicValue> map = symbEnv.get(i);
			sb.append("  Map ").append(i + 1).append(":\n");
	
			for (Map.Entry<VariableHeapLoc, SymbolicValue> entry : map.entrySet()) {
				sb.append("    ")
				  .append(entry.getKey().toString()) // Key
				  .append(" -> ")
				  .append(entry.getValue().toString()) // Value
				  .append("\n");
			}
		}
	
		return sb.toString();
	}

	public boolean distinct(List<SymbolicValue> paramSymbValues) {
		if (paramSymbValues.size() < 2) return true;
		List<Pair<SymbolicValue, SymbolicValue>> lp = 
				IntStream.range(0, paramSymbValues.size())
                .boxed()
                .flatMap(i -> IntStream.range(i + 1, paramSymbValues.size())
                        .mapToObj(j -> new Pair<>(paramSymbValues.get(i), paramSymbValues.get(j))))
                .collect(Collectors.toList());
		
		for (Pair<SymbolicValue, SymbolicValue> p : lp) {
			if (canReach(p.getValue0(), p.getValue1(), new ArrayList<>())) 
				return false;
		}
		return true;
	}
	
	/**
	 * Check if v1 can reach v2 recursively, changing v1 to the values that can be reached from 
	 * the fields of v1
	 * @param v1
	 * @param v2
	 * @param visited
	 * @return
	 */
	public boolean canReach(SymbolicValue v1, SymbolicValue v2, List<SymbolicValue> visited) {
		if (visited.contains(v1))
			return false;
		visited.add(v1);
		
		if (v1.equals(v2)) return true;

		// Get all values that can be reached from v1.field
		List<SymbolicValue> reachableFromField = symbEnv.stream()
				.map(innerMap -> innerMap.entrySet().stream()
						.filter(entry -> entry.getKey() instanceof FieldHeapLoc && 
								((FieldHeapLoc) entry.getKey()).heapLoc.equals(v1))
						.map(entry -> entry.getValue())
						.collect(Collectors.toList()))
				.flatMap(List::stream)
				.collect(Collectors.toList());

		for (SymbolicValue v : reachableFromField) {
			return ( false || canReach(v, v2, visited)); 
		}
		return false;
	}

	/**
	 * Clone the symbolic environment at a certain moment in time
	 * @return
	 */
	public SymbolicEnvironment cloneLast() {
		SymbolicEnvironment clone = new SymbolicEnvironment();

        // clone.enterScope();
        // symbEnv.getFirst().forEach((k, v) -> {
        //     clone.add(k, v);
        // });
		for (Map<VariableHeapLoc, SymbolicValue> map : symbEnv) {
			Map<VariableHeapLoc, SymbolicValue> newMap = new HashMap<>();
			map.forEach((k, v) -> {
				newMap.put(k, v);
			});
			clone.symbEnv.add(newMap);
		}
		return clone;
    }

	public boolean contains(VariableHeapLoc v) {
		return symbEnv.stream()
				.map(innerMap -> innerMap.containsKey(v))
				.reduce(false, (a, b) -> a || b);
	}

	public boolean isEmpty() {
		return symbEnv.stream().allMatch(Map::isEmpty);
	}

  }


